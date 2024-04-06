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

public class AccountHandler implements HttpHandler {

    final String ACCOUNT_PAGE = "account.thtml";
    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService;
    private PostService postService;

    public AccountHandler(Logger log, DisplayLogic dl, UserService us, PostService ps) {
        logger = log;
        displayLogic = dl;
        userService = us;
        postService = ps;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        logger.info("account handler called called");

        Map<String, Object> dataModel = new HashMap<String,Object>();

        
        if ("POST".equals(exchange.getRequestMethod())) {
            // Fetching the current user's username from cookies
            Map<String, String> cookies = displayLogic.getCookies(exchange);
            String username = cookies.get("username"); // Assuming "username" cookie stores the logged-in user's username

            if (username != null && !username.isEmpty()) {
                // Call UserService to switch the user's account to private
                userService.switchToPrivate(username);
                logger.info("Account successfully switched to private for username: " + username);
                dataModel.put("accountActionSuccess", true);

                dataModel.put("accountActionSuccess", "Your account has been switched to private.");
            } else {
                // Handle case where username is not found in cookies
                logger.warning("Username not found in cookies.");
                dataModel.put("error", "Unable to retrieve username. Please log in again.");
            }
        } else {
            // Handle non-POST requests or show account page with options
            // For simplicity, assuming all account actions are done via POST
            logger.warning("Unsupported request method for account actions.");
        }

        logger.info("does this code in post handler get ran???");

        StringWriter sw = new StringWriter();
        displayLogic.parseTemplate(ACCOUNT_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, sw.getBuffer().length());
        
        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}
