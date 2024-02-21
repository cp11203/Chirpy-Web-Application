# Chirpy design document
## This document is meant to outline the general strucutre of the chirpy application


## Functionalities 

- adding user, with a passowrd
- posting a chirp
- fetchign posts, and rendering a timeline
- searching for chirps from hashatag
- searching for posts via username

-  WHAT WE NEED TO DO FOR THIS ASSIGNMENT
    -  create register - which is sign in form 
    - create list users 
    - create login 


## Chirpy.java file

- this is the entry point of the application
- main function 
    - instantiates Chirpy instance
    - creates a UserService instance
    - creates the web service using Chirpy startService function
        - server.CreateContext calls display pages for specific url paths
- hosting notes
    - https://organic-space-guacamole-7jrvxxrq9vj2xg69-8080.app.github.dev/
    - https://organic-space-guacamole-7jrvxxrq9vj2xg69-8080.app.github.dev/formtest/
    - aight so these r the same brotha 


## Data Object Classes
### purpose of these classes is 

NEED TO LOOK AT THIS - look at both of these before u go 


## Business Logic Classes
### purpose of these classes is 

NEED TO FIGURE THIS OUT

 idk if we need seprate classes for the other services, as i have now, or they can just be functions of main user service
 - tbd 

right now thinking UserService and ChirpService, maybe also search we will see 

- **UserService** 
- **UserRegistrationService** 
- **UserAuthenticationService** 
- **ChirpService** 
- **SearchService** 


## Display Logic Classes
### purpose of these classes is 

how do we persist data and send this to dataModel

so you add key value paids to dataModel hashmap, pass this to displaylogic parser, and then access these variable values by doing ${key} in the template .thtml code

- in resources/templates , we have .thmtl files, 
    - toppage.thmtl - chnage this to be landingpage - and then user can either register or login
    - formtest.thmtl - will get rid of this, use for refernece now
    - showcookies.thmtl
    - listusers.thtml - need to add this 
    - login.thtml
    - mainpage.thtml -- need to add this 
- handlers - implemenation of HTTPHandler interface, to handle http requests to different url paths
    - DefaultPageHandler 
    - ListCookiesHandler
    - TestFormHandler - will delete this
    -  ListUsersHandler
    -  LoginHandler
    -  RegisterUserHandler
- DisplayLogic.java file - we do not need to change this, but it gives us code to render dynamic html pages


## Storage

- we will incoporate some sort of object oriented database backend in order to persist data - most likely mongo db

## Cookies

- what of cookies ????

## Business Logic Classes


