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
                /*dataModel.put("username", username);
                dataModel.put("password", password);
                dataModel.put("Message", "Registration Success!");*/
                userService.registerUser(username, password);

                exchange.getResponseHeaders().set("Location", "/main/");
                exchange.sendResponseHeaders(302, -1); 
                exchange.getResponseBody().close(); 
                return;

            } else {
                // Passwords don't match
                dataModel.put("Message", "Registration Failed.");
            }
        } else {
            logger.info("Form data missing username, password, or confirm_password.");
        }

        StringWriter sw = new StringWriter();

        displayLogic.parseTemplate(REGISTER_PAGE, dataModel, sw);

        exchange.getResponseHeaders().set("Content-Type", "text/html");

        exchange.sendResponseHeaders(200, sw.getBuffer().length());

        OutputStream os = exchange.getResponseBody();
        os.write(sw.toString().getBytes());
        os.close();
    }
}
