package com.zekefarioli.bastion.service;

import com.zekefarioli.bastion.model.User;
import com.zekefarioli.bastion.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(user.getName().length()>0);
        assertNotNull(user.getEmail());
        assertTrue(user.getEmail().length()>0);
        assertEquals("Name Example",user.getName());
        assertEquals("email@emaildomain.com",user.getEmail());
    }

    
}