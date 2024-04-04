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


public class MainPageHandler implements HttpHandler {

    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService;

    private final String MAIN_PAGE = "main.thtml";

    public MainPageHandler(Logger log, DisplayLogic dl, UserService us) {
        logger = log;
        displayLogic = dl;
        userService = us;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.info("MainPageHandler called");

        // Initialize StringWriter to hold the output of the template parsing
        StringWriter sw = new StringWriter();
        // Initialize dataModel to hold any data to be used in the template
        Map<String, Object> dataModel = new HashMap<>();
        Map<String, String> dataFromWebForm = displayLogic.parseResponse(exchange);
        // Code taken from chatGPT
        // Check if logout button was clicked
        if (dataFromWebForm.containsKey("logout")) {
            // Delete the cookie
            displayLogic.deleteCookie(exchange); // Change "user_id" to your cookie name
            // Redirect to login page or any other desired page
            exchange.getResponseHeaders().set("Location", "/login/"); // Redirect to login page
            exchange.sendResponseHeaders(302, -1); // HTTP 302 Found response
            exchange.close();
            return;
        }

        // Parse the main.thtml template with any required data and write to StringWriter
        displayLogic.parseTemplate(MAIN_PAGE, dataModel, sw);

        // Set the content type of the response to HTML
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        // Send HTTP headers with a 200 OK response, and the length of the response content
        exchange.sendResponseHeaders(200, sw.getBuffer().length());
        // Write the content of the StringWriter (the parsed template) to the response body and close the stream
        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}
