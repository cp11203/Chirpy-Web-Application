functionality we need to add for p2
- posting 
- feed
- search
- logout
- functrional navigation, which we have


need to change names

app will be called message_mania




chirp naming was annoying me so much

Chirpy.java and Chirpy class renamed to MessageMania - this will be name of our app

a Chirper is just a User

and a Chirp is just a Post


so need to be able to create a post, but u need username i would say 


and then rendering them, 

fetch_all_posts - need to create an array or somnething, and someome show it in html, just like you do with users 


posts 

posted_by // string user_name

content _ string

hashtag  - string 

is_public bool, default is true , onlty false is psoting user profile is false 


posts need to have 

// so how do we do private 

// at time of post, check if user id is private, if so add private flag to post 


// so we need to add a user contact list it seems

// so this will be an array of users in each user document - 

// bascially - if the user is private, the user we are fetching posts for has to be in the contact list 