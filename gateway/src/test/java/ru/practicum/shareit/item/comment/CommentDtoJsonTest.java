package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDTO> json;

    @Test
    void create() throws IOException {
        LocalDateTime created = LocalDateTime.now();
        User user = User.create(1L, "user 1", "email1");
        CommentDTO commentDto = CommentDTO.create(1L, "comment", user.getName(), created);
        JsonContent<CommentDTO> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("user 1");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnn'Z'")));
    }
}