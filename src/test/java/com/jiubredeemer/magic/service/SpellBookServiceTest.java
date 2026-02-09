package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellBookDto;
import com.jiubredeemer.magic.dto.spellbook.SpellBookItemDto;
import com.jiubredeemer.magic.entity.SpellBook;
import com.jiubredeemer.magic.entity.SpellBookItem;
import com.jiubredeemer.magic.entity.SpellCell;
import com.jiubredeemer.magic.mapper.SpellBookDtoMapper;
import com.jiubredeemer.magic.mapper.SpellBookItemDtoMapper;
import com.jiubredeemer.magic.mapper.SpellCellDtoMapper;
import com.jiubredeemer.magic.mapper.SpellDtoMapper;
import com.jiubredeemer.magic.repository.SpellBookItemRepository;
import com.jiubredeemer.magic.repository.SpellBookRepository;
import com.jiubredeemer.magic.repository.SpellCellRepository;
import com.jiubredeemer.magic.repository.SpellRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpellBookServiceTest {

    @Mock
    SpellBookRepository spellBookRepository;

    @Mock
    SpellBookDtoMapper spellBookDtoMapper;

    @Mock
    SpellBookItemRepository spellBookItemRepository;

    @Mock
    SpellBookItemDtoMapper spellBookItemDtoMapper;

    @Mock
    SpellCellRepository spellCellRepository;

    @Mock
    SpellCellDtoMapper spellCellDtoMapper;

    @Mock
    SpellRepository spellRepository;

    @Mock
    SpellDtoMapper spellDtoMapper;

    @InjectMocks
    SpellBookService spellBookService;

    private static SpellBookDto dto(UUID id) {
        SpellBookDto dto = new SpellBookDto();
        dto.setId(id);
        dto.setCharacterId(UUID.randomUUID());
        dto.setRoomId(UUID.randomUUID());
        dto.setManaMax(100L);
        dto.setManaCurrent(100L);
        return dto;
    }

    private static SpellBook entity(UUID id) {
        SpellBook e = new SpellBook();
        e.setId(id);
        e.setCharacterId(UUID.randomUUID());
        e.setRoomId(UUID.randomUUID());
        e.setManaMax(100L);
        e.setManaCurrent(100L);
        return e;
    }

    @Test
    void create_mapsDtoToEntity_savesAndReturnsDto() {
        SpellBookDto request = dto(null);
        SpellBook entity = entity(null);
        SpellBook saved = entity(UUID.randomUUID());
        SpellBookDto baseDto = dto(saved.getId());

        when(spellBookDtoMapper.toEntity(request)).thenReturn(entity);
        when(spellBookRepository.save(entity)).thenReturn(saved);
        when(spellBookDtoMapper.toDto(saved)).thenReturn(baseDto);
        when(spellBookItemRepository.findAllBySpellBookId(saved.getId())).thenReturn(List.of());
        when(spellCellRepository.findAllBySpellBookId(saved.getId())).thenReturn(List.of());

        SpellBookDto result = spellBookService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(saved.getId());
        verify(spellBookRepository).save(entity);
    }

    @Test
    void getById_returnsDtoWhenFound() {
        UUID id = UUID.randomUUID();
        SpellBook entity = entity(id);
        SpellBookDto baseDto = dto(id);

        when(spellBookRepository.findById(id)).thenReturn(Optional.of(entity));
        when(spellBookDtoMapper.toDto(entity)).thenReturn(baseDto);
        when(spellBookItemRepository.findAllBySpellBookId(id)).thenReturn(List.of());
        when(spellCellRepository.findAllBySpellBookId(id)).thenReturn(List.of());

        SpellBookDto result = spellBookService.getById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(spellBookRepository).findById(id);
    }

    @Test
    void list_returnsAllAsDtos() {
        SpellBook entity = entity(UUID.randomUUID());
        SpellBookDto baseDto = dto(entity.getId());

        when(spellBookRepository.findAll()).thenReturn(List.of(entity));
        when(spellBookDtoMapper.toDto(entity)).thenReturn(baseDto);
        when(spellBookItemRepository.findAllBySpellBookId(entity.getId())).thenReturn(List.of());
        when(spellCellRepository.findAllBySpellBookId(entity.getId())).thenReturn(List.of());

        List<SpellBookDto> result = spellBookService.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(entity.getId());
        verify(spellBookRepository).findAll();
    }

    @Test
    void update_mapsDtoToEntity_savesAndReturnsDto() {
        UUID id = UUID.randomUUID();
        SpellBookDto request = dto(id);
        SpellBook entity = entity(id);
        SpellBookDto baseDto = dto(id);

        when(spellBookRepository.findById(id)).thenReturn(Optional.of(entity));
        when(spellBookRepository.save(any(SpellBook.class))).thenReturn(entity);
        when(spellBookDtoMapper.toDto(entity)).thenReturn(baseDto);
        when(spellBookItemRepository.findAllBySpellBookId(id)).thenReturn(List.of());
        when(spellCellRepository.findAllBySpellBookId(id)).thenReturn(List.of());

        SpellBookDto result = spellBookService.update(id, request);

        assertThat(result).isNotNull();
        verify(spellBookDtoMapper).updateEntity(eq(request), eq(entity));
        verify(spellBookRepository).save(entity);
    }

    @Test
    void delete_callsRepository() {
        UUID id = UUID.randomUUID();

        spellBookService.delete(id);

        verify(spellBookRepository).deleteById(id);
    }

    @Test
    void findSpellBookByRoomIdAndCharacterId_returnsExisting() {
        UUID roomId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        SpellBook entity = entity(UUID.randomUUID());
        SpellBookDto baseDto = dto(entity.getId());

        when(spellBookRepository.findByRoomIdAndCharacterId(roomId, characterId)).thenReturn(Optional.of(entity));
        when(spellBookDtoMapper.toDto(entity)).thenReturn(baseDto);
        when(spellBookItemRepository.findAllBySpellBookId(entity.getId())).thenReturn(List.of());
        when(spellCellRepository.findAllBySpellBookId(entity.getId())).thenReturn(List.of());

        SpellBookDto result = spellBookService.findSpellBookByRoomIdAndCharacterId(roomId, characterId);

        assertThat(result).isNotNull();
        verify(spellBookRepository).findByRoomIdAndCharacterId(roomId, characterId);
        verify(spellBookRepository, never()).save(any());
    }

    @Test
    void findSpellBookByRoomIdAndCharacterId_createsWhenNotFound() {
        UUID roomId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        SpellBook newEntity = new SpellBook();
        newEntity.setCharacterId(characterId);
        newEntity.setRoomId(roomId);
        SpellBook saved = entity(UUID.randomUUID());
        saved.setRoomId(roomId);
        saved.setCharacterId(characterId);
        SpellBookDto baseDto = dto(saved.getId());

        when(spellBookRepository.findByRoomIdAndCharacterId(roomId, characterId)).thenReturn(Optional.empty());
        when(spellBookRepository.save(any(SpellBook.class))).thenReturn(saved);
        when(spellBookDtoMapper.toDto(saved)).thenReturn(baseDto);
        when(spellBookItemRepository.findAllBySpellBookId(saved.getId())).thenReturn(List.of());
        when(spellCellRepository.findAllBySpellBookId(saved.getId())).thenReturn(List.of());

        SpellBookDto result = spellBookService.findSpellBookByRoomIdAndCharacterId(roomId, characterId);

        assertThat(result).isNotNull();
        verify(spellBookRepository).save(any(SpellBook.class));
    }

    @Test
    void addSpellToBook_createsItemWhenNotPresent_returnsSpellBook() {
        UUID spellBookId = UUID.randomUUID();
        UUID spellId = UUID.randomUUID();
        SpellBook entity = entity(spellBookId);
        SpellBookItem newItem = new SpellBookItem();
        newItem.setSpellBookId(spellBookId);
        newItem.setSpellId(spellId);
        newItem.setInUse(false);
        SpellBookDto baseDto = dto(spellBookId);

        when(spellBookRepository.findById(spellBookId)).thenReturn(Optional.of(entity));
        when(spellBookItemRepository.findBySpellBookIdAndSpellId(spellBookId, spellId)).thenReturn(Optional.empty());
        when(spellBookItemRepository.save(any(SpellBookItem.class))).thenReturn(newItem);
        when(spellBookDtoMapper.toDto(entity)).thenReturn(baseDto);
        when(spellBookItemRepository.findAllBySpellBookId(spellBookId)).thenReturn(List.of(newItem));
        when(spellCellRepository.findAllBySpellBookId(spellBookId)).thenReturn(List.of());

        SpellBookDto result = spellBookService.addSpellToBook(spellBookId, spellId);

        assertThat(result).isNotNull();
        verify(spellBookItemRepository).save(argThat(item ->
                item.getSpellBookId().equals(spellBookId) && item.getSpellId().equals(spellId) && Boolean.FALSE.equals(item.getInUse())
        ));
    }

    @Test
    void removeSpellFromBook_deletesItemAndReturnsSpellBook() {
        UUID spellBookId = UUID.randomUUID();
        UUID spellId = UUID.randomUUID();
        SpellBook entity = entity(spellBookId);
        SpellBookDto baseDto = dto(spellBookId);

        when(spellBookRepository.findById(spellBookId)).thenReturn(Optional.of(entity));
        when(spellBookDtoMapper.toDto(entity)).thenReturn(baseDto);
        when(spellBookItemRepository.findAllBySpellBookId(spellBookId)).thenReturn(List.of());
        when(spellCellRepository.findAllBySpellBookId(spellBookId)).thenReturn(List.of());

        SpellBookDto result = spellBookService.removeSpellFromBook(spellBookId, spellId);

        assertThat(result).isNotNull();
        verify(spellBookItemRepository).deleteBySpellBookIdAndSpellId(spellBookId, spellId);
    }

    @Test
    void setSpellInUse_updatesItemAndReturnsDto() {
        UUID spellBookId = UUID.randomUUID();
        UUID spellId = UUID.randomUUID();
        SpellBookItem item = new SpellBookItem();
        item.setId(UUID.randomUUID());
        item.setSpellBookId(spellBookId);
        item.setSpellId(spellId);
        item.setInUse(false);
        SpellBookItemDto itemDto = new SpellBookItemDto();
        itemDto.setInUse(true);

        when(spellBookItemRepository.findBySpellBookIdAndSpellId(spellBookId, spellId)).thenReturn(Optional.of(item));
        when(spellBookItemRepository.save(item)).thenReturn(item);
        when(spellBookItemDtoMapper.toDto(item)).thenReturn(itemDto);

        SpellBookItemDto result = spellBookService.setSpellInUse(spellBookId, spellId, true);

        assertThat(result).isNotNull();
        assertThat(result.getInUse()).isTrue();
        verify(spellBookItemRepository).save(argThat(e -> Boolean.TRUE.equals(e.getInUse())));
    }
}
