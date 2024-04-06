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
    void addPostSuccessfullyAddsPost() {

        String username = "testUser";
        String content = "This is a test post";
        String hashtag = "#test";

        Exception exception = null;
        try {
            postService.addPost(username, content, hashtag);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception, "addPost should not throw an exception");

    
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
    public void testFetchPostsByUsernameUserNotFound() {
        String currentUsername = "testuser";
        String targetUsername = "nonexistentuser";
        
        Exception exception = assertThrows(Exception.class, () -> {
            postService.fetchPostsByUsername(currentUsername, targetUsername);
        });

        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
        public void testFetchPostsByHashtag() {
        String currentUsername = "testuser";
        String hashtag = "#test";
        Vector<Post> posts = null;

        try {
            posts = postService.fetchPostsByHashtag(currentUsername, hashtag);

        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
     }

        assertNull(posts, "The returned vector should not be null");
}



    
    
}