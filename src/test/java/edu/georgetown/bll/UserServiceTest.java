package edu.georgetown.bll;


import edu.georgetown.dao.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.georgetown.bll.user.UserService;

import java.util.Vector;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = Logger.getLogger(UserServiceTest.class.getName());
        userService = new UserService(logger);
    }

    @Test
    void testRegisterUser_Success() {
        String username = "testuser";
        String password = "testpassword";

        boolean result = userService.registerUser(username, password);

        assertTrue(result);
    }


    @Test
    void testLoginUser_Success() {
        String username = "testuser";
        String password = "testpassword";

        userService.registerUser(username, password);

        boolean result = userService.loginUser(username, password);

        assertTrue(result);
    }

    @Test
    void testLoginUser_Failure() {
        String username = "testuser";
        String password = "wrongpassword";

        boolean result = userService.loginUser(username, password);

        assertFalse(result);
    }

    @Test
    void testGetUsers() {
        Vector<User> users = userService.getUsers();

        assertNotNull(users);
    }

    @Test
    void testAddFollower_Success() {
        String currentUsername = "testuser";
        String userToFollow = "usertofollow";

        userService.registerUser(currentUsername, "password");
        userService.registerUser(userToFollow, "password");

        boolean result = userService.addFollower(currentUsername, userToFollow);

        assertTrue(result);
    }

    @Test
    void testAddFollower_UserNotFound() {
        String currentUsername = "testuser";
        String userToFollow = "nonexistentuser";

        userService.registerUser(currentUsername, "password");

        boolean result = userService.addFollower(currentUsername, userToFollow);

        assertFalse(result);
    }


}