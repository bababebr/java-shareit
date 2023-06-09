package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class CommentMapper {

    public static CommentDTO commentToDto(Comment comment, User user) {
        return CommentDTO.create(comment.getId(),
                comment.getText(),
                user.getName(),
                comment.getCreated());
    }

    public static Comment DtoToComment(CommentDTO commentDTO, Item item, User author) {
        return Comment.create(commentDTO.getId(),
                commentDTO.getText(),
                item.getId(),
                author.getId(),
                commentDTO.getCreated());
    }

}
