package com.wilke.delivery.user.integration;

import com.wilke.delivery.user.integration.model.Post;
import com.wilke.delivery.user.integration.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class ExternalApiClient {

    static final String USERS = "/users";
    static final String POSTS = "/posts";

    private final WebClient webClient;

    public ExternalApiClient(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public Mono<User> fetchUser(long userId) {
        return this.webClient
                .get()
                .uri(USERS + "/{id}", userId)
                .retrieve()
                .bodyToMono(User.class);
    }

    public Mono<List<Post>> fetchPosts(long userId) {
        return this.webClient
                .get()
                .uri(POSTS + "?userId={userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
