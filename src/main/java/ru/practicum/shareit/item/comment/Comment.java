package ru.practicum.shareit.item.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "create")
@RequiredArgsConstructor(staticName = "create")
public class Comment {
    @NotNull
    Long id;
    @NotNull
    @Length(max = 256)
    String text;
    @NotNull
    Long item_id;
    @NotNull
    Long author_id;
}
