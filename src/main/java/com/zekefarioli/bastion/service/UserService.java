package com.zekefarioli.bastion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.zekefarioli.bastion.model.User;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();
    private long nextId = 1;

    public User createUser(String name, String email) {
        User user = new User(nextId, name, email);
        nextId = nextId + 1;
        users.add(user);
        return user;
    }

    public List<User> findAll() {
        return users;
    }

    public User findById(long id) {
        User user = users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
        return user;
    }
}
