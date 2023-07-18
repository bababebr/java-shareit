package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class UserDto {
    @Positive
    Long id;
    String name;
    String email;
}