package com.zekefarioli.bastion.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.zekefarioli.bastion.service.UserService;
import static org.mockito.Mockito.when;
import java.util.List;
import com.zekefarioli.bastion.model.User;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Test
    void getAllUsers_returnsAllUsersAsJson() throws Exception {

        User user1 = new User(1L, "name1", "name1@email.com");
        User user2 = new User(2L, "name2", "name2@email.com");
        when(userService.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[1].email").value("name2@email.com"));
    }

}