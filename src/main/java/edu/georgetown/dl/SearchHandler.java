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

public class SearchHandler implements HttpHandler {

    final String SEARCH_PAGE = "search.thtml";
    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService;
    private PostService postService;

    public SearchHandler(Logger log, DisplayLogic dl, UserService us, PostService ps) {
        logger = log;
        displayLogic = dl;
        userService = us;
        postService = ps;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        logger.info("Search handler called");

        Map<String, Object> dataModel = new HashMap<>();

        Vector<Post> postsVector = new Vector<>();
 
        if ("POST".equals(exchange.getRequestMethod())) {

            Map<String, String> formData = displayLogic.parseResponse(exchange);
    
            Map<String, String> cookies = displayLogic.getCookies(exchange);

            String currentUsername = cookies.getOrDefault("username", "Guest");
    
            try {
                // Now, depending on form data, decide which PostService method to call.
                if (formData.containsKey("username")) {
                    String username = formData.get("username");
                    try {
                        postsVector.addAll(postService.fetchPostsByUsername(currentUsername, username));
                        dataModel.put("success", "Posts fetched successfully.");
                    } catch (Exception e) {
                        dataModel.put("error", e.getMessage());
                    }
                } else if (formData.containsKey("hashtag")) {
                    String hashtag = formData.get("hashtag");
                    try {
                        postsVector.addAll(postService.fetchPostsByHashtag(currentUsername, hashtag));
                        dataModel.put("success", "Posts fetched successfully.");
                    } catch (Exception e) {
                        dataModel.put("error", e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.severe("Error fetching posts: " + e.getMessage());
                dataModel.put("error", "Failed to fetch posts.");
            }
            
            // Add postsVector to the data model if there are any posts to display

               
            
        }

        dataModel.put("posts", postsVector);

        StringWriter sw = new StringWriter();
        displayLogic.parseTemplate(SEARCH_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, sw.getBuffer().length());
        
        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}

