package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
public class User {
    Long id;
    String name;
    String email;
}