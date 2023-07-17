package ru.practicum.shareit.validation.validator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class BookingValidatorTest {

    @Test
    void isValidFalse() {
        LocalDateTime end = LocalDateTime.now().minusHours(1);
        LocalDateTime start = LocalDateTime.now();
        Boolean result = true;
        if (end.isBefore(start) || start.isEqual(end)) {
            result = false;
            assertEquals(result, false);
        }
        assertEquals(result, false);
    }

    @Test
    void isValid() {
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        LocalDateTime start = LocalDateTime.now();
        Boolean result = true;
        if (end.isBefore(start) || start.isEqual(end)) {
            result = false;
            assertEquals(result, false);
        }
        assertEquals(result, true);
    }
}