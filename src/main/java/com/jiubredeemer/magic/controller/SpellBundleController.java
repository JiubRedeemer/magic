package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellBundleDto;
import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.service.SpellBundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/spells/bundles")
@RequiredArgsConstructor
public class SpellBundleController {

    private final SpellBundleService bundleService;

    @GetMapping("/visible/{userId}")
    public List<SpellBundleDto> getVisibleBundles(@PathVariable UUID userId,
                                                  @RequestParam(required = false) String search) {
        return bundleService.getVisibleBundles(userId, search);
    }

    @GetMapping("/own/{userId}")
    public List<SpellBundleDto> getOwnBundles(@PathVariable UUID userId) {
        return bundleService.getOwnBundles(userId);
    }

    @GetMapping("/{bundleId}")
    public SpellBundleDto getBundle(@PathVariable UUID bundleId) {
        return bundleService.getBundle(bundleId);
    }

    @PostMapping("/{userId}")
    public SpellBundleDto createBundle(@PathVariable UUID userId, @RequestBody SpellBundleDto dto) {
        return bundleService.createBundle(userId, dto);
    }

    @PutMapping("/{bundleId}/{userId}")
    public SpellBundleDto updateBundle(@PathVariable UUID bundleId, @PathVariable UUID userId,
                                       @RequestBody SpellBundleDto dto) {
        return bundleService.updateBundle(bundleId, userId, dto);
    }

    @DeleteMapping("/{bundleId}/{userId}")
    public void deleteBundle(@PathVariable UUID bundleId, @PathVariable UUID userId) {
        bundleService.deleteBundle(bundleId, userId);
    }

    @GetMapping("/{bundleId}/spells")
    public List<SpellDto> getBundleSpells(@PathVariable UUID bundleId) {
        return bundleService.getBundleSpells(bundleId);
    }

    @PutMapping("/{bundleId}/spells/{userId}")
    public SpellDto saveBundleSpell(@PathVariable UUID bundleId, @PathVariable UUID userId,
                                    @RequestBody SpellDto dto) {
        return bundleService.saveBundleSpell(bundleId, userId, dto);
    }

    @DeleteMapping("/spells/{spellId}")
    public void deleteBundleSpell(@PathVariable UUID spellId) {
        bundleService.deleteBundleSpell(spellId);
    }

    @PostMapping("/{bundleId}/import/{userId}")
    public void importSpells(@PathVariable UUID bundleId, @PathVariable UUID userId,
                             @RequestBody List<UUID> spellIds) {
        bundleService.importSpells(bundleId, userId, spellIds);
    }

    @GetMapping("/creator-spells/{userId}")
    public List<SpellDto> getSpellsCreatedByUser(@PathVariable UUID userId,
                                                 @RequestParam(required = false) String search) {
        return bundleService.getSpellsCreatedByUser(userId, search);
    }

    @PostMapping("/{bundleId}/purchase/{userId}")
    public SpellBundleDto recordPurchase(@PathVariable UUID bundleId, @PathVariable UUID userId) {
        return bundleService.recordPurchase(userId, bundleId);
    }

    @GetMapping("/rooms/{roomId}/{userId}")
    public List<SpellBundleDto> getBundlesForRoom(@PathVariable UUID roomId, @PathVariable UUID userId,
                                                  @RequestParam(required = false) String search) {
        return bundleService.getBundlesForRoom(roomId, userId, search);
    }

    @GetMapping("/rooms/{roomId}/spells")
    public List<SpellDto> getRoomSpells(@PathVariable UUID roomId,
                                        @RequestParam(required = false) String spellClass,
                                        @RequestParam(required = false) UUID bundleId) {
        return bundleService.getRoomSpells(roomId, spellClass, bundleId);
    }

    @PutMapping("/rooms/{roomId}/{userId}/{bundleId}")
    public void enableBundleForRoom(@PathVariable UUID roomId, @PathVariable UUID userId,
                                    @PathVariable UUID bundleId) {
        bundleService.enableBundleForRoom(roomId, userId, bundleId);
    }

    @DeleteMapping("/rooms/{roomId}/{bundleId}")
    public void disableBundleForRoom(@PathVariable UUID roomId, @PathVariable UUID bundleId) {
        bundleService.disableBundleForRoom(roomId, bundleId);
    }
}
