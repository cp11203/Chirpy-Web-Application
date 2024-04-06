package edu.georgetown.bll.user;


import java.util.Vector;
import java.util.logging.Logger;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;

import edu.georgetown.dao.*;

public class UserService {

    private static Logger logger;

    private MongoCollection<Document> usersCollection;

    public UserService(Logger log) {
        logger = log;
        logger.info("UserService started");

        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://chirpy_user:advanced_pro@chirpyv1.libuseu.mongodb.net/?retryWrites=true&w=majority&appName=chirpyv1");
            MongoDatabase database = mongoClient.getDatabase("messsage_mania");
            usersCollection = database.getCollection("m_users");
            logger.info("conncected to mongodb");
        } catch (Exception e) {
            logger.severe("Could not connect to MongoDB: " + e.getMessage());
        }
    }


    // return true if successfuly registered, takes in string of username and password
    public boolean registerUser(String username, String password) {
        logger.info("Attempting to register user: " + username);
    
        try {
            // Create a new user document with isPublic defaulting to true and an empty followers list
            Document newUser = new Document("username", username)
                    .append("password", password)
                    .append("isPublic", true) // Defaults to public
                    .append("following", new Vector<String>()); // Initializes an empty list of followers
    
            usersCollection.insertOne(newUser);
            logger.info("User registered successfully: " + username);
            return true; // Return true if user is successfully registered
        } catch (Exception e) {
            logger.severe("Failed to register user " + username + ": " + e.getMessage());
            // Handle the exception as appropriate for your application
            return false; // Return false if registration fails
        }
    }
    
    

    // returns true if successfully logged in, takes in username and password
    public boolean loginUser(String username, String password) {
        
        Document user = usersCollection.find(Filters.eq("username", username)).first();
        
        if (user != null) {

            String pass = user.getString("password");

            if (pass.equals(password)) {
                logger.info("successful login for " + username);
                return true;
            } else {
                logger.info("failed login for " + username + " due to incorrect password.");
                return false;
            }
        } else {
            logger.info("failed login, user does not exist-  " + username);
            return false;
        }
    }

    /// this function is generated from chat gpt 3.5 provided by OpenAI
    // it fetches all users in the m_users collection, returns vector of user objects
    public Vector<User> getUsers() {
        Vector<User> users = new Vector<>();
        try (MongoCursor<Document> cursor = usersCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String username = doc.getString("username");
                String password = doc.getString("password");
                User user = new User(username, password);
                users.add(user);
            }
        } catch (Exception e) {
            logger.severe("Failed to fetch users: " + e.getMessage());
        }
        return users;
    }

    // this function is generated from chat gpt 3.5 provided by OpenAI
    // this function takes in the current username, and the username this user wants to follow, and adds the latter username to the current user's following list
    public boolean addFollower(String currentUsername, String userToFollow) {
        logger.info("Attempting to add follower for user: " + currentUsername);
        try {
            // Check if the userToFollow exists
            Document targetUser = usersCollection.find(Filters.eq("username", userToFollow)).first();
            if (targetUser == null) {
                logger.info("User to follow not found: " + userToFollow);
                return false; // User to follow does not exist
            }
            
            // If we found the user, proceed to update the "following" array for the current user
            usersCollection.updateOne(
                Filters.eq("username", currentUsername),
                Updates.addToSet("following", userToFollow) // Use addToSet to avoid duplicates
            );
            logger.info("Added " + userToFollow + " to " + currentUsername + "'s following list.");
            return true; // Successfully added follower
        } catch (Exception e) {
            logger.severe("Failed to add follower for " + currentUsername + ": " + e.getMessage());
            // Handle the exception as appropriate for your application
            return false; // An error occurred
        }
    }
    

    // this function allows users to switch their account to private
    public void switchToPrivate(String username) {
        
        logger.info("Switching user to private: " + username);
        try {
            // Update the isPublic field for the specified user to false
            usersCollection.updateOne(
                Filters.eq("username", username),
                Updates.set("isPublic", false)
            );
            logger.info("User successfully switched to private: " + username);
        } catch (Exception e) {
            logger.severe("Failed to switch user to private " + username + ": " + e.getMessage());
            // Handle the exception as appropriate for your application
        }
    }


    

}
