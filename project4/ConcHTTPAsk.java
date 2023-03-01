import java.net.*;
import java.io.*;
import java.util.logging.*;

public class ConcHTTPAsk {
    private static final Logger LOGGER = Logger.getLogger(ConcHTTPAsk.class.getName());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
            LOGGER.info("Server started on port " + args[0]);

            while (true) {
                Socket socket = serverSocket.accept();
                LOGGER.info("Accepted connection from " + socket.getInetAddress());

                Runnable thread = new MyRunnable(socket);
                new Thread(thread).start();
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.severe("Usage: java ConcHTTPAsk <port>");
            System.exit(1);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while running the server", e);
            System.exit(1);
        }
    }
}
