package edu.georgetown.dl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.georgetown.bll.user.UserService; // Make sure this import statement is correct based on your project structure

public class RegisterUserHandler implements HttpHandler {

    final String FORM_PAGE = "register.thtml";
    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService; // Add a reference to UserService

    // Update the constructor to accept UserService
    public RegisterUserHandler(Logger log, DisplayLogic dl, UserService us) {
        logger = log;
        displayLogic = dl;
        userService = us; // Initialize userService
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        logger.info("RegisterUserHandler called");

        Map<String, Object> dataModel = new HashMap<>();
        Map<String, String> dataFromWebForm = displayLogic.parseResponse(exchange);

        // Check if the form contains both username and password
        if (dataFromWebForm.containsKey("username") && dataFromWebForm.containsKey("password")) {
            String username = dataFromWebForm.get("username");
            String password = dataFromWebForm.get("password");

            // Call UserService to register the user
            userService.registerUser(username, password);

            // NEED TO HANDLE ERRORS
        }

        StringWriter sw = new StringWriter();

        displayLogic.parseTemplate(FORM_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");

        exchange.sendResponseHeaders(200, sw.getBuffer().length());
        
        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}

