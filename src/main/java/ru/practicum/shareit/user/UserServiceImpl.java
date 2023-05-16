package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicationException;

import java.util.List;

@Service
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDTO> getAll() {
        return repository.getAll();
    }

    @Override
    public UserDTO get(long userId) {
        return repository.get(userId);
    }

    @Override
    public UserDTO create(User user) {
        if (isSameEmailExist(user)) {
            return repository.create(user);
        }
        throw new DuplicationException("Пользователь с таким e-mail уже существует.");
    }

    @Override
    public UserDTO update(User user, long userId) {
        user.setId(userId);
        if (isSameEmailExist(user)) {
            return repository.update(user, userId);
        }
        throw new DuplicationException("Пользователь с таким e-mail уже существует.");
    }

    @Override
    public UserDTO delete(long userId) {
        return repository.delete(userId);
    }

    private boolean isSameEmailExist(User user) {
        return repository.getAll().stream()
                .noneMatch(u -> u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId()));
    }
}