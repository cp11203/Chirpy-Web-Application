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

public class ListUsersHandler implements HttpHandler {

    final String LIST_USERS_PAGE = "listusers.thtml";
    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService;

    public ListUsersHandler(Logger log, DisplayLogic dl, UserService us) {
        logger = log;
        displayLogic = dl;
        userService = us;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        logger.info("ListUsersHandler called");

        Map<String, Object> dataModel = new HashMap<String,Object>();

      
        try {
            dataModel.put("users", userService.getUsers());
        } catch (Exception e) {
            logger.warning("Failed to fetch users: " + e.getMessage());
        }
        

        StringWriter sw = new StringWriter();
        displayLogic.parseTemplate(LIST_USERS_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, sw.getBuffer().length());
        
        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}

