package edu.georgetown.dl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.georgetown.bll.user.PostService;
import edu.georgetown.bll.user.UserService; 

public class PostHandler implements HttpHandler {

    final String POST_PAGE = "post.thtml";
    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService;
    private PostService postService;

    public PostHandler(Logger log, DisplayLogic dl, UserService us, PostService ps) {
        logger = log;
        displayLogic = dl;
        userService = us;
        postService = ps;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        logger.info("post handler called called");

        Map<String, Object> dataModel = new HashMap<String,Object>();

    if ("POST".equals(exchange.getRequestMethod())) {
    // Parse the form data
        Map<String, String> postData = displayLogic.parseResponse(exchange);
        Map<String, String> cookies = displayLogic.getCookies(exchange);
        String username = cookies.getOrDefault("username", "Guest");

    // Validate required fields are present
        if (postData.containsKey("content") && postData.containsKey("hashtag")) {
            String content = postData.get("content");
            String hashtag = postData.get("hashtag");

        // Use PostService to add the post
            try {
                postService.addPost(username, content, hashtag);
                logger.info("Post successfully added for username: " + username);
                dataModel.put("postSuccess", "Post successfully added!");
            } catch (Exception e) {
             logger.warning("Failed to add post for username: " + username);
             dataModel.put("error", "An error occurred while adding the post.");
            }
        } else {
        // Handle missing fields in the form
        logger.warning("Missing fields in post form.");
        dataModel.put("error", "Missing required fields in the post form.");
        }
}

        logger.info("does this code in post handler get ran???");

        StringWriter sw = new StringWriter();
        displayLogic.parseTemplate(POST_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, sw.getBuffer().length());
        
        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}
