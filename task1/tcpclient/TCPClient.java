package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    public TCPClient() {
    }

    private static int BUFFERSIZE = 1024;
    
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        //create the socket and write to server
        Socket client =  new Socket(hostname, port);

        client.getOutputStream().write(toServerBytes);

        //setup output stream
        ByteArrayOutputStream outputs = new ByteArrayOutputStream();

        //setput input stream
        InputStream inputs = client.getInputStream();
        //ByteArrayInputStream inputs = new ByteArrayInputStream();

        //set byte buffer for receiving
        byte[] fromServer = new byte[BUFFERSIZE];
        int b = 0;
        while((b = inputs.read(fromServer)) != -1){   //read one byte at a time
               outputs.write(fromServer, 0, b);   //write to output buffer
        }

        client.close();

        //create the byte array to be returned
        byte [] r = outputs.toByteArray();

        return r;
    }
}
