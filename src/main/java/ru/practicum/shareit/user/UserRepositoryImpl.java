package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NoSuchObjectException;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Resource(name = "memoryUserRepository")
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, User> userHashMap = new HashMap<>();
    private long id = 1;

    @Override
    public List<UserDto> getAll() {
        return userHashMap.values()
                .stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDto(long userId) {
        Optional<User> userOpt = Optional.ofNullable(userHashMap.get(userId));
        if (userOpt.isPresent()) {
            return UserMapper.userToDto(userOpt.get());
        }
        throw new NoSuchObjectException(String.format("Пользователя с ID=%s не существует", userId));
    }

    @Override
    public User getUser(long userId) {
        Optional<User> userOpt = Optional.ofNullable(userHashMap.get(userId));
        if (userOpt.isPresent()) {
            return userOpt.get();
        }
        throw new NoSuchObjectException(String.format("Пользователя с ID=%s не существует", userId));
    }

    @Override
    public UserDto create(UserDto userDto) {
        userDto.setId(id++);
        User user = UserMapper.userDtoToUser(userDto);
        userHashMap.put(userDto.getId(), user);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto, long userId) {

        if (userHashMap.containsKey(userId)) {
            User oldUser = userHashMap.get(userId);
            oldUser.setName(userDto.getName() == null ? oldUser.getName() : userDto.getName());
            oldUser.setEmail(userDto.getEmail() == null ? oldUser.getEmail() : userDto.getEmail());
            userHashMap.replace(userId, oldUser);
            return UserMapper.userToDto(oldUser);
        }
        throw new NoSuchObjectException(String.format("Неудалось обновить пользователя. " +
                "Пользователя с ID=%s не существует.", userDto.getId()));
    }

    @Override
    public UserDto delete(long userId) {
        Optional<User> userOptional = Optional.ofNullable(userHashMap.remove(userId));
        if (userOptional.isPresent()) {
            return UserMapper.userToDto(userOptional.get());
        }
        throw new NoSuchObjectException(String.format("Неудалось удалить пользователя. " +
                "Пользователя с ID=%s не существует.", userId));
    }

    public boolean isUserExist(long userId) {
        return userHashMap.containsKey(userId);
    }
}
