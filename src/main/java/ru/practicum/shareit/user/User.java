package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    @Positive
    Long id;
    @NotEmpty(message = "Name cannot be empty.")
    String name;
    @Email(message = "Email is incorrect.")
    @NotNull(message = "Name cannot be null.")
    String email;
}