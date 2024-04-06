package edu.georgetown.bll.user;

import com.mongodb.client.MongoClients;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.georgetown.dao.*;


public class PostService {

    private static Logger logger;
    private MongoCollection<Document> postsCollection;
    private MongoCollection<Document> usersCollection;

    public PostService(Logger log) {
        logger = log;
        logger.info("PostService started");

        try {
            // Assuming the use of the same MongoClient settings as UserService
            MongoClient mongoClient = MongoClients.create("mongodb+srv://chirpy_user:advanced_pro@chirpyv1.libuseu.mongodb.net/?retryWrites=true&w=majority&appName=chirpyv1");
            MongoDatabase database = mongoClient.getDatabase("messsage_mania");
            postsCollection = database.getCollection("m_posts");
            usersCollection = database.getCollection("m_users");
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

    // fetch all posts
    // returns vector of posts
    public Vector<Post> fetchPosts() {
        Vector<Post> posts = new Vector<>();
        try {
            FindIterable<Document> documents = postsCollection.find();
            for (Document doc : documents) {
                String username = doc.getString("username");
                String content = doc.getString("content");
                String hashtag = doc.getString("hashtag");
                
                posts.add(new Post(username, content, hashtag));
            }
            logger.info("Posts fetched successfully.");
        } catch (Exception e) {
            logger.severe("Failed to fetch posts: " + e.getMessage());
        }


        return posts;
    }

    // fetch posts that are posted by user the current user follow

public Vector<Post> fetchFollowingPosts(String currentUsername) {
    Vector<Post> posts = new Vector<>();
    try {
        Document userDoc = usersCollection.find(Filters.eq("username", currentUsername)).first();
        if (userDoc != null) {
            // Get the following list and convert it to Vector if necessary
            List<String> followingListTmp = userDoc.getList("following", String.class);
            Vector<String> followingList = new Vector<>(followingListTmp); // Convert List to Vector

            // find all posts where the username is in the current user's following list
            Bson postsQuery = Filters.in("username", followingList);
            FindIterable<Document> documents = postsCollection.find(postsQuery);

            for (Document doc : documents) {
                String username = doc.getString("username");
                String content = doc.getString("content");
                String hashtag = doc.getString("hashtag");

                posts.add(new Post(username, content, hashtag));
            }
            logger.info("Following posts fetched successfully for " + currentUsername);
        } else {
            logger.info("User not found: " + currentUsername);
        }
    } catch (Exception e) {
        logger.severe("Failed to fetch following posts for " + currentUsername + ": " + e.getMessage());
    }
    return posts;
}



    // then fetch all posts of people you follow
    // ADD THIS

    // change both of these 

    // if private, can only see if you follow them 
    public Vector<Post> fetchPostsByUsername(String currentUsername, String targetUsername) {
        Vector<Post> posts = new Vector<>();
        try {
            // Check the privacy setting of the user whose posts are being fetched
            Document targetUserDoc = usersCollection.find(Filters.eq("username", targetUsername)).first();
            if (targetUserDoc != null) {
                boolean isPublic = targetUserDoc.getBoolean("isPublic", true);
                
                // Fetch the "following" list for the current user
                Document currentUserDoc = usersCollection.find(Filters.eq("username", currentUsername)).first();
                List<String> followingListTmp = currentUserDoc != null ? currentUserDoc.getList("following", String.class) : List.of();
                Vector<String> followingList = new Vector<>(followingListTmp); // Convert List to Vector for compatibility
                
                // Determine if the target user's posts can be fetched
                boolean canFetchPosts = isPublic || followingList.contains(targetUsername);
                
                if (canFetchPosts) {
                    FindIterable<Document> documents = postsCollection.find(Filters.eq("username", targetUsername));
                    for (Document doc : documents) {
                        posts.add(new Post(
                            doc.getString("username"), 
                            doc.getString("content"), 
                            doc.getString("hashtag")
                            // Ensure your Post constructor can handle these fields directly
                        ));
                    }
                    logger.info("Posts fetched successfully for username: " + targetUsername);
                } else {
                    logger.info("Unable to fetch posts: " + targetUsername + "'s profile is private and current user is not following.");
                }
            } else {
                logger.info("User not found: " + targetUsername);
            }
        } catch (Exception e) {
            logger.severe("Failed to fetch posts for username " + targetUsername + ": " + e.getMessage());
        }
        return posts;
    }
    

    // Function to fetch posts by a specific hashtag
    public Vector<Post> fetchPostsByHashtag(String currentUsername, String hashtag) {
    Vector<Post> posts = new Vector<>();
    try {
        Bson hashtagFilter = Filters.eq("hashtag", hashtag);
        FindIterable<Document> documents = postsCollection.find(hashtagFilter);

        // Fetch the "following" list for the current user
        Document currentUserDoc = usersCollection.find(Filters.eq("username", currentUsername)).first();
        List<String> followingListTmp = currentUserDoc != null ? currentUserDoc.getList("following", String.class) : List.of();
        Vector<String> followingList = new Vector<>(followingListTmp); // Convert List to Vector for compatibility

        for (Document doc : documents) {
            String postUsername = doc.getString("username");

            // Check the privacy setting of the user who posted
            Document postUserDoc = usersCollection.find(Filters.eq("username", postUsername)).first();
            if (postUserDoc != null) {
                boolean isPublic = postUserDoc.getBoolean("isPublic", true);
                // If the post is from a public user or a private user the current user is following, add the post
                if (isPublic || followingList.contains(postUsername)) {
                    posts.add(new Post(
                        postUsername, 
                        doc.getString("content"), 
                        doc.getString("hashtag")
                        // Ensure your Post constructor can handle these fields directly
                    ));
                }
            }
        }
        logger.info("Posts fetched successfully for hashtag: " + hashtag);
    } catch (Exception e) {
        logger.severe("Failed to fetch posts for hashtag " + hashtag + ": " + e.getMessage());
    }
    return posts;
}
    
    
}

