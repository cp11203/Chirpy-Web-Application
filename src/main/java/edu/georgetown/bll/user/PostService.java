package edu.georgetown.bll.user;

import com.mongodb.client.MongoClients;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import java.util.Vector;
import java.util.logging.Logger;

import edu.georgetown.dao.*;


public class PostService {

    private static Logger logger;
    private MongoCollection<Document> postsCollection;

    public PostService(Logger log) {
        logger = log;
        logger.info("PostService started");

        try {
            // Assuming the use of the same MongoClient settings as UserService
            MongoClient mongoClient = MongoClients.create("mongodb+srv://chirpy_user:advanced_pro@chirpyv1.libuseu.mongodb.net/?retryWrites=true&w=majority&appName=chirpyv1");
            MongoDatabase database = mongoClient.getDatabase("chirpyv1");
            postsCollection = database.getCollection("posts");
            logger.info("Connected to MongoDB at posts collection");
        } catch (Exception e) {
            logger.severe("Could not connect to MongoDB: " + e.getMessage());
        }
    }

    public void addPost(String username, String content, String hashtag) {
        logger.info("Attempting to add post for user: " + username);
        try {
            Document newPost = new Document("username", username)
                                .append("content", content)
                                .append("hashtag", hashtag);
            postsCollection.insertOne(newPost);
            logger.info("Post added successfully for user: " + username);
        } catch (Exception e) {
            logger.severe("Failed to add post for " + username + ": " + e.getMessage());
            // Consider rethrowing the exception or handling it based on your application's needs
        }
    }

    public Vector<Post> fetchPosts() {
        Vector<Post> posts = new Vector<>();
        try {
            FindIterable<Document> documents = postsCollection.find();
            for (Document doc : documents) {
                String username = doc.getString("username");
                String content = doc.getString("content");
                String hashtag = doc.getString("hashtag");
                //boolean isPublic = doc.getBoolean("isPublic", true); // Default to true if not found
                
                posts.add(new Post(username, content, hashtag));
            }
            logger.info("Posts fetched successfully.");
        } catch (Exception e) {
            logger.severe("Failed to fetch posts: " + e.getMessage());
        }

        for (Post post : posts) {
            logger.info("Posted by: " + post.username);
            logger.info("Content: " + post.content);
            logger.info("Hashtag: " + post.hashtag);
            logger.info("------------------------");
        }

        return posts;
    }


     public Vector<Post> fetchPostsByUsername(String username) {
        Vector<Post> posts = new Vector<>();
        try {
            FindIterable<Document> documents = postsCollection.find(Filters.eq("username", username));
            for (Document doc : documents) {
                posts.add(new Post(
                    doc.getString("username"), 
                    doc.getString("content"), 
                    doc.getString("hashtag")
                ));
            }
            logger.info("Posts fetched successfully for username: " + username);
        } catch (Exception e) {
            logger.severe("Failed to fetch posts for username " + username + ": " + e.getMessage());
        }
        return posts;
    }

    // Function to fetch posts by a specific hashtag
    public Vector<Post> fetchPostsByHashtag(String hashtag) {
        Vector<Post> posts = new Vector<>();
        try {
            FindIterable<Document> documents = postsCollection.find(Filters.eq("hashtag", hashtag));
            for (Document doc : documents) {
                posts.add(new Post(
                    doc.getString("username"), 
                    doc.getString("content"), 
                    doc.getString("hashtag")
                ));
            }
            logger.info("Posts fetched successfully for hashtag: " + hashtag);
        } catch (Exception e) {
            logger.severe("Failed to fetch posts for hashtag " + hashtag + ": " + e.getMessage());
        }
        return posts;
    }

    
}

