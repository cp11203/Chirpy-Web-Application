/**
 * Chirpy -- a really basic social networking site
 * 
 * Micah Sherr <msherr@cs.georgetown.edu>
 */

package edu.georgetown;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import com.sun.net.httpserver.HttpServer;
import edu.georgetown.bll.user.UserService;
import edu.georgetown.dl.DefaultPageHandler;
import edu.georgetown.dl.DisplayLogic;
import edu.georgetown.dl.ListCookiesHandler;
import edu.georgetown.dl.RegisterUserHandler;
import edu.georgetown.dl.LoginUserHandler;
import edu.georgetown.dl.ListUsersHandler;
import edu.georgetown.dl.MainPageHandler;


public class Chirpy {

    final static int PORT = 8080;

    private Logger logger;
    private DisplayLogic displayLogic;

    public Chirpy() {
        /* 
         * A Logger is a thing that records "log" messages.  This is better
         * than using System.out.println() because you can control which
         * messages are logged.  For example, you can log only "warning"
         * messages, or you can log all messages.
         * 
         * We'll create one logger, call it `logger`, and then pass this
         * logger to our classes.  This way, all of our classes will log
         * to the same place.
         */
        logger = Logger.getLogger("MyLogger");
        try {
            FileHandler fileHandler = new FileHandler("/tmp/log.txt");
            logger.addHandler(fileHandler);
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConsoleHandler consoleHandler = new ConsoleHandler();
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false); // Remove default handlers
        // Set desired log level (e.g., Level.INFO, Level.WARNING, etc.)
        logger.setLevel(Level.ALL); 

        try {
            displayLogic = new DisplayLogic(logger);
        } catch (IOException e) {
            logger.warning("failed to initialize display logic: " + e.getMessage());
            System.exit(1);
        }

        logger.info("Starting chirpy web service");

    }

    /**
     * Start the web service
     */
    private void startService() {
        try {
            // initialize the web server
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

            UserService userService = new UserService(logger);

            // each of these "contexts" below indicates a URL path that will be handled by
            // the service. The top-level path is "/", and that should be listed last.
            server.createContext("/register/", new RegisterUserHandler(logger, displayLogic, userService));
            server.createContext("/login/", new LoginUserHandler(logger, displayLogic, userService));
            server.createContext("/main/", new MainPageHandler(logger, displayLogic, userService));
            server.createContext("/main/listusers/", new ListUsersHandler(logger, displayLogic, userService));
            server.createContext("/main/showcookies/", new ListCookiesHandler(logger, displayLogic));

            server.createContext("/", new DefaultPageHandler(logger, displayLogic));           

            server.setExecutor(null); // Use the default executor

            // this next line effectively starts the web service and waits for requests. The
            // above "contexts" (created via `server.createContext`) will be used to handle
            // the requests.
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        logger.info("Server started on port " + PORT);
    }

    public static void main(String[] args) {

        Chirpy ws = new Chirpy();

        // let's start up the various business logic services
        UserService userService = new UserService(ws.logger);

        // finally, let's begin the web service so that we can start handling requests
        ws.startService();
    }

}
