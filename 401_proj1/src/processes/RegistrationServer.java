package processes;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;

import data_model.PeerRecord;

public class RegistrationServer {

	
	public static LinkedList<PeerRecord> peerList; 
	public static int portNumber = 65243; 
	
	public static void main(String[] args) { 
		
		System.out.println("Starting RS server...");
		peerList = new LinkedList<PeerRecord>(); 
		
		
		 try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
	            while (true) {
		            new RSServerThread(serverSocket.accept(), peerList).start();
		        }
		    } 
		 catch (IOException e) {
	            System.err.println("Could not listen on port " + portNumber);
	            System.exit(-1);
	        }
		 
		 System.out.println("Terminating RS server...");
		
		
	}
	

	
	
	
	
	
	
	
	
}
