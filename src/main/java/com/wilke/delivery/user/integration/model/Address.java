package com.wilke.delivery.user.integration.model;

public record Address(
        String street,
        String suite,
        String city,
        String zipcode,
        GeoPoint geo
) {}
