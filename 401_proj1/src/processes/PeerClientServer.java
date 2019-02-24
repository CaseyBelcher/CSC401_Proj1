package processes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import data_model.RFC;

public class PeerClientServer {

	
	public LinkedList<RFC> rfcIndex;
	// TODO: find correct non-local hostname 
	private static String hostName; 
	public static int portNumber = 65243;
	
	public static void main(String[] args) { 
		
		try { 
			InetAddress ip = InetAddress.getLocalHost();
			hostName = ip.getHostName();
			System.out.println("hostname: " + hostName); 
		} 
		catch(UnknownHostException e) { 
			System.err.println("Can't find localhost???");
            System.exit(1);
		}
		
		
        try (
        		
                Socket mySocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(mySocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(mySocket.getInputStream()));
            ) {
                BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
                String fromServer = "";
                String fromUser;
                 

                while ((fromUser = stdIn.readLine()) != null) {
                    // out.println(fromUser);
                    String test = "PQuery" + "\n" + 
                    							"Host: me" + "\n" + 
                    							"Cookie: 6" + "\n" + 
                    							"RFCServerPortNumber: 6" + "\n";  
                    		
                    out.println(test);
                    
                    
                    while(in.ready()) {
                    	fromServer = in.readLine(); 
                    	if(!fromServer.equals("")){
                    		System.out.println("Server: " + fromServer);
                    	}
                    	
                    	 
                    	
                    }
                     
                    
                   
                    
                }
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + hostName);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
                System.exit(1);
            }
		
	}

}
