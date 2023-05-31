package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    private final Set<String> emailHashSet = new HashSet<>();

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDto> getAll() {
        return repository.getAll();
    }

    @Override
    public UserDto get(long userId) {
        return repository.getUserDto(userId);
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (isSameDoNotEmailExist(userDto)) {
            emailHashSet.add(userDto.getEmail());
            return repository.create(userDto);
        }
        throw new DuplicationException("Пользователь с таким e-mail уже существует.");
    }

    @Override
    public UserDto update(UserDto userDto, long userId) {
        userDto.setId(userId);
        if (isSameDoNotEmailExist(userDto)) {
            emailHashSet.remove(repository.getUser(userId).getEmail());
            emailHashSet.add(userDto.getEmail());
            return repository.update(userDto, userId);
        } else if (userDto.getEmail().equals(repository.getUser(userId).getEmail())) {
            return repository.update(userDto, userId);
        }
        throw new DuplicationException("Пользователь с таким e-mail уже существует.");
    }

    @Override
    public UserDto delete(long userId) {
        UserDto deletedUserDto = repository.delete(userId);
        emailHashSet.remove(deletedUserDto.getEmail());
        return deletedUserDto;
    }

    private boolean isSameDoNotEmailExist(UserDto user) {
        return !emailHashSet.contains(user.getEmail());
    }
}