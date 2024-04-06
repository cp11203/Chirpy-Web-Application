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

    private boolean is_public;   

    private Vector<String> followers; 


    public User( String username, String password ) {
        this.username = username;
        this.password = password;
        this.is_public = true;
        this.followers = new Vector<String>();        
    }

    /**
     * Gets the user's username
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    // DO WE USE THIS =- genuienly dk 
    public boolean checkPassword( String password ) {
        return this.password.equals( password );
    }



  

}