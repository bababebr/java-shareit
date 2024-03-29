package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NoSuchObjectException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getAll() {
        return UserMapper.userToDto(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(long userId) {
        User user = repository.findById(userId).orElseThrow(()
                -> new NoSuchObjectException(String.format("User with ID=%s not found", userId)));
        return UserMapper.userToDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = repository.save(UserMapper.userDtoToUser(userDto));
        return UserMapper.userToDto(user);
    }

    @Override
    @Transactional
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
    @Transactional
    public UserDto delete(long userId) {
        UserDto userDto = get(userId);
        repository.deleteById(userId);
        return userDto;
    }
}