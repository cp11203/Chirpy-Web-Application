/**
 * A skeleton of a Chirper
 * 
 * Micah Sherr <msherr@cs.georgetown.edu>
 */

package edu.georgetown.dao;

import java.io.Serializable;
import java.util.Vector;

public class User implements Serializable {
    
    private String username;
    private String password;

    /** if true, the user's chirps are public */
    private boolean is_public;   

    /** list of this chirper's followers */
    private Vector<User> followers;


    public User( String username, String password ) {
        this.username = username;
        this.password = password;
        this.is_public = true;
        this.followers = new Vector<User>();        
    }

    /**
     * Gets the user's username
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    public boolean checkPassword( String password ) {
        return this.password.equals( password );
    }

    // ARE WE DOING FOLLOWERS
    public void addFollower( User follower ) {
        // doesn't do anything.  you should probably change this
        return;
    }

    public Vector<User> getFollowers() {
        return this.followers;
    }

}