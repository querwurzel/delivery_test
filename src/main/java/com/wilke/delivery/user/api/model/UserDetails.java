package com.wilke.delivery.user.api.model;

import com.wilke.delivery.user.integration.model.Post;
import com.wilke.delivery.user.integration.model.User;

import java.util.List;

public record UserDetails (
    User user,
    List<Post> posts
) {}
