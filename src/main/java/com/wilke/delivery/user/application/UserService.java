package com.wilke.delivery.user.application;

import com.wilke.delivery.user.api.exception.ServiceUnavailableException;
import com.wilke.delivery.user.api.exception.UserNotFoundException;
import com.wilke.delivery.user.api.model.UserDetails;
import com.wilke.delivery.user.integration.ExternalApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final ExternalApiClient externalApiClient;

    @Autowired
    public UserService(ExternalApiClient externalApiClient1) {
        this.externalApiClient = externalApiClient1;
    }

    public Mono<UserDetails> fetchUserDetails(long userId) {
        return Mono.zip(
                this.externalApiClient.fetchUser(userId),
                this.externalApiClient.fetchPosts(userId),
                UserDetails::new
        )
                .onErrorMap(WebClientResponseException.NotFound.class, notFound -> new UserNotFoundException())
                .onErrorMap(WebClientResponseException.class, _any -> new ServiceUnavailableException());
    }
}
