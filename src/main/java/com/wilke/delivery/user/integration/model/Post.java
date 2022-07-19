package com.wilke.delivery.user.integration.model;

public record Post(
        long userId,
        long id,
        String title,
        String body
) {}