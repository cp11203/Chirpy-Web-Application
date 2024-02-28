# Chirpy design document
## This document is meant to outline the general strucutre of the chirpy application

## Version Control Guidlines
- our process for ensuuring we have a clean code history and minimize confusion in terms of changing code:
    - each group member always codes in their own branch, and works on a specifc functionality
    - to combine all members' work, we one-by-one, merge their branch with a "main-integration" branch
    - we use this main-integration branch as a staging area to test the new code
    - after all branches are merged to main-integraton and everything functions properly, we merge this main-integration branch with main


## Functionalities 

v1 of chirpy simply entials a user login and registration, and a page to list all users 
a core part of our app is utilzing a hosted backend to store data, we chose MOngoDB to accomplish this, so all user data is stored and verified with our mongodb databaase

in the future, users will also have the abilities of: 
- posting a chirp
- fetchign posts, and rendering a timeline
- searching for chirps from hashatag
- searching for posts via username


## Chirpy.java file

- this is the entry point of the application
- main function 
    - instantiates Chirpy instance
    - creates a UserService instance
    - creates the web service using Chirpy startService function
        - server.CreateContext calls display pages for specific url paths


## Data Object Classes

- right now we just have a chirper object, which holds user information - just their username and password right now
- in future we will add objects to hold post information


## Business Logic Classes

- right now we just have a UserService, which allows users to login and register, and also has fucntion to list all users

UserService class functions
- registerUser - takes in a username and password, and creates a chriper object stored as a document in our mongodb collection
- loginUser- takes in a username and passowrd strings, and checks these against mongodb collection, if valid, returns true, else, returns false
- getUsers - function that returns all user documents stored in mongodb, as a vector of Chirper objects

in future will add:
- **ChirpService** 
- **SearchService** 


## Display Logic Classes / FrontEnd templates
# The handler functions that are respjsnbile for passing data to htmtl to render on screen

- in resources/templates , we have .thmtl files, use bootstrap to make them look pretty, need to work on this in future though
    - default.thmtl - this is the page rendered when just url is entered
    - showcookies.thmtl
    - listusers.thtml - need to add this 
    - login.thtml
    - register.thtml
    - mainpage.thtml -- this is the main page of the application, shown only after succesful user login or registration
- handlers - implemenation of HTTPHandler interface, to handle http requests to different url paths
    - DefaultPageHandler 
    - ListCookiesHandler
    -  ListUsersHandler
    -  LoginHandler
    -  RegisterUserHandler
    - MainPageHandler
- DisplayLogic.java file - we do not need to change this, but it gives us code to render dynamic html pages


## Storage

- we are using a hosted mongodb atlas instance to store data
- right now we just store user (chirper) objects in the databse, and in the future we will add posts





