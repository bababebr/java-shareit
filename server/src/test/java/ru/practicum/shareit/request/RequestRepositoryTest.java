package java.ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RequestRepositoryTest {

    @Mock
    private RequestRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User booker;
    private User booker2;
    private Item item;
    private LocalDateTime created;
    private ItemRequest request;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now();
        booker = User.create(1L, "booker", "booker@mail.ru");
        booker2 = User.create(4L, "booker2", "booker2@mail.ru");
        item = Item.create(1L, booker, true, "desc", "item 1", null);
        request = ItemRequest.create(1L, "request 1", created, booker, item);
        request2 = ItemRequest.create(2L, "request 2", created, booker2, item);
    }

    @Test
    void addRequest() {
        Mockito.when(repository.save(request))
                .thenReturn(request);
        ItemRequest returnedRequest = repository.save(request);
        assertEquals(request.getId(), returnedRequest.getId());
        assertEquals(request.getDescription(), returnedRequest.getDescription());
        assertEquals(request.getRequester().getId(), returnedRequest.getRequester().getId());
        assertEquals(request.getItem().getId(), returnedRequest.getItem().getId());
    }

    @Test
    void findAllByRequesterId() {
        Mockito.when(repository.findAllByRequesterId(booker.getId(), Pageable.unpaged()))
                .thenReturn(List.of(request));
        List<ItemRequest> requestList = repository.findAllByRequesterId(booker.getId(), Pageable.unpaged());
        assertEquals(requestList.size(), 1);
    }

    @Test
    void findAllByRequesterIdIsNot() {
        Mockito.when(repository.findAllByRequesterIdIsNot(booker.getId(), Pageable.unpaged()))
                .thenReturn(List.of(request));
        List<ItemRequest> requestList = repository.findAllByRequesterIdIsNot(booker.getId(), Pageable.unpaged());
        assertEquals(requestList.size(), 1);
    }
}