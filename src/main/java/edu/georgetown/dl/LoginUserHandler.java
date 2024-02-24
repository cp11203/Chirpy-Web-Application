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

public class LoginUserHandler implements HttpHandler {

    final String LOGIN_PAGE = "login.thtml";
    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService;

    public LoginUserHandler(Logger log, DisplayLogic dl, UserService us) {
        logger = log;
        displayLogic = dl;
        userService = us; // Initialize userService
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        logger.info("LoginUserHandler called");

        Map<String, Object> dataModel = new HashMap<>();
        Map<String, String> dataFromWebForm = displayLogic.parseResponse(exchange);


        if (dataFromWebForm.containsKey("username") && dataFromWebForm.containsKey("password")) {
            String username = dataFromWebForm.get("username");
            String password = dataFromWebForm.get("password");

            // Use the userService to log the login attempt
            userService.loginUser(username, password);

            // NEED TO HANDLE ERRORS
        } 
        //if (dataFromWebForm.containsKey("username")) {
        //    displayLogic.addCookie(exchange, "username", dataFromWebForm.get("username"));
        //}
        StringWriter sw = new StringWriter();

        // figure out how to advance to mainpage

        displayLogic.parseTemplate(LOGIN_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");

        // would prob need to add cookies 

        exchange.sendResponseHeaders(200, sw.getBuffer().length());
        
        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}


