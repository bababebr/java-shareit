package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto userToDto(User user) {
        return UserDto.create(user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static List<UserDto> userToDto(Collection<User> users) {
        return users.stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }

    public static User userDtoToUser(UserDto userDto) {
        return User.create(userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }

    public static List<User> userDtoToUser(Collection<UserDto> userDtos) {
        return userDtos.stream().map(UserMapper::userDtoToUser).collect(Collectors.toList());
    }
}