/**
 * 
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
import edu.georgetown.bll.user.PostService;
import edu.georgetown.dl.DefaultPageHandler;
import edu.georgetown.dl.DisplayLogic;
import edu.georgetown.dl.ListCookiesHandler;
import edu.georgetown.dl.RegisterUserHandler;
import edu.georgetown.dl.LoginUserHandler;
import edu.georgetown.dl.ListUsersHandler;
import edu.georgetown.dl.MainPageHandler;
import edu.georgetown.dl.PostHandler;
import edu.georgetown.dl.FeedHandler;


public class MessageMania {

    final static int PORT = 1025;

    private Logger logger;
    private DisplayLogic displayLogic;

    public MessageMania() {

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

        logger.info("Starting web service");

    }

    /**
     * Start the web service
     */
    private void startService() {
        try {
            // initialize the web server
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

            // initalzie the user service
            UserService userService = new UserService(logger);

            PostService postService = new PostService(logger);

            // prob add posting service, and maybe feed and search shit

            // each of these "contexts" below indicates a URL path that will be handled by
            // the service. The top-level path is "/", and that should be listed last.
            server.createContext("/register/", new RegisterUserHandler(logger, displayLogic, userService));
            server.createContext("/login/", new LoginUserHandler(logger, displayLogic, userService));
            server.createContext("/main/", new MainPageHandler(logger, displayLogic, userService));
            server.createContext("/main/listusers/", new ListUsersHandler(logger, displayLogic, userService));
            server.createContext("/main/showcookies/", new ListCookiesHandler(logger, displayLogic));
            server.createContext("/main/post/", new PostHandler(logger, displayLogic, userService, postService));
            server.createContext("/main/feed/", new FeedHandler(logger, displayLogic, userService, postService));

            // what other ones do we need
            // so have main show buttons for feed and search 
            // feed 
            // search

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

        MessageMania ws = new MessageMania();

        //UserService userService = new UserService(ws.logger); // do u init this twice ????

        // finally, let's begin the web service so that we can start handling requests
        ws.startService();
    }

}
