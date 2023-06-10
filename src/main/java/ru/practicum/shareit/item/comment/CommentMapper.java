package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDTO commentToDto(Comment comment, User user) {
        return CommentDTO.create(comment.getId(),
                comment.getText(),
                user.getName(),
                comment.getCreated());
    }

    public static List<CommentDTO> commentsToDtos(Collection<Comment> comments, User user) {
        return comments.stream().map(c -> commentToDto(c, user)).collect(Collectors.toList());
    }

    public static Comment DtoToComment(CommentDTO commentDTO, Item item, User author) {
        return Comment.create(commentDTO.getId(),
                commentDTO.getText(),
                item.getId(),
                author.getId(),
                commentDTO.getCreated());
    }
}
