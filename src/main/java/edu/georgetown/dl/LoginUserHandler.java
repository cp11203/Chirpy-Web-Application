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
    final String MAIN_PAGE = "main.thtml";
    
    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService;

    public LoginUserHandler(Logger log, DisplayLogic dl, UserService us) {
        logger = log;
        displayLogic = dl;
        userService = us;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        logger.info("LoginUserHandler called");

        Map<String, Object> dataModel = new HashMap<String,Object>();
        
        Map<String, String> dataFromWebForm = displayLogic.parseResponse(exchange); // go back over how this works 


        if (dataFromWebForm.containsKey("username") && dataFromWebForm.containsKey("password")) {
            String username = dataFromWebForm.get("username");
            String password = dataFromWebForm.get("password");

            logger.info("Setting cookie for username: " + dataFromWebForm.get("username"));
            displayLogic.addCookie(exchange, "username", dataFromWebForm.get("username"));

            boolean loginSuccess = userService.loginUser(username, password);
            
            if (loginSuccess) {
                exchange.getResponseHeaders().set("Location", "/main/");
                exchange.sendResponseHeaders(302, -1); 
                exchange.getResponseBody().close(); 
                return;
            } else {
                dataModel.put("Result", "Invalid username or password");
            }
        } 
        StringWriter sw = new StringWriter();

        displayLogic.parseTemplate(LOGIN_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");

        exchange.sendResponseHeaders(200, sw.getBuffer().length());
        
        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}

