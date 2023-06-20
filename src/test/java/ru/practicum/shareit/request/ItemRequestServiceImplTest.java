package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ItemsAvailabilityException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    ItemRequestServiceImpl requestService;
    @Mock
    RequestRepository requestRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private Booking booking;
    private Booking bookingCangelled;
    private Booking bookingApproved;
    private Booking bookingRejected;
    private BookingDto bookingDto;
    private Item item;
    private User user;
    private User owner;
    private LocalDateTime startFuture;
    private LocalDateTime startPast;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        requestService = new ItemRequestServiceImpl(requestRepository, itemRepository, userRepository);
        user = User.create(1L, "user", "user@mail.ru");

        owner = User.create(2L, "owner", "owner@mail.ru");
        item = Item.create(1L, owner, true, "Item 1", "Item", null);
        startFuture = LocalDateTime.now().plusHours(1);
        startPast = LocalDateTime.now().minusHours(1);
        end = LocalDateTime.now().plusHours(2);
        booking = Booking.create(1L, item, user, startFuture, end, BookingStatus.WAITING);
        bookingApproved = Booking.create(2L, item, user, startPast, end, BookingStatus.APPROVED);
        bookingCangelled = Booking.create(3L, item, user, startPast, end, BookingStatus.CANCELLED);
        bookingRejected = Booking.create(4L, item, user, startPast, startPast.plusMinutes(3), BookingStatus.REJECTED);
        bookingDto = BookingDto.create(1L, item.getId(), item, user, startFuture, end, BookingStatus.WAITING);

        itemRequestDto = ItemRequestDto.create(1L, "request 1", startFuture, new ArrayList<>(), user.getId());
        itemRequest = ItemRequest.create(1L, "request 1", startFuture, user.getId(), item.getId());
    }

    @Test
    void addItemUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("Not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> requestService.addItem(itemRequestDto, user.getId()));
        assertEquals("Not found", e.getMessage());
    }

    @Test
    void addItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto dto = requestService.addItem(itemRequestDto, user.getId());
        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getCreated(), dto.getCreated());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
        assertEquals(itemRequest.getUserId(), dto.getUserId());
    }

    @Test
    void getUsersAllUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("Not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> requestService.getUsersAll(user.getId(), 0, 10));
        assertEquals("Not found", e.getMessage());
    }

    @Test
    void getUsersAllSizeMinus2() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        List list = requestService.getUsersAll(user.getId(), -2, 10);
        assertEquals(list.size(), 0);
    }

    @Test
    void getUsersAllFromNegative() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> requestService.getUsersAll(user.getId(), -10, 10));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getUsersAllSizeNegative() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> requestService.getUsersAll(user.getId(), 10, -10));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getUsersAllSizeAndFromZeros() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> requestService.getUsersAll(user.getId(), 0, 0));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getUsersAllEmpty() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(requestRepository.findAllByUserId(anyLong()))
                .thenReturn(new ArrayList<>());
        List list = requestService.getUsersAll(user.getId(), 1, 10);
        assertEquals(list.size(), 0);
    }

    @Test
    void getUsersAllItemIsNull() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(requestRepository.findAllByUserId(anyLong()))
                .thenReturn(List.of(itemRequest));
        itemRequest.setItemId(null);
        List<ItemRequestDto> list = requestService.getUsersAll(user.getId(), 0, 10);
        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getItems().isEmpty(), true);
        assertEquals(list.get(0).getId(), itemRequest.getId());
        assertEquals(list.get(0).getUserId(), itemRequest.getUserId());
        assertEquals(list.get(0).getDescription(), itemRequest.getDescription());
        assertEquals(list.get(0).getCreated(), itemRequest.getCreated());
    }

    @Test
    void getUsersAllIteNotNull() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(requestRepository.findAllByUserId(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        List<ItemRequestDto> list = requestService.getUsersAll(user.getId(), 0, 10);
        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getItems().isEmpty(), false);
        assertEquals(list.get(0).getId(), itemRequest.getId());
        assertEquals(list.get(0).getUserId(), itemRequest.getUserId());
        assertEquals(list.get(0).getDescription(), itemRequest.getDescription());
        assertEquals(list.get(0).getCreated(), itemRequest.getCreated());
    }


    @Test
    void getUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("Not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> requestService.get(user.getId(), itemRequestDto.getId()));
        assertEquals("Not found", e.getMessage());
    }

    @Test
    void getItemNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("Request has not found."));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> requestService.get(user.getId(), itemRequestDto.getId()));
        assertEquals("Request has not found.", e.getMessage());
    }

    @Test
    void get() {
        itemRequestDto.getItems().add(item);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        ItemRequestDto request = requestService.get(user.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getItems(), request.getItems());
        assertEquals(itemRequestDto.getId(), request.getId());
        assertEquals(itemRequestDto.getCreated(), request.getCreated());
        assertEquals(itemRequestDto.getDescription(), request.getDescription());
        assertEquals(itemRequestDto.getUserId(), request.getUserId());
    }

    @Test
    void getOtherRequestFromMinus2() {
        List list = requestService.getOtherRequest(user.getId(), -2, 10);
        assertEquals(list.size(), 0);
    }

    @Test
    void getOtherRequestFromNegative() {
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> requestService.getOtherRequest(user.getId(), -1, 10));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getOtherRequestSizeNegative() {
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> requestService.getOtherRequest(user.getId(), 0, -1));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getOtherRequestSizAndFromZeros() {
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> requestService.getOtherRequest(user.getId(), 0, 0));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getOther() {
        when(requestRepository.findAllForOtherUsers(anyLong()))
                .thenReturn(List.of(itemRequest));
        itemRequest.setItemId(null);
        List<ItemRequestDto> itemRequestDtos = requestService.getOtherRequest(user.getId(), 0, 10);
        assertEquals(itemRequestDtos.get(0).getItems().isEmpty(), true);
    }
}