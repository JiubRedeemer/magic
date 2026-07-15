package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellBundleDto;
import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.entity.RoomSpellBundle;
import com.jiubredeemer.magic.entity.SpellBundle;
import com.jiubredeemer.magic.entity.SpellBundlePurchase;
import com.jiubredeemer.magic.entity.SpellBundled;
import com.jiubredeemer.magic.mapper.SpellDtoMapper;
import com.jiubredeemer.magic.repository.RoomSpellBundleRepository;
import com.jiubredeemer.magic.repository.SpellBundlePurchaseRepository;
import com.jiubredeemer.magic.repository.SpellBundleRepository;
import com.jiubredeemer.magic.repository.SpellBundledRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpellBundleService {

    private final SpellBundleRepository bundleRepository;
    private final RoomSpellBundleRepository roomBundleRepository;
    private final SpellBundledRepository bundledRepository;
    private final SpellBundlePurchaseRepository purchaseRepository;
    private final SpellStorageService spellStorageService;
    private final SpellDtoMapper spellDtoMapper;

    public List<SpellBundleDto> getVisibleBundles(UUID userId, String search) {
        List<SpellBundleDto> bundles = bundleRepository.findVisibleForUser(userId).stream()
                .filter(b -> matchesSearch(b, search))
                .map(this::toDto)
                .collect(Collectors.toList());
        markPurchased(userId, bundles);
        return bundles;
    }

    public List<SpellBundleDto> getOwnBundles(UUID userId) {
        return bundleRepository.findByOwnerUserIdOrderByName(userId).stream()
                .map(this::toDto)
                .toList();
    }

    public SpellBundleDto getBundle(UUID id) {
        return toDto(findBundle(id));
    }

    public SpellBundleDto createBundle(UUID ownerUserId, SpellBundleDto dto) {
        SpellBundle bundle = new SpellBundle();
        bundle.setName(dto.getName());
        bundle.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        bundle.setCreatedAt(Instant.now());
        bundle.setImgUrl(dto.getImgUrl());
        bundle.setOwnerUserId(ownerUserId);
        bundle.setIsPublic(Boolean.TRUE.equals(dto.getIsPublic()));
        bundle.setPriceCrystals(dto.getPriceCrystals() != null ? dto.getPriceCrystals() : 0);
        return toDto(bundleRepository.save(bundle));
    }

    public SpellBundleDto updateBundle(UUID id, UUID userId, SpellBundleDto dto) {
        SpellBundle bundle = findBundle(id);
        requireOwner(bundle, userId);
        bundle.setName(dto.getName());
        bundle.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        bundle.setImgUrl(dto.getImgUrl());
        bundle.setIsPublic(Boolean.TRUE.equals(dto.getIsPublic()));
        bundle.setPriceCrystals(dto.getPriceCrystals() != null ? dto.getPriceCrystals() : 0);
        return toDto(bundleRepository.save(bundle));
    }

    @Transactional
    public void deleteBundle(UUID id, UUID userId) {
        SpellBundle bundle = findBundle(id);
        requireOwner(bundle, userId);
        purchaseRepository.deleteBySpellBundleId(id);
        roomBundleRepository.deleteBySpellBundleId(id);
        bundledRepository.deleteBySpellBundleId(id);
        bundleRepository.deleteById(id);
    }

    public List<SpellDto> getBundleSpells(UUID bundleId) {
        findBundle(bundleId);
        return spellDtoMapper.toDtoFromBundled(bundledRepository.findBySpellBundleId(bundleId));
    }

    public SpellDto saveBundleSpell(UUID bundleId, UUID userId, SpellDto dto) {
        SpellBundle bundle = findBundle(bundleId);
        requireOwner(bundle, userId);

        SpellBundled entity;
        if (dto.getId() != null) {
            entity = bundledRepository.findById(dto.getId())
                    .filter(s -> s.getSpellBundleId().equals(bundleId))
                    .orElse(null);
        } else {
            entity = null;
        }
        if (entity == null) {
            entity = spellDtoMapper.toBundled(dto);
            entity.setId(dto.getId() != null ? dto.getId() : UUID.randomUUID());
            entity.setSpellBundleId(bundleId);
            entity.setCreatedAt(Instant.now());
        } else {
            BeanUtils.copyProperties(dto, entity, "id", "createdAt", "creatorId", "spellBundleId");
        }
        entity.setCreatorId(userId);
        if (entity.getName() == null) {
            entity.setName(dto.getName());
        }
        return spellDtoMapper.toDto(bundledRepository.save(entity));
    }

    public void deleteBundleSpell(UUID spellId) {
        bundledRepository.deleteById(spellId);
    }

    @Transactional
    public void importSpells(UUID bundleId, UUID userId, List<UUID> spellIds) {
        SpellBundle bundle = findBundle(bundleId);
        requireOwner(bundle, userId);
        List<SpellBundled> sources = bundledRepository.findAllById(spellIds);
        for (SpellBundled source : sources) {
            SpellBundled copy = new SpellBundled();
            BeanUtils.copyProperties(source, copy);
            copy.setId(UUID.randomUUID());
            copy.setSpellBundleId(bundleId);
            copy.setCreatorId(userId);
            copy.setCreatedAt(Instant.now());
            bundledRepository.save(copy);
        }
    }

    public List<SpellDto> getSpellsCreatedByUser(UUID userId, String search) {
        return bundledRepository.findAll().stream()
                .filter(s -> userId.equals(s.getCreatorId()))
                .filter(s -> matchesSpellSearch(s, search))
                .map(spellDtoMapper::toDto)
                .toList();
    }

    public SpellBundleDto recordPurchase(UUID userId, UUID bundleId) {
        SpellBundle bundle = findBundle(bundleId);
        if (!Boolean.TRUE.equals(bundle.getIsPublic())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bundle is not public");
        }
        if (!purchaseRepository.existsByUserIdAndSpellBundleId(userId, bundleId)) {
            SpellBundlePurchase purchase = new SpellBundlePurchase();
            purchase.setUserId(userId);
            purchase.setSpellBundleId(bundleId);
            purchase.setCreatedAt(Instant.now());
            purchaseRepository.save(purchase);
        }
        SpellBundleDto dto = toDto(bundle);
        dto.setPurchased(true);
        return dto;
    }

    public List<SpellBundleDto> getBundlesForRoom(UUID roomId, UUID userId, String search) {
        Set<UUID> enabledIds = roomBundleRepository.findByRoomId(roomId).stream()
                .map(RoomSpellBundle::getSpellBundleId)
                .collect(Collectors.toSet());
        List<SpellBundleDto> bundles = getVisibleBundles(userId, search);
        bundles.forEach(b -> b.setEnabled(enabledIds.contains(b.getId())));
        return bundles;
    }

    public void enableBundleForRoom(UUID roomId, UUID userId, UUID bundleId) {
        SpellBundle bundle = findBundle(bundleId);
        boolean isSystem = bundle.getOwnerUserId() == null;
        boolean isOwn = userId != null && userId.equals(bundle.getOwnerUserId());
        boolean isFreePublic = Boolean.TRUE.equals(bundle.getIsPublic())
                && (bundle.getPriceCrystals() == null || bundle.getPriceCrystals() <= 0);
        boolean isPurchased = userId != null && purchaseRepository.existsByUserIdAndSpellBundleId(userId, bundleId);
        if (!isSystem && !isOwn && !isFreePublic && !isPurchased) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Bundle must be purchased first");
        }
        if (roomBundleRepository.findByRoomIdAndSpellBundleId(roomId, bundleId).isEmpty()) {
            RoomSpellBundle rb = new RoomSpellBundle();
            rb.setRoomId(roomId);
            rb.setSpellBundleId(bundleId);
            roomBundleRepository.save(rb);
        }
    }

    @Transactional
    public void disableBundleForRoom(UUID roomId, UUID bundleId) {
        roomBundleRepository.deleteByRoomIdAndSpellBundleId(roomId, bundleId);
    }

    /**
     * Каталог заклинаний комнаты: из включённых бандлов (или конкретного бандла при {@code bundleId}).
     * Когда конкретный бандл не выбран, добавляются пользовательские заклинания (spells_user) — как и раньше.
     */
    public List<SpellDto> getRoomSpells(UUID roomId, String spellClass, UUID bundleId) {
        List<UUID> enabledIds = roomBundleRepository.findByRoomId(roomId).stream()
                .map(RoomSpellBundle::getSpellBundleId)
                .toList();

        String code = spellClass == null ? "" : spellClass.trim().toUpperCase();
        boolean byClass = !code.isEmpty();
        String codePrefix = code + ", %";
        String codeSuffix = "%, " + code;
        String codeMiddle = "%, " + code + ", %";

        List<UUID> bundleIds = bundleId != null ? List.of(bundleId) : enabledIds;
        boolean includeUser = bundleId == null;

        List<SpellDto> result = new ArrayList<>();
        if (!bundleIds.isEmpty()) {
            List<SpellBundled> bundled = byClass
                    ? bundledRepository.findBySpellClassInBundles(bundleIds, code, codePrefix, codeSuffix, codeMiddle)
                    : bundledRepository.findBySpellBundleIdInOrderByCreatedAt(bundleIds);
            result.addAll(spellDtoMapper.toDtoFromBundled(bundled));
        }
        if (includeUser) {
            List<com.jiubredeemer.magic.entity.Spell> users = byClass
                    ? spellStorageService.findUserSpellsByClass(code, codePrefix, codeSuffix, codeMiddle)
                    : spellStorageService.findAllUserSpells();
            result.addAll(spellDtoMapper.toDto(users));
        }
        return result;
    }

    private void markPurchased(UUID userId, List<SpellBundleDto> bundles) {
        if (userId == null) {
            return;
        }
        Set<UUID> purchased = purchaseRepository.findByUserId(userId).stream()
                .map(SpellBundlePurchase::getSpellBundleId)
                .collect(Collectors.toSet());
        bundles.forEach(b -> b.setPurchased(purchased.contains(b.getId())));
    }

    private boolean matchesSearch(SpellBundle bundle, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        return bundle.getName() != null
                && bundle.getName().toLowerCase().contains(search.trim().toLowerCase());
    }

    private boolean matchesSpellSearch(SpellBundled spell, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        if (spell.getName() == null) {
            return false;
        }
        String needle = search.trim().toLowerCase();
        return spell.getName().values().stream()
                .anyMatch(v -> v != null && v.toLowerCase().contains(needle));
    }

    private SpellBundle findBundle(UUID id) {
        return bundleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Spell bundle not found"));
    }

    private void requireOwner(SpellBundle bundle, UUID userId) {
        if (bundle.getOwnerUserId() == null || !bundle.getOwnerUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the bundle owner");
        }
    }

    private SpellBundleDto toDto(SpellBundle bundle) {
        SpellBundleDto dto = new SpellBundleDto();
        dto.setId(bundle.getId());
        dto.setName(bundle.getName());
        dto.setDescription(bundle.getDescription());
        dto.setCreatedAt(bundle.getCreatedAt());
        dto.setImgUrl(bundle.getImgUrl());
        dto.setOwnerUserId(bundle.getOwnerUserId());
        dto.setIsPublic(bundle.getIsPublic());
        dto.setPriceCrystals(bundle.getPriceCrystals());
        return dto;
    }
}
