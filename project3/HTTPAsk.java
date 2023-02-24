import java.net.*;
import java.io.*;
import tcpclient.TCPClient;
import java.nio.charset.StandardCharsets;

public class HTTPAsk {
    public static void main(String[] args) {
        // Check for valid port number
        int port = 0;
        ServerSocket serverSocket = null;
        
        if (args.length == 0) {
            System.err.println("Wrong port input!\nUse correct format: java HTTPAsk <port_number>");
            System.exit(1);
        }
        
        try {
            // Create a server socket and link it to the port
            port = Integer.parseInt(args[0]);
            serverSocket = new ServerSocket(port);

            while (true) {
                
                // Accept a client connection
                Socket socket = serverSocket.accept();

                // Get the output stream from the socket.
                OutputStream socketOutput = socket.getOutputStream();

                // Create a StringBuilder object to save the HTTP response
                StringBuilder str = new StringBuilder();

                try {
                    // Read the request from the client
                    int BUFF_MAX = 1024;
                    byte[] dataBuff = new byte[BUFF_MAX];
                    socket.getInputStream().read(dataBuff);

                    // Decode the request
                    String strDecoded = new String(dataBuff, StandardCharsets.UTF_8);
                    String[] decodedParts = strDecoded.split(" ");

                    // Check if the request is valid
                    String requestMethod = decodedParts[0];
                    String requestPath = decodedParts[1];
                    if (!requestMethod.equals("GET") || !strDecoded.contains("HTTP/1.1")) {
                        String errorMsg = "Bad Request";
                        str.append("HTTP/1.1 400 " + errorMsg + "\r\n");
                        throw new Exception(errorMsg);
                    }
                    // Check if the path is valid
                    String[] pathStr = requestPath.split("\\?");
                    String pathArr = pathStr[0];
                    if (pathArr.equals("/ask")) {
                        if (pathStr.length < 2) {
                            String errorReq = "Invalid request: input string not found";
                            str.append("HTTP/1.1 400 Bad Request\r\n\r\n"); 
                            throw new Exception(errorReq);
                        }

                        // Get the parameters
                        //If pathsStr has at least two elements before trying to access the second element (pathsStr[1]). 
                        //If it doesn't, it assigns an empty array to pList
                        
                        String[] pList = pathStr[1].split("&");
                        //Pattern pattern = Pattern.compile("&");
                        //String[] pList = pattern.split(pathStr[1]);

                        // Initialize the variables for the parameters
                        String hostname = "";
                        
                        boolean shutdown = false;
                        Integer timeout = null;
                        Integer limit = null;
                        
                        byte[] serverBytes = new byte[0];
                        int portC = 0;

                        // Check if the parameters are valid
                        for (String x : pList) {

                            String[] key = x.split("=");

                            if (key.length < 2) {
                                str.append("HTTP/1.1 400 Bad Request\r\n\r\n");
                                String errorMsg = "Invalid key: " + key +" not found";
                                throw new Exception(errorMsg);
                            }
                
                            switch (key[0]) {
                                case "shutdown":
                                    shutdown = Boolean.parseBoolean(key[1]);
                                    break;
                                case "timeout":
                                    timeout = Integer.parseInt(key[1]);
                                    break;
                                case "limit":
                                    limit = Integer.parseInt(key[1]);
                                    break;
                                case "hostname":
                                    hostname = key[1];
                                    break;
                                case "port":
                                    portC = Integer.parseInt(key[1]);
                                    break;
                                case "string":
                                    serverBytes = key[1].getBytes();
                                    break;
                                default:
                                    str.append("HTTP/1.1 400 Bad Request\r\n");
                                    String errorMsg = "Invalid key: " + key[0];
                                    throw new Exception(errorMsg);
                            }
                        }
                        
                        if (hostname.equals("") || portC == 0) {str.append("HTTP/1.1 400 Bad Request\r\n");
                            String errorReq = "Invalid request: hostname or port not found";
                            throw new Exception(errorReq);
                        }

                        // Send the request to the server
                        try {str.append("HTTP/1.1 200 OK\r\n\r\n");

                            // Create a TCPClient object
                            TCPClient clientTcp = new TCPClient(shutdown, timeout, limit);

                            // Send the request to the server
                            str.append(new String(clientTcp.askServer(hostname, portC, serverBytes)));

                            // Send the response to the client
                            socketOutput.write(str.toString().getBytes());
                            // Close socket
                            socket.close();
                    
                        } catch (Exception e) {str.append("HTTP/1.1 500 Internal Server Error\r\n");
                        String errorMsg = "Internal server error: " + e.getMessage();
                            throw new Exception(errorMsg);
                        }

                    }   
                    else {str.append("HTTP/1.1 404 Not Found\r\n");
                        String errorMsg = "Not Found:" + pathArr;
                        throw new Exception(errorMsg);
                    }
                
                } catch (Exception e) {
                    // Send the response to the client
                    socketOutput.write(str.toString().getBytes());
                    // Close socket
                    socket.close();
                  }
            }
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
   	 	    //str.append("HTTP/1.1 400 Bad Request\r\n\r\n");
    		//errorResponse = "Invalid request: " + e.getMessage();
    		//throw new ArrayIndexOutOfBoundsException(errorResponse);
            }
    }
}
