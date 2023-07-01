package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    @InjectMocks
    ItemRequestServiceImpl requestService;
    @Mock
    RequestRepository mockRequestRepository;
    @Mock
    ItemRepository mockItemRepository;
    @Mock
    UserRepository mockUserRepository;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private Item item;
    private User user;
    private User owner;
    private LocalDateTime startFuture;
    private LocalDateTime startPast;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        requestService = new ItemRequestServiceImpl(mockRequestRepository, mockItemRepository, mockUserRepository);
        user = User.create(1L, "user", "user@mail.ru");
        owner = User.create(2L, "owner", "owner@mail.ru");
        item = Item.create(1L, owner, true, "Item 1", "Item", null);
        startFuture = LocalDateTime.now().plusHours(1);
        startPast = LocalDateTime.now().minusHours(1);
        end = LocalDateTime.now().plusHours(2);
        itemRequestDto = ItemRequestDto.create(1L, "request 1", startFuture, new ArrayList<>());
        itemRequest = ItemRequest.create(1L, "request 1", startFuture, user, item);
    }

    @Test
    void addItemUserNotFound() {
        when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("Not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> requestService.addItem(itemRequestDto, user.getId()));
        assertEquals("Not found", e.getMessage());
    }

    @Test
    void addItem() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(mockRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto dto = requestService.addItem(itemRequestDto, user.getId());
        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getCreated(), dto.getCreated());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
    }

    @Test
    void getUsersAllUserNotFound() {
        when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("Not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> requestService.getUsersAll(user.getId(), 0, 10));
        assertEquals("Not found", e.getMessage());
    }

    @Test
    void getUsersAllFromNegative() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> requestService.getUsersAll(user.getId(), -10, 10));
        assertEquals("Page index must not be less than zero", e.getMessage());
    }

    @Test
    void getUsersAllSizeNegative() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> requestService.getUsersAll(user.getId(), 10, -10));
        assertEquals("Page size must not be less than one", e.getMessage());
    }

    @Test
    void getUsersAllSizeAndFromZeros() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> requestService.getUsersAll(user.getId(), 0, 0));
        assertEquals("Page size must not be less than one", e.getMessage());
    }

    @Test
    void getUsersAllEmpty() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(mockRequestRepository.findAllByRequesterId(anyLong(), any(Pageable.class)))
                .thenReturn(new ArrayList<>());
        List list = requestService.getUsersAll(user.getId(), 1, 10);
        assertEquals(list.size(), 0);
    }

    @Test
    void getUsersAllItemIsNull() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(mockRequestRepository.findAllByRequesterId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        itemRequest.setItem(null);
        List<ItemRequestDto> list = requestService.getUsersAll(user.getId(), 0, 10);
        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getItems().isEmpty(), true);
        assertEquals(list.get(0).getId(), itemRequest.getId());
        assertEquals(list.get(0).getDescription(), itemRequest.getDescription());
        assertEquals(list.get(0).getCreated(), itemRequest.getCreated());
    }

    @Test
    void getUsersAllIteNotNull() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(mockRequestRepository.findAllByRequesterId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        List<ItemRequestDto> list = requestService.getUsersAll(user.getId(), 0, 10);
        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getItems().isEmpty(), false);
        assertEquals(list.get(0).getId(), itemRequest.getId());
        assertEquals(list.get(0).getDescription(), itemRequest.getDescription());
        assertEquals(list.get(0).getCreated(), itemRequest.getCreated());
    }


    @Test
    void getUserNotFound() {
        when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("Not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> requestService.get(user.getId(), itemRequestDto.getId()));
        assertEquals("Not found", e.getMessage());
    }

    @Test
    void getItemNotFound() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(mockRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(mockItemRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("Request has not found."));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> requestService.get(user.getId(), itemRequestDto.getId()));
        assertEquals("Request has not found.", e.getMessage());
    }

    @Test
    void get() {
        itemRequestDto.getItems().add(item);
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(mockRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        ItemRequestDto request = requestService.get(user.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getItems(), request.getItems());
        assertEquals(itemRequestDto.getId(), request.getId());
        assertEquals(itemRequestDto.getCreated(), request.getCreated());
        assertEquals(itemRequestDto.getDescription(), request.getDescription());
    }

    @Test
    void getOtherRequestFromNegative() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> requestService.getOtherRequest(user.getId(), -1, 10));
        assertEquals("Page index must not be less than zero", e.getMessage());
    }

    @Test
    void getOtherRequestSizeNegative() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> requestService.getOtherRequest(user.getId(), 0, -1));
        assertEquals("Page size must not be less than one", e.getMessage());
    }

    @Test
    void getOtherRequestSizAndFromZeros() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> requestService.getOtherRequest(user.getId(), 0, 0));
        assertEquals("Page size must not be less than one", e.getMessage());
    }

    @Test
    void getOtherIsNull() {
        when(mockRequestRepository.findAllByRequesterIdIsNot(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        itemRequest.setItem(null);
        List<ItemRequestDto> itemRequestDtos = requestService.getOtherRequest(user.getId(), 0, 10);
        assertEquals(itemRequestDtos.get(0).getItems().isEmpty(), true);
    }

    @Test
    void getOther() {
        when(mockRequestRepository.findAllByRequesterIdIsNot(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        List<ItemRequestDto> itemRequestDtos = requestService.getOtherRequest(user.getId(), 0, 10);
        assertEquals(itemRequestDtos.get(0).getItems().size(), 1);
    }
}