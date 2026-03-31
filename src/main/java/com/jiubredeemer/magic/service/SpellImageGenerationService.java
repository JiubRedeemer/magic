package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.image.ImageGenerationResultDto;
import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.entity.SpellAi;
import com.jiubredeemer.magic.mapper.SpellDtoMapper;
import com.jiubredeemer.magic.repository.SpellAiRepository;
import com.jiubredeemer.magic.repository.SpellRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Service
public class SpellImageGenerationService {

    private static final Logger log = LoggerFactory.getLogger(SpellImageGenerationService.class);

    private final SpellAiRepository spellAiRepository;
    private final SpellRepository spellRepository;
    private final SpellDtoMapper spellDtoMapper;
    private final WebClient webClient;

    @Value("${magic.images.sd-txt2img-url:http://127.0.0.1:7860/sdapi/v1/txt2img}")
    private String sdTxt2imgUrl;

    @Value("${magic.images.upload-url:https://localhost:8079/files/spell-images/upload}")
    private String imageUploadUrl;

    @Value("${magic.images.prompt-format:fantasy RPG spell icon, %s, %s}")
    private String promptFormat;

    @Value("${magic.images.steps:20}")
    private int steps;

    @Value("${magic.images.width:512}")
    private int width;

    @Value("${magic.images.height:512}")
    private int height;

    @Value("${magic.images.seed:-1}")
    private int seed;

    @Value("${magic.images.sampler-name:euler}")
    private String samplerName;

    @Value("${magic.images.scheduler:Simple}")
    private String scheduler;

    public SpellImageGenerationService(SpellAiRepository spellAiRepository,
                                       SpellRepository spellRepository,
                                       SpellDtoMapper spellDtoMapper,
                                       WebClient webClient) {
        this.spellAiRepository = spellAiRepository;
        this.spellDtoMapper = spellDtoMapper;
        this.spellRepository = spellRepository;
        this.webClient = webClient;
    }

    /**
     * Loads a row from {@code magic.spell_ai} by id, runs txt2img, uploads PNG, updates {@code img_url}.
     * Intended for n8n after the workflow has chosen a spell id.
     */
    public SpellDto generateAndSaveImageForSpellAi(UUID id) {
        SpellAi spellAi = spellAiRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "spell_ai not found: " + id));
        try {
            String nameEng = spellNameForPrompt(spellAi);
            String descriptionForPrompt = descriptionForImagePrompt(spellAi);
            log.info("Generating image for spell_ai id={}, name={}", spellAi.getId(), nameEng);

            String prompt = promptFormat.formatted(nameEng, descriptionForPrompt);

            ImageGenerationResultDto imageGenerationResult = webClient.post()
                    .uri(sdTxt2imgUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(Map.of(
                            "prompt", prompt,
                            "steps", steps,
                            "width", width,
                            "height", height,
                            "seed", seed,
                            "sampler_name", samplerName,
                            "scheduler", scheduler))
                    .retrieve()
                    .bodyToMono(ImageGenerationResultDto.class)
                    .block();

            if (imageGenerationResult == null
                    || imageGenerationResult.getImages() == null
                    || imageGenerationResult.getImages().isEmpty()) {
                log.error("SD txt2img returned no images for spell_ai {}", spellAi.getId());
                throw new ResponseStatusException(BAD_GATEWAY, "Stable Diffusion returned no image");
            }

            byte[] png = Base64.getDecoder().decode(imageGenerationResult.getImages().getFirst());

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", png)
                    .filename("%s.png".formatted(spellAi.getId()))
                    .contentType(MediaType.IMAGE_PNG);
            builder.part("userFilename", spellAi.getId().toString());

            String filename = webClient.put()
                    .uri(imageUploadUrl)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            spellAi.setImgUrl(filename);
            SpellAi saved = spellAiRepository.save(spellAi);

            Spell spell = new Spell();
            BeanUtils.copyProperties(saved, spell);

            Spell spell1 = spellRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "spell_ai not found: " + id));
            spell1.setImgUrl(filename);
            spellRepository.save(spell1);

            return spellDtoMapper.toDto(spell);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception ex) {
            log.error("Cannot create image for spell_ai id={}", id, ex);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Image generation failed: " + ex.getMessage());
        }
    }

    /**
     * Prefer manually stored English text; otherwise fall back to {@code description}.
     */
    private static String descriptionForImagePrompt(SpellAi spellAi) {
        if (spellAi.getEngDescription() != null && !spellAi.getEngDescription().isBlank()) {
            return spellAi.getEngDescription();
        }
        return spellAi.getDescription() != null ? spellAi.getDescription() : "";
    }

    private static String spellNameForPrompt(SpellAi spell) {
        if (spell.getName() == null || spell.getName().isEmpty()) {
            return "";
        }
        var m = spell.getName();
        if (m.containsKey("eng")) {
            return m.get("eng");
        }
        if (m.containsKey("en")) {
            return m.get("en");
        }
        if (m.containsKey("rus")) {
            return m.get("rus");
        }
        return m.values().iterator().next();
    }
}
