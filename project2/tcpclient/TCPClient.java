package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
	
    private final boolean shutdown;
    private final Integer timeout;
    private final Integer limit;
    
	
    public TCPClient(boolean shutdown, Integer timeout, Integer limit){
    this.shutdown = shutdown; 
    this.timeout = timeout;
    this.limit = limit; 
    }
    
    private static int BUFF_MAX = 1024;	
    
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
   
  	Socket clientSoc = new Socket(hostname, port);
  	
  	clientSoc.getOutputStream().write(toServerBytes); //start socket

        //setup output stream
        ByteArrayOutputStream outputs = new ByteArrayOutputStream();

       //setput input stream
        InputStream inputs = clientSoc.getInputStream();
        
    	 byte[] data = new byte[BUFF_MAX];
    	 int b = 0;
  
  	if(this.shutdown == true){
  		clientSoc.shutdownOutput();
  	}
  	
  	try{	if(timeout != null){
  			clientSoc.setSoTimeout(timeout);
  		}
   	
   		if(this.limit != null){
   			int remainingBytes = limit;
   			int count = 0;
   		
   		 	while ((b = inputs.read(data, 0, Math.min(BUFF_MAX, remainingBytes))) != -1) {
                        	outputs.write(data, 0, b);
                        	count += b;
                        	remainingBytes -= b;

                        	if (remainingBytes <= 0) {
                            	break;
                        	}
                    	}
        	} else {
      				while ((b = inputs.read(data)) != -1) {
                		outputs.write(data, 0, b);
                		}		
   			}
   	}
   	
   	catch (SocketTimeoutException e) {  	 
     	}
   	
    	clientSoc.close(); 
    	
	      //create the byte array to be returned
	        byte [] r = outputs.toByteArray();
	        return r;
	}
     
}

