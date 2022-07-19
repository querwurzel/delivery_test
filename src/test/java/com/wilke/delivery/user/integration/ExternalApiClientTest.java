package com.wilke.delivery.user.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilke.delivery.user.UserFixtures;
import com.wilke.delivery.user.integration.model.Post;
import com.wilke.delivery.user.integration.model.User;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExternalApiClientTest {

    MockWebServer mockWebServer;

    ExternalApiClient externalApiClient;

    @BeforeEach
    void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        this.externalApiClient = new ExternalApiClient(this.mockWebServer.url("/").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    void fetchUser_shouldReturn_user_correspondingToUserId() throws JsonProcessingException {
        long givenUserId = 4711;
        User givenUser = givenValidUserResponse(givenUserId);

        final User user = externalApiClient.fetchUser(givenUserId).block();

        assertThat(user).isEqualTo(givenUser);
    }

    @Test
    void fetchUser_shouldThrowException_when_unknownUser() {
        givenUnknownUserResponse();

        assertThatThrownBy(() -> {
            externalApiClient.fetchUser(4711).block();
        }).isInstanceOf(WebClientResponseException.NotFound.class);
    }

    @Test
    void fetchPosts_shouldReturn_posts_correspondingToUserId() throws JsonProcessingException {
        long givenUserId = 4711;
        List<Post> givenPosts = givenPostsForUser(givenUserId);

        final List<Post> posts = externalApiClient.fetchPosts(givenUserId).block();

        assertThat(posts).hasSize(givenPosts.size());
    }

    @Test
    void fetchPosts_shouldReturn_EmptyList_when_unknownUser() {
        givenNoPosts();

        final List<Post> posts = externalApiClient.fetchPosts(4711).block();

        assertThat(posts).isEmpty();
    }

    private User givenValidUserResponse(long userId) throws JsonProcessingException {
        User user = UserFixtures.givenUser(userId);

        // TODO NOOOOOT happy without precise url :(, improve test specificity
        mockWebServer.enqueue(
                new MockResponse()
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(new ObjectMapper().writeValueAsString(user))
        );

        return user;
    }

    private void givenUnknownUserResponse() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.NOT_FOUND.value())
        );
    }

    private List<Post> givenPostsForUser(long userId) throws JsonProcessingException {
        List<Post> posts = UserFixtures.givenPosts(userId);

        // TODO NOOOOOT happy without precise url :(, improve test specificity
        mockWebServer.enqueue(
                new MockResponse()
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(new ObjectMapper().writeValueAsString(posts))
        );

        return posts;
    }

    private void givenNoPosts() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("[]") // TODO NOOOOT happy with API design, discuss with Delivery Team
        );
    }

}