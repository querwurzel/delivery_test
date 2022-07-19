package com.wilke.delivery.user.api;

import com.wilke.delivery.user.api.model.UserDetails;
import com.wilke.delivery.user.application.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(UserController.PATH)
public class UserController {

    static final String PATH = "/api/users";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    protected Mono<UserDetails> getUserDetails(@PathVariable("userId") Long userId) {
        return this.userService.fetchUserDetails(userId);
    }
}
