package edu.georgetown.dl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.georgetown.bll.user.PostService;
import edu.georgetown.bll.user.UserService;
import edu.georgetown.dao.Post; 

public class FollowingHandler implements HttpHandler {

    final String FOLLOWING_PAGE = "following.thtml";
    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService;
    private PostService postService;

    public FollowingHandler(Logger log, DisplayLogic dl, UserService us, PostService ps) {
        logger = log;
        displayLogic = dl;
        userService = us;
        postService = ps;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        logger.info("following handler called");

        Map<String, Object> dataModel = new HashMap<String,Object>();

        Vector<Post> postsVector = new Vector<>();


       
        try {

            Map<String, String> cookies = displayLogic.getCookies(exchange);

            String username = cookies.getOrDefault("username", "Guest");

            postsVector.addAll(postService.fetchFollowingPosts(username));

            int vectorSize = postsVector.size();
            
            logger.info("Size of vector: " + vectorSize);   

            // Then add this Vector of posts to the dataModel
            dataModel.put("posts", postsVector);

            logger.info("Posts successfully added to data model");
        } catch (Exception e) {
            logger.warning("Failed to fetch posts: " + e.getMessage());
        }
        

        StringWriter sw = new StringWriter();

        displayLogic.parseTemplate(FOLLOWING_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, sw.getBuffer().length());
        
        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}
