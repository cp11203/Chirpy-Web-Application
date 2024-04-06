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

public class RegisterUserHandler implements HttpHandler {

    final String REGISTER_PAGE = "register.thtml";
    private Logger logger;
    private DisplayLogic displayLogic;
    private UserService userService;

    public RegisterUserHandler(Logger log, DisplayLogic dl, UserService us) {
        logger = log;
        displayLogic = dl;
        userService = us;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        logger.info("RegisterUserHandler called");

        Map<String, Object> dataModel = new HashMap<String,Object>();
Map<String, String> dataFromWebForm = displayLogic.parseResponse(exchange);

if (dataFromWebForm.containsKey("username") && dataFromWebForm.containsKey("password") && dataFromWebForm.containsKey("confirm_password")) {
    String username = dataFromWebForm.get("username");
    String password = dataFromWebForm.get("password");
    String confirmPassword = dataFromWebForm.get("confirm_password");

    if (password.equals(confirmPassword)) {
        
        // Using the return value of registerUser to determine the outcome
        boolean registrationSuccess = userService.registerUser(username, password);

        if (registrationSuccess) {
            // Registration successful
            logger.info("User registered successfully. Setting cookie for username: " + username);
            displayLogic.addCookie(exchange, "username", username);

            // Setting a success message
            dataModel.put("success", "Registration successful. Please log in.");

            exchange.getResponseHeaders().set("Location", "/login/");
            exchange.sendResponseHeaders(302, -1); 
            exchange.getResponseBody().close(); 
            return;
        } else {
            // Registration failed due to a problem (e.g., username already exists)
            logger.info("Registration failed for username: " + username);
            // Setting an error message
            dataModel.put("error", "Registration failed. Please try a different username.");
        }

    } else {
        // Passwords don't match
        logger.info("Passwords do not match.");
        // Setting an error message for password mismatch
        dataModel.put("error", "Passwords do not match. Please try again.");
    }
} else {
    logger.info("Form data missing username, password, or confirm_password.");
    // Setting an error message for missing form data
    dataModel.put("error", "Please provide a username, password, and confirm password.");
}

// Render the registration form page again with error/success messages
StringWriter sw = new StringWriter();
displayLogic.parseTemplate(REGISTER_PAGE, dataModel, sw);
exchange.getResponseHeaders().set("Content-Type", "text/html");
exchange.sendResponseHeaders(200, sw.getBuffer().length());
OutputStream os = exchange.getResponseBody();
os.write(sw.toString().getBytes());
os.close();

}
}
