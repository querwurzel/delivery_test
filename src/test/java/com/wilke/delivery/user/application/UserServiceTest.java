package com.wilke.delivery.user.application;

import com.wilke.delivery.user.UserFixtures;
import com.wilke.delivery.user.api.exception.ServiceUnavailableException;
import com.wilke.delivery.user.api.exception.UserNotFoundException;
import com.wilke.delivery.user.api.model.UserDetails;
import com.wilke.delivery.user.integration.ExternalApiClient;
import com.wilke.delivery.user.integration.model.Post;
import com.wilke.delivery.user.integration.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private ExternalApiClient externalApiClient;

    @InjectMocks
    private UserService userService;

    @Test
    void fetchUserDetails_shouldReturn_fullUserDetails() {
        final long userId = 4711;
        final User user = this.givenValidUser(userId);
        final List<Post> posts = this.givenPostsByUser(userId);

        UserDetails userDetails = this.userService.fetchUserDetails(userId).block();

        assertThat(userDetails.user()).isEqualTo(user);
        assertThat(userDetails.posts()).isEqualTo(posts);
    }

    @Test
    void fetchUserDetails_shouldThrow_when_userNotFound() {
        givenUnknownUser();

        assertThatThrownBy(() -> {
            this.userService.fetchUserDetails(4711).block();
        }).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void fetchUserDetails_shouldThrow_when_serviceFails() {
        givenServiceFails();

        assertThatThrownBy(() -> {
            this.userService.fetchUserDetails(4711).block();
        }).isInstanceOf(ServiceUnavailableException.class);
    }

    User givenValidUser(long userId) {
        User user = UserFixtures.givenUser(userId);

        Mockito.doReturn(Mono.just(user)).when(this.externalApiClient).fetchUser(Mockito.eq(userId));
        return user;
    }

    void givenUnknownUser() {
        Mockito
                .doReturn(
                        Mono.error(
                                WebClientResponseException.create(
                                        HttpStatus.NOT_FOUND.value(),
                                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                                        new HttpHeaders(),
                                        new byte[0],
                                        Charset.defaultCharset()
                                )
                        )
                )
                .when(this.externalApiClient).fetchUser(Mockito.any(Long.class));

        Mockito.doReturn(Mono.just(Collections.emptyList())).when(this.externalApiClient).fetchPosts(Mockito.any(Long.class));
    }

    List<Post> givenPostsByUser(long userId) {
        List<Post> posts = UserFixtures.givenPosts(userId);

        Mockito.doReturn(Mono.just(posts)).when(this.externalApiClient).fetchPosts(Mockito.eq(userId));
        return posts;
    }

    void givenServiceFails() {
        WebClientResponseException error = WebClientResponseException.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                new HttpHeaders(),
                new byte[0],
                Charset.defaultCharset()
        );

        Mockito
                .doReturn(Mono.error(error))
                .when(this.externalApiClient).fetchUser(Mockito.any(Long.class));

        Mockito
                .doReturn(Mono.error(error))
                .when(this.externalApiClient).fetchPosts(Mockito.any(Long.class));
    }

}
