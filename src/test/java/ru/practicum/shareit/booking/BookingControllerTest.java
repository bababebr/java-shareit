package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionsHandler;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    BookingService bookingService;

    @InjectMocks
    BookingController controller;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
        mapper.findAndRegisterModules();
    }

    @Test
    void add() {
    }

    @Test
    void approve() {
    }

    @Test
    void get() {
    }

    @Test
    void getUserBookings() {
    }

    @Test
    void getOwnerBookings() {
    }
}