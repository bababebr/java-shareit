package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoSuchObjectException;

import java.util.List;

@Service
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDto> getAll() {
        return UserMapper.userToDto(repository.findAll());
    }

    @Override
    public UserDto get(long userId) {
        User user = repository.findById(userId).orElseThrow(()
                -> new NoSuchObjectException(String.format("User with ID=%s not found", userId)));
        return UserMapper.userToDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = repository.save(UserMapper.userDtoToUser(userDto));
        return UserMapper.userToDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, long userId) {
        User user = repository.findById(userId).get();
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        repository.save(user);
        return UserMapper.userToDto(user);
    }

    @Override
    public UserDto delete(long userId) {
        UserDto userDto = get(userId);
        repository.deleteById(userId);
        return userDto;
    }
}