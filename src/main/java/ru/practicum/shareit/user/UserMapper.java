package ru.practicum.shareit.user;

public class UserMapper {

    public static UserDto userToDto(User user) {
        return UserDto.create(user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static User userDtoToUser(UserDto userDto) {
        return User.create(userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }
}