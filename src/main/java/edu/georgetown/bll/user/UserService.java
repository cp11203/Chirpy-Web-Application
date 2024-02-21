package edu.georgetown.bll.user;


import java.util.Vector;
import java.util.logging.Logger;

import edu.georgetown.dao.*;

public class UserService {

    private static Logger logger;

    public UserService(Logger log) {
        logger = log;
        logger.info("UserService started");
    }

    // BOILER PLATE FUNCTIONS THAT WE NEED TO IMPLEMENT

    public Vector<Chirper> getUsers() {
        // not implemented; you'll need to change this
        return null;
    }


    public void registerUser(String username, String password) {
       
        System.out.println("Registered user: " + username + " with password: " + password);
    }

    public void loginUser(String username, String password) {
       
        System.out.println("User " + username + " attempted to log in with password: " + password);
        
    }

    

}
