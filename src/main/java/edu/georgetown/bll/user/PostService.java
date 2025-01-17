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

    public PostService(Logger log){
        logger = log;
        logger.info("PostService started");

            // connection to mongodb databse, called "message_mania", with m_posts and m_users collections
            MongoClient mongoClient = MongoClients.create("mongodb+srv://chirpy_user:advanced_pro@chirpyv1.libuseu.mongodb.net/?retryWrites=true&w=majority&appName=chirpyv1");
            MongoDatabase database = mongoClient.getDatabase("messsage_mania");
            postsCollection = database.getCollection("m_posts");
            usersCollection = database.getCollection("m_users");
            logger.info("Connected to MongoDB at posts collection");
       
    }

    // function to add post, takes in username, content, and hashtag, content being > 128 charcters enforced with JS in post.thtml
    public void addPost(String username, String content, String hashtag) throws Exception {
        logger.info("Attempting to add post for user: " + username);
        try {
            Document newPost = new Document("username", username)
                                .append("content", content)
                                .append("hashtag", hashtag);
            postsCollection.insertOne(newPost);
            logger.info("Post added successfully for user: " + username);
        } catch (Exception e) {
            throw new Exception("Failed to add post for " + username + ": " + e.getMessage());
        }
    }

    // function that fetches all posts in m_posts collection, used in feed.hthml
    public Vector<Post> fetchPosts() throws Exception {
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
            throw new Exception("Failed to fetch posts: " + e.getMessage());
        }
        return posts;
    }

    // takes in the current user's username, returns posts that are in this user's following list
    public Vector<Post> fetchFollowingPosts(String currentUsername) throws Exception {
        Vector<Post> posts = new Vector<>();
        try {
            Document userDoc = usersCollection.find(Filters.eq("username", currentUsername)).first();
            if (userDoc != null) {
                List<String> followingListTmp = userDoc.getList("following", String.class);
                Vector<String> followingList = new Vector<>(followingListTmp);

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
                throw new Exception("User not found: " + currentUsername);
            }
        } catch (Exception e) {
            throw new Exception("Failed to fetch following posts for " + currentUsername + ": " + e.getMessage());
        }
        return posts;
    }

    // searches for posts that are posted by a specific username, if user is private, only returns if current user follows them
    public Vector<Post> fetchPostsByUsername(String currentUsername, String targetUsername) throws Exception {
        Vector<Post> posts = new Vector<>();
        try {
            Document targetUserDoc = usersCollection.find(Filters.eq("username", targetUsername)).first();
            if (targetUserDoc != null) {
                boolean isPublic = targetUserDoc.getBoolean("isPublic", true);
                
                Document currentUserDoc = usersCollection.find(Filters.eq("username", currentUsername)).first();
                List<String> followingListTmp = currentUserDoc != null ? currentUserDoc.getList("following", String.class) : List.of();
                Vector<String> followingList = new Vector<>(followingListTmp);
                
                boolean canFetchPosts = isPublic || followingList.contains(targetUsername);
                
                if (canFetchPosts) {
                    FindIterable<Document> documents = postsCollection.find(Filters.eq("username", targetUsername));
                    for (Document doc : documents) {
                        posts.add(new Post(
                            doc.getString("username"), 
                            doc.getString("content"), 
                            doc.getString("hashtag")
                        ));
                    }
                    logger.info("Posts fetched successfully for username: " + targetUsername);
                } else {
                    throw new Exception("Unable to fetch posts: " + targetUsername + "'s profile is private and current user is not following.");
                }
            } else {
                throw new Exception("User not found: " + targetUsername);
            }
        } catch (Exception e) {
            throw new Exception("Failed to fetch posts for username " + targetUsername + ": " + e.getMessage());
        }
        return posts;
    }
    
    // searches for posts by hashtag, if post is posted by private user, only shows if current user follows them
    public Vector<Post> fetchPostsByHashtag(String currentUsername, String hashtag) throws Exception {
        Vector<Post> posts = new Vector<>();
        try {
            Bson hashtagFilter = Filters.eq("hashtag", hashtag);
            FindIterable<Document> documents = postsCollection.find(hashtagFilter);

            Document currentUserDoc = usersCollection.find(Filters.eq("username", currentUsername)).first();
            List<String> followingListTmp = currentUserDoc != null ? currentUserDoc.getList("following", String.class) : List.of();
            Vector<String> followingList = new Vector<>(followingListTmp);

            for (Document doc : documents) {
                String postUsername = doc.getString("username");

                Document postUserDoc = usersCollection.find(Filters.eq("username", postUsername)).first();
                if (postUserDoc != null) {
                    boolean isPublic = postUserDoc.getBoolean("isPublic", true);
                    if (isPublic || followingList.contains(postUsername)) {
                        posts.add(new Post(
                            postUsername, 
                            doc.getString("content"), 
                            doc.getString("hashtag")
                        ));
                    }
                }
            }
            logger.info("Posts fetched successfully for hashtag: " + hashtag);
        } catch (Exception e) {
            throw new Exception("Failed to fetch posts for hashtag " + hashtag + ": " + e.getMessage());
        }
        return posts;
    }
}

