package com.wilke.delivery.user;

import com.wilke.delivery.user.integration.model.Address;
import com.wilke.delivery.user.integration.model.Company;
import com.wilke.delivery.user.integration.model.GeoPoint;
import com.wilke.delivery.user.integration.model.Post;
import com.wilke.delivery.user.integration.model.User;

import java.util.Arrays;
import java.util.List;

public class UserFixtures {

    public static User givenUser() {
        return givenUser(4711);
    }

    public static User givenUser(long userId) {
        User user = new User(
                userId,
                "Foo",
                "Foobar",
                "mail@example.org",
                "030 4711",
                "example.org",
                new Address("sampleStreet", "sampleSuite", "sampleCity", "12345", new GeoPoint(4711, 4711)),
                new Company("sampleCompany", "sampleCatchPhrase", "bs?")
        );

        return user;
    }

    public static List<Post> givenPosts() {
        return givenPosts(4711);
    }

    public static List<Post> givenPosts(long userId) {
        int postId = 0;

        return Arrays.asList(
                new Post(userId, ++postId, "sample title", "sample body"),
                new Post(userId, ++postId, "sample title", "sample body")
        );
    }

}
