package processes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

import data_model.PeerRecord;

//MUST ALSO BE MADE THREADSAFE when accessing 
public class RSServerThread extends Thread {
    
	private Socket socket = null;
	private static LinkedList<PeerRecord> peerList; 

    public RSServerThread(Socket socket, LinkedList<PeerRecord> myPeerList) {
        super("RSServerThread");
        this.socket = socket;
        peerList = myPeerList; 

    
    }
    
    public String processInput(String in) { 
		System.out.println("RS processing input...");
    	if(in.equals("hello")) { 
			return "done"; 
		}	
		return ""; 
	}
    
    public void run() {

        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            String inputLine, outputLine;
          
            
            while((inputLine = in.readLine()) != null) { 
            	outputLine = processInput(inputLine); 
            	out.println(outputLine);
            	
            	// set the actual end condition 
            	if(outputLine.equals("done")) {
            		break; 
            	}
            	
            }
            socket.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}