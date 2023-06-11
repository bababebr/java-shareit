package ru.practicum.shareit.user;

import java.util.List;

interface UserService {

    List<UserDto> getAll();

    UserDto get(long id);

    UserDto create(UserDto user);

    UserDto update(UserDto user, long userId);

    UserDto delete(long userId);
}