import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import tcpclient.TCPClient;

public class MyRunnable implements Runnable {
    
    //declare socket
    private Socket socket;
    //declare variables
    private String hostname = "";
    private StringBuilder str = new StringBuilder();
    private boolean shutdown = false;
    private Integer limit = null;
    private Integer timeout = null;
    
    private Integer portC = null;
    private byte[] serverBytes = new byte[0];
    
    private final int BUFF_MAX = 1024;
    
    //constructor MyRunnable
    public MyRunnable(Socket socket) {
        this.socket = socket;        
    }

    //run method for MyRunnable
    public void run() {
        try {
            String input = getInput();
            // System.out.println(input);
            if (!checkRequest(input)) {
                getParameters(input);
                processRequest();
            } else {
                str.append("HTTP/1.1 400 Bad Request\r\n\r\n");
            }
    
        } catch (Exception e) {
            //System.err.println("Error occurred while processing the request!:");
            //System.err.println("Request details: " + input);
            e.printStackTrace();
        }
         finally {
            closeSocket();
        }
    }

    //get input from socket and return it as a string 
    private String getInput() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream input = socket.getInputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) != -1) {
            output.write(buffer, 0, length);
            if (input.available() == 0) {
                break;
            }
        }
        output.flush();
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(output.toByteArray()), StandardCharsets.UTF_8)).readLine();
    }
    
    //check if the request is valid or not 
    private boolean checkRequest(String inpuString) {
        String[] requestMethod = inpuString.split(" ");
        return (!requestMethod[0].equals("GET") || !inpuString.contains("HTTP/1.1"));
    }

    //get parameters from the request and set the variables
    private void getParameters(String input) throws Exception {
        String requestMethod = input.split(" ")[1];
        String[] requestPath = requestMethod.split("\\?");

        //Same code from previous assignment, HTTPAsk.java 
        if (requestPath.length > 0 && requestPath[0].equals("/ask")) {
            if (requestPath.length < 2) {
                str.append("HTTP/1.1 400 Bad Request\r\n");
                throw new Exception("Bad Request");
            }

            String[] pList = requestPath[1].split("&");

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
        } else {
            String errorMessage = "Not Found";
            str.append("HTTP/1.1 404 " + errorMessage + "\r\n");
            throw new Exception(errorMessage);
        }

        if (hostname.equals("") || portC == 0) {str.append("HTTP/1.1 400 Bad Request\r\n");
                            String errorReq = "Invalid request: hostname or port not found";
                            throw new Exception(errorReq);
        }
    }

    //process the request and send it to the server 
    private void processRequest() throws Exception {
        TCPClient client = new TCPClient(shutdown, timeout, limit);
    
        String requestMethod = null;
        try {
            requestMethod = new String(client.askServer(hostname, portC, serverBytes));
        } catch (IOException e) {
            str.append("HTTP/1.1 500 Internal Server Error\r\n");
            throw new Exception("Internal Server Error", e);
        }
        str.append("HTTP/1.1 200 OK\r\n\r\n");

        str.append(requestMethod);
    }
    
    //close the socket 
    private void closeSocket() {
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
            outputStream.write(str.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            //System.err.println("Error writing to socket: " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                socket.close();
            } catch (IOException e) {
                //System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}