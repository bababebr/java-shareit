package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "create")
@RequiredArgsConstructor
public class CommentDTO {
    Long id;
    String text;
    String authorName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.nnnn'Z'")
    LocalDateTime created;
}
