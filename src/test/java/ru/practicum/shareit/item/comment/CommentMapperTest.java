package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class CommentMapperTest {
    private User user;
    private User user2;
    private Item item;
    private Comment comment;
    private CommentDTO commentDTO;
    private LocalDateTime created = LocalDateTime.now();
    @BeforeEach
    void setUp() {
        user = User.create(1L, "user", "user@email.ru");
        user2 = User.create(2L, "user2", "user2@email.ru");
        item = Item.create(1L, user, true, "desc", "name", 1L);
        commentDTO = CommentDTO.create(1L, "text", user.getName(), created);
        comment = Comment.create(1L, "text", item, user, created);
    }

    @Test
    void commentToDto() {
        CommentDTO dto = CommentMapper.commentToDto(comment, user);
        assertEquals(dto.getText(), comment.getText());
        assertEquals(dto.getId(), comment.getId());
        assertEquals(dto.getCreated(), comment.getCreated());
        assertEquals(dto.getAuthorName(), comment.getAuthor().getName());
    }

    @Test
    void dtoToComment() {
        Comment c = CommentMapper.dtoToComment(commentDTO, item, user);
        assertEquals(c.getText(), commentDTO.getText());
        assertEquals(c.getId(), commentDTO.getId());
        assertEquals(c.getAuthor().getName(), commentDTO.getAuthorName());
        assertEquals(c.getItem().getName(), item.getName());
    }
}