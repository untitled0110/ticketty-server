package com.ticketty.tickettyapp.controller.user;

import com.ticketty.tickettyapp.service.user.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

}
