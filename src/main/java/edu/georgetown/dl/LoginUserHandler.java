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

    final String FORM_PAGE = "login.thtml";
    final String MAIN_PAGE = "main.thtml";
    
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
            boolean loginSuccess = userService.loginUser(username, password);
            
            if (loginSuccess) {
                exchange.getResponseHeaders().set("Location", MAIN_PAGE); //redir to main
                exchange.sendResponseHeaders(302, -1);
                return; // End the request after redirect
            } else {
                dataModel.put("error", "Invalid username or password");
            }
        } 
        StringWriter sw = new StringWriter();

        // figure out how to advance to mainpage

        displayLogic.parseTemplate(FORM_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");

        // would prob need to add cookies 
        //if (dataFromWebForm.containsKey("username")) {
        //    displayLogic.addCookie(exchange, "username", dataFromWebForm.get("username"));
        //}

        exchange.sendResponseHeaders(200, sw.getBuffer().length());
        
        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}


