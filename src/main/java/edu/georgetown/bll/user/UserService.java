package edu.georgetown.bll.user;


import java.util.Vector;
import java.util.logging.Logger;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

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
            MongoDatabase database = mongoClient.getDatabase("chirpyv1");
            usersCollection = database.getCollection("users");
            logger.info("conncected to mongodb");
        } catch (Exception e) {
            logger.severe("Could not connect to MongoDB: " + e.getMessage());
        }
    }


    public void registerUser(String username, String password) {
        logger.info("Attempting to register user: " + username);
        try {
            Document newUser = new Document("username", username).append("password", password);
            usersCollection.insertOne(newUser);
            logger.info("User registered successfully: " + username);
        } catch (Exception e) {
            logger.severe("Failed to register user " + username + ": " + e.getMessage());
            // Consider rethrowing the exception or handling it based on your application's needs
        }
    }
    


    public boolean loginUser(String username, String password) {
        
        Document user = usersCollection.find(Filters.eq("username", username)).first();
        
        if (user != null) {

            String pass = user.getString("password");

            if (pass.equals(password)) {
                logger.info("succeasful login for " + username);
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
    

}
