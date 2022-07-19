package com.wilke.delivery.user.api;

import com.wilke.delivery.user.UserFixtures;
import com.wilke.delivery.user.api.exception.UserNotFoundException;
import com.wilke.delivery.user.api.model.UserDetails;
import com.wilke.delivery.user.application.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(UserController.class)
class UserControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    UserService userService;

    @Test
    void getUserDetails_shouldReturn_userDetails() {
        long userId = 4711;
        UserDetails details = this.givenUserDetails(userId);

        webTestClient
                .get()
                .uri(UserController.PATH + "/{userId}", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDetails.class).isEqualTo(details);
    }

    @Test
    void getUserDetails_shouldReturn_NotFound_whenUserNotFound() {
        this.givenNoUserDetails();

        webTestClient
                .get()
                .uri(UserController.PATH + "/{userId}", 4711)
                .exchange()
                .expectStatus().isNotFound();
    }

    private UserDetails givenUserDetails(long userId) {
        UserDetails details = new UserDetails(
                UserFixtures.givenUser(userId),
                UserFixtures.givenPosts(userId)
        );

        Mockito.doReturn(Mono.just(details)).when(userService).fetchUserDetails(Mockito.eq(userId));
        return details;
    }

    private void givenNoUserDetails() {
        Mockito.doReturn(Mono.error(new UserNotFoundException())).when(userService).fetchUserDetails(Mockito.any(Long.class));
    }

}