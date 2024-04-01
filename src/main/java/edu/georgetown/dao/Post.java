package edu.georgetown.dao;

import java.io.Serializable;
import java.util.Vector;

public class Post implements Serializable {
    
    private String username;
    private String content;
    private String hashtag;
    private boolean isPublic;   


    public Post(String username, String content, String hashtag) {
        this.username = username;
        this.content = content;
        this.hashtag = hashtag;
        this.isPublic = true; // Default to true, can be changed later   
    }

    /**
     * Gets the username of the person who posted.
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets the content of the post.
     * @return the content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Gets the hashtag associated with the post.
     * @return the hashtag
     */
    public String getHashtag() {
        return this.hashtag;
    }

    /**
     * Checks if the post is public.
     * @return true if the post is public
     */
    public boolean getIsPublic() {
        return this.isPublic;
    }

    
}

