package ru.practicum.shareit.itemTest;

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
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingHistoryDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.item.model.ItemServiceImpl;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    ItemServiceImpl itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    RequestRepository requestRepository;
    private Item item1;
    private Item item2;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private Booking bookingItem1;
    private Booking bookingItem2;
    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, requestRepository);
        owner = User.create(1L, "owner", "owner@mail.ru");
        booker = User.create(2L, "booker", "booker@mail.ru");
        item1 = Item.create(1L, owner, true, "item 1", "item 1", null);
        item2 = Item.create(2L, booker, true, "item 2", "item 2", null);
        itemDto1 = ItemMapper.itemToDto(item1);
        itemDto2 = ItemMapper.itemToDto(item2);
        bookingItem1 = Booking.create(1L, item1, booker, LocalDateTime.now(), LocalDateTime.now(),
                BookingStatus.APPROVED);
        bookingItem2 = Booking.create(2L, item2, owner, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED);
    }

    @Test
    void addItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);
        ItemDto returnItem = itemService.addItem(itemDto1, owner.getId());
        assertEquals(returnItem.getId(), itemDto1.getId());
        assertEquals(returnItem.getAvailable(), itemDto1.getAvailable());
        assertEquals(returnItem.getDescription(), itemDto1.getDescription());
        assertEquals(returnItem.getRequestId(), itemDto1.getRequestId());
        assertEquals(returnItem.getName(), itemDto1.getName());
    }

    @Test
    void addItemWithOwnerNotExist() {
        when(userRepository.findById(anyLong()))
                .thenThrow(NoSuchObjectException.class);
        final NoSuchObjectException exception = assertThrows(
                NoSuchObjectException.class,
                () -> itemService.addItem(itemDto1, owner.getId()));
    }

    @Test
    void addItemNull() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(null);
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> itemService.addItem(itemDto1, owner.getId()));
    }

    @Test
    void updateItem() {
        when(itemRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);
        ItemDto returnItem = itemService.updateItem(itemDto1, owner.getId(), itemDto1.getId());
        assertEquals(returnItem.getId(), itemDto1.getId());
        assertEquals(returnItem.getAvailable(), itemDto1.getAvailable());
        assertEquals(returnItem.getDescription(), itemDto1.getDescription());
        assertEquals(returnItem.getRequestId(), itemDto1.getRequestId());
        assertEquals(returnItem.getName(), itemDto1.getName());
    }

    @Test
    void updateItemNotExist() {
        when(itemRepository.existsById(anyLong()))
                .thenReturn(false);
        final NoSuchObjectException exception = assertThrows(
                NoSuchObjectException.class,
                () -> itemService.updateItem(itemDto1, owner.getId(), itemDto1.getId()));
    }

    @Test
    void getItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        ItemBookingHistoryDto returnItem = itemService.getItem(itemDto1.getId(), itemDto1.getId());
        assertEquals(returnItem.getId(), itemDto1.getId());
        assertEquals(returnItem.getAvailable(), itemDto1.getAvailable());
        assertEquals(returnItem.getDescription(), itemDto1.getDescription());
        assertEquals(returnItem.getRequestId(), itemDto1.getRequestId());
        assertEquals(returnItem.getName(), itemDto1.getName());
    }

    @Test
    void getItemNotFound() {
        when(itemRepository.findById(1L))
                .thenThrow(new NoSuchObjectException(String.format("Item with ID=%s not found", itemDto1.getId())));
        final NoSuchObjectException exception = assertThrows(
                NoSuchObjectException.class,
                () -> itemService.getItem(itemDto1.getId(), itemDto1.getId()));
        assertEquals(exception.getMessage(), "Item with ID=1 not found");
    }

    @Test
    void getItemWithNextBookings() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findByItem_IdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(bookingItem1));
        ItemBookingHistoryDto returnItem = itemService.getItem(itemDto1.getId(), itemDto1.getId());
        assertNotNull(returnItem.getNextBooking());
    }

    @Test
    void getUsersOwnItems() {
        when(itemRepository.findItemsByOwner(owner.getId()))
                .thenReturn(List.of(item1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        ArrayList<ItemBookingHistoryDto> items = new ArrayList<>(itemService.getUsersOwnItems(owner.getId()));
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), itemDto1.getId());
        assertEquals(items.get(0).getAvailable(), itemDto1.getAvailable());
        assertEquals(items.get(0).getDescription(), itemDto1.getDescription());
        assertEquals(items.get(0).getRequestId(), itemDto1.getRequestId());
        assertEquals(items.get(0).getName(), itemDto1.getName());
    }

    @Test
    void getUsersOwnItemsNotFound() {
        when(itemRepository.findItemsByOwner(owner.getId()))
                .thenThrow(new NoSuchObjectException(String.format("Item with ID=%s not found", itemDto1.getId())));
        final NoSuchObjectException exception = assertThrows(
                NoSuchObjectException.class,
                () -> itemService.getUsersOwnItems(owner.getId()));
        assertEquals(exception.getMessage(), "Item with ID=1 not found");
    }

    @Test
    void searchItemByDescription() {
        when(itemRepository.findItemByNameAndDescription(anyString()))
                .thenReturn(List.of(item1, item2));
        ArrayList<ItemDto> itemDtos = new ArrayList<>(itemService.searchItemByDescription("item"));
        assertEquals(itemDtos.size(), 2);
    }

    @Test
    void addComment() {
        CommentDTO commentDto = CommentDTO.create(1L, "booking 1", booker.getName(), LocalDateTime.now().minusHours(1));
        bookingItem1.setStart(LocalDateTime.now().minusHours(1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findByBookerAndItem(booker.getId(), item1.getId(), BookingStatus.APPROVED))
                .thenReturn(List.of(bookingItem1));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(CommentMapper.dtoToComment(commentDto, item1, booker));

        CommentDTO returnDto = itemService.addComment(item1.getId(), booker.getId(), commentDto);

        assertEquals(commentDto.getId(), returnDto.getId());
        assertEquals(commentDto.getAuthorName(), returnDto.getAuthorName());
        assertEquals(commentDto.getCreated(), returnDto.getCreated());
        assertEquals(commentDto.getText(), returnDto.getText());
    }

    @Test
    void addCommentBookingsNotStarted() {
        CommentDTO commentDto = CommentDTO.create(1L, "booking 1", booker.getName(), LocalDateTime.now().minusHours(1));
        bookingItem1 = Booking.create(1L, item1, booker, LocalDateTime.now().plusHours(1), LocalDateTime.now(),
                BookingStatus.APPROVED);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findByBookerAndItem(booker.getId(), item1.getId(), BookingStatus.APPROVED))
                .thenReturn(List.of(bookingItem1));

        final CommentException exception = assertThrows(
                CommentException.class,
                () -> itemService.addComment(item1.getId(), booker.getId(), commentDto));
        assertEquals(exception.getClass(), CommentException.class);
    }

    @Test
    void addCommentBookingsWrongStatus() {
        CommentDTO commentDto = CommentDTO.create(1L, "booking 1", booker.getName(), LocalDateTime.now().minusHours(1));
        bookingItem1 = Booking.create(1L, item1, booker, LocalDateTime.now().minusHours(1), LocalDateTime.now(),
                BookingStatus.WAITING);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findByBookerAndItem(booker.getId(), item1.getId(), BookingStatus.APPROVED))
                .thenReturn(new ArrayList<>());

        final CommentException exception = assertThrows(
                CommentException.class,
                () -> itemService.addComment(item1.getId(), booker.getId(), commentDto));
        assertEquals(exception.getClass(), CommentException.class);
    }
}
