package com.influcollab.service;

import com.influcollab.entity.User;
import com.influcollab.exception.EmailAlreadyExistsException;
import com.influcollab.exception.UserNotFoundException;
import com.influcollab.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(User user) {

        if (repository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        return repository.save(user);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User updateUser(Long id, User updatedUser) {

        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(updatedUser.getName());
        user.setSurname(updatedUser.getSurname());
        user.setChannelName(updatedUser.getChannelName());
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setCity(updatedUser.getCity());

        return repository.save(user);
    }

    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        repository.delete(user);
    }

}