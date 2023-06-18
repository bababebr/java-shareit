package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class UserDto {
    @Positive
    Long id;
    @NotBlank(message = "Name cannot be null or empty.")
    String name;
    @Email(message = "Email is incorrect.")
    @NotBlank(message = "Email cannot be null or empty.")
    String email;
}