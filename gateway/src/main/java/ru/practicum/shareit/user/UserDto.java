package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
    @NotBlank(message = "Name cannot be null or empty.")
    String name;
    @Email(message = "Email is incorrect.")
    @NotBlank(message = "Email cannot be null or empty.")
    String email;
}