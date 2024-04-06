package edu.georgetown.dl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.georgetown.bll.user.UserService;

public class AddFollowerHandler implements HttpHandler {
    final String ADD_FOLLOWER_PAGE = "addfollower.thtml";

    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService;

    public AddFollowerHandler(Logger log, DisplayLogic dl, UserService us) {
        logger = log;
        displayLogic = dl;
        userService = us;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {


        logger.info("AddFollowerHandler called");

        Map<String, Object> dataModel = new HashMap<>();

        if ("POST".equals(exchange.getRequestMethod())) {
            // Parse the form data
            Map<String, String> postData = displayLogic.parseResponse(exchange);
            Map<String, String> cookies = displayLogic.getCookies(exchange);

            String currentUsername = cookies.getOrDefault("username", "Guest");

            if (postData.containsKey("userToFollow")) {
                String userToFollow = postData.get("userToFollow");
    
                // Use UserService to add the follower and check if the operation was successful
                boolean addedSuccessfully = userService.addFollower(currentUsername, userToFollow);
    
                if (addedSuccessfully) {
                    logger.info("Added " + userToFollow + " to " + currentUsername + "'s following list.");
                    dataModel.put("successMessage", "Successfully added " + userToFollow + " to your following list.");
                } else {
                    // This means the userToFollow does not exist or some error occurred
                    logger.warning("Could not add " + userToFollow + " to " + currentUsername + "'s following list.");
                    dataModel.put("errorMessage", "Could not add " + userToFollow + " to your following list. User may not exist.");
                }
            } else {
                // Handle missing fields in the form
                logger.warning("Missing 'userToFollow' field in the form.");
                dataModel.put("errorMessage", "Missing required field: 'userToFollow'.");
            }
        }

        StringWriter sw = new StringWriter();

        displayLogic.parseTemplate(ADD_FOLLOWER_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, sw.getBuffer().length());

        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}
