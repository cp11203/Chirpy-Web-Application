package edu.georgetown.bll;


import edu.georgetown.dao.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.georgetown.bll.user.PostService;

import java.util.Vector;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {

    private PostService postService;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = Logger.getLogger(PostServiceTest.class.getName());
        postService = new PostService(logger);
    }

    @Test
    void testAddPost() {
        String username = "testuser";
        String content = "This is a test post";
        String hashtag = "#test";

        try {
            postService.addPost(username, content, hashtag);
        } catch (Exception e) {
            fail("Failed to add post: " + e.getMessage());
        }
    }

    @Test
    void testFetchPosts() {
        try {
            Vector<Post> posts = postService.fetchPosts();
            assertNotNull(posts);
        } catch (Exception e) {
            fail("Failed to fetch posts: " + e.getMessage());
        }
    }

    @Test
    void testFetchFollowingPosts() {
        String currentUsername = "testuser";

        try {
            Vector<Post> posts = postService.fetchFollowingPosts(currentUsername);
            assertNotNull(posts);
        } catch (Exception e) {
            fail("Failed to fetch following posts: " + e.getMessage());
        }
    }

    @Test
    void testFetchPostsByUsername() {
        String currentUsername = "testuser";
        String targetUsername = "targetuser";

        try {
            Vector<Post> posts = postService.fetchPostsByUsername(currentUsername, targetUsername);
            assertNotNull(posts);
        } catch (Exception e) {
            fail("Failed to fetch posts by username: " + e.getMessage());
        }
    }

    @Test
    void testFetchPostsByHashtag() {
        String currentUsername = "testuser";
        String hashtag = "#test";

        try {
            Vector<Post> posts = postService.fetchPostsByHashtag(currentUsername, hashtag);
            assertNotNull(posts);
        } catch (Exception e) {
            fail("Failed to fetch posts by hashtag: " + e.getMessage());
        }
    }
}