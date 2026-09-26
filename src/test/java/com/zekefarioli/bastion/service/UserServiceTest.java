package com.zekefarioli.bastion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.zekefarioli.bastion.model.TicketStatus;
import com.zekefarioli.bastion.model.User;
import com.zekefarioli.bastion.repository.UserRepository;

@DataJpaTest
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void createUserWithNotNullIdAndNotEmptyUserNameAndEmail() {
        UserService service = new UserService(userRepository);

        User user = service.createUser("Name Example", "email@emaildomain.com");

        assertNotNull(user.getId());
        assertNotNull(user.getName());
        assertTrue(user.getName().length() > 0);
        assertNotNull(user.getEmail());
        assertTrue(user.getEmail().length() > 0);
        assertEquals("Name Example", user.getName());
        assertEquals("email@emaildomain.com", user.getEmail());
    }

    @Test
    void findAll_returnsAllCreatedUsers() {

        UserService service = new UserService(userRepository);

        service.createUser("Name Example1", "email1@emaildomain.com");
        service.createUser("Name Example2", "email2@emaildomain.com");
        service.createUser("Name Example3", "email3@emaildomain.com");

        List<User> allUsers = service.findAll();
        assertEquals(3, allUsers.size());
    }

    @Test
    void findUserByIdAndCheckFields() {
        UserService service = new UserService(userRepository);
        User createdUser = service.createUser("Name Example", "email@emaildomain.com");
        User foundUser = service.findById(createdUser.getId());
        assertNotNull(foundUser.getId());
        assertNotNull(foundUser.getName());
        assertTrue(foundUser.getName().length() > 0);
        assertNotNull(foundUser.getEmail());
        assertTrue(foundUser.getEmail().length() > 0);
        assertEquals("Name Example", foundUser.getName());
        assertEquals("email@emaildomain.com", foundUser.getEmail());

    }

    @Test
    void findUserByIdWithWrongId() {
        UserService service = new UserService(userRepository);
        assertThrows(NoSuchElementException.class, () -> {
            service.findById(1L);
        });

    }
}