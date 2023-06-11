package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDTO commentToDto(Comment comment, User user) {
        return CommentDTO.create(comment.getId(),
                comment.getText(),
                user.getName(),
                comment.getCreated());
    }

    public static Comment dtoToComment(CommentDTO commentDTO, Item item, User author) {
        return Comment.create(commentDTO.getId(),
                commentDTO.getText(),
                item.getId(),
                author.getId(),
                LocalDateTime.now());
    }
}
