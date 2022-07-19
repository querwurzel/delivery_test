package com.wilke.delivery.user.integration.model;

public record User (
        long id,
        String name,
        String username,
        String email,
        String phone,
        String website,
        Address address,
        Company company
) {}

