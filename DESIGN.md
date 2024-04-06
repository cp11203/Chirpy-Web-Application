# MessageMania design document
## This document is meant to outline the general strucutre of our MessageMania application

## Version Control Guidlines
- our process for ensuring we have a clean code history and minimize confusion in terms of changing code:
    - each group member always codes in their own branch, and works on a specifc functionality
    - to combine all members' work, we one-by-one, merge their branch with an integration branch
    - we use this integration branch as a staging area to test the new code
    - after all branches are merged to main-integraton and everything functions properly, we merge this main-integration branch with main


## Functionalities 

this version of MessageMania has the following user functionalities:
    - able to post a post with a content and hashtag
    - ability to follow other users
    - viewing a global feed that shows all posts
    - viewing a following feed that shows you only posts that are posted by people you follow
    - searching for posts via username or hashtag
        - if you search for a post that is posted by a private user, you will only see the post if you follow them



## MessageMania.java file

- this is the entry point of the application
- main function 
    - instantiates Chirpy instance
    - instantiates a UserService instance
    - instantiates a PostService instance
    - creates the web service using startService function
    - server.CreateContext calls display pages for specific url paths


## Data Object Classes

- has a User class, holds username and password, but our User object stored in MongoDB Databse also has isPublic bool and followers list, but we do not need this for the actual java class implementation
- has a Post class, which has username, content, and hashtag string variables
- both classes have private variables with public access and set method


## Business Logic Classes

- both BLL classes connect to our hosted backend with MongoDB
- rundown of MongoDB implementation
    - create client connect with our url, unfortunatley stored inline rather than as github env variable 
    - database is called "message_mania" - has 2 collections: m_users and m_posts
    - use MongoDB java driver functions .insert and .find to add and fetch user and post objects to and from database

UserService class functions
- registerUser - takes in a username and password, and creates a User object stored as a document in users collection, return true if success, false otherwise, throws expetion
- loginUser- takes in a username and passowrd strings, and checks these against MongoDB collection, if valid, returns true, else, returns false
- getUsers - function that returns all user documents stored in mongodb, as a vector of User objects
- addFollower - a function that takes in 2 strings: the logged in user as currentUsername, and the userToFollow, it adds the latter value to the current user's following list in MongoDB, if success, returns, true, otherwise false
- switchToPrivate- fucntion that sets users profile to private, takes in string which is current username

PostService class functions
- addPost - takes in username, content, and hashtag strings, inserts this post document to MongoDB posts collection, throws exception
- fetchPosts - fetches all posts in posts collection, returns as vector of posts, this is used to render posts in the global feed, throws exception
- fetchFollowingPosts - fetches only posts that the logged-in user follows, this is used to render posts in the following feed, throws exception
- fetchPostsByUsername - fecthes all posts with username == to inputted username, returns vector of Post objects, throws exception
-fetchPostsByHashTag - fecthes all posts with hashtag == to inputted hahstag, returns vector of Post objects, throws exception
    - in each of these search functions, if the user who posted post is private, we only return the post if the current user follows this user

## Display Logic Classes / FrontEnd templates
# The handler functions that are resonsible for passing data to htmtl to render on screen


- in resources/templates , we have .thmtl files, in which we use basic css to make them look pretty
    - default.thmtl - this is the page rendered when just url is entered
    - listusers.thtml
    - login.thtml
    - register.thtml
    - mainpage.thtml -- this is the main page of the application, shown only after succesful user login or registration
    - post.thtml - form inputs to post a post
    - feed.thtml - shows global feed of all posts
    - following - shows feed of post only posted by user you follow
    - search.thtml - allows users to search by username or hashtag
    - account - button that allows users to switch account to private
    - addfollowers- form that allows user to input username to add to following list
- handlers - implemenation of HTTPHandler interface, to handle http requests to different url paths
    - DefaultPageHandler 
    - ListUsersHandler
    - LoginHandler
    - RegisterUserHandler
    - MainPageHandler
    - PostHandler
    - FeedHandler
    - FollowingHandler
    - SearchHandler
    - AddContactsHandler 
    - Accounthandler
- DisplayLogic.java file - we do not need to change this, but it gives us code to render dynamic html pages


## Cookies
    - when user either registers or logs in, we call displayLogic.addCookie to add the username as a cookie, this is the only cookie we store
    - when user logs out, we call displayLogic.deleteCookie in order to remove this cooki


## Testing
    - used JUnit and run mvm test in terminal to instantiate test classes for UserService and PostService
    - kind of hard to implement testing using hosted backend, but tried to make sure the correct values are beign returned for each function, whether in a successful or error context
    - PostServiceTest
        - addPostSuccessfullyAddsPost - makes sure addPost does not throw
        - testFetchPosts - ensures posts in returned vector
        - testFetchFollowingPosts - ensures posts in returned vector
        - testFetchPostsByUsernameUserNotFound - makes sure search for fake user throws
        - testFetchPostsByHashtag - makes sure search for fake hashtag returns empty vector
    - UserServiceTest
        - testRegisterUser_Success - makes sure true is returned when succesffuly registering
        - testLoginUser_Success - makes sure login in works if right user and pass entred
        - testLoginUser_Failure - makes sure log in with wrong credentials fails
        - testGetUsers - ensrues a non-empty vector returned
        - testAddFollower_Success  - ensures adding an exiestent user works
        - testAddFollower_UserNotFound - make sure trying to add fake user does not work

## Build Tool
    - in order to implement the MongoDB Java Driver, we used Maven build tool
    - in pom.xml file- we include the mongo driver, and also Maven has JUnit built in

## Storage (extra functionality)

- we are using a hosted MongoDB atlas instance to store data
- we store both users and posts in this databse
- this allows us to persist usernames and password, so we can verifty sign-ins





