package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<UserDto> getAll();

    UserDto getUserDto(long userId);

    User getUser(long userId);

    UserDto create(UserDto user);

    UserDto update(UserDto user, long userId);

    UserDto delete(long userId);

    boolean isUserExist(long userId);
}