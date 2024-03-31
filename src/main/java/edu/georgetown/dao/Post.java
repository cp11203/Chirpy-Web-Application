package edu.georgetown.dao;

public class Post {

    public String username;
    public String content;
    public String hashtag;
    public boolean isPublic;

    public Post(String username, String content, String hashtag) {
        this.username = username;
        this.content = content;
        this.hashtag = hashtag;
        this.isPublic = true; // can change later when we figure out the stuff 
    }
}
