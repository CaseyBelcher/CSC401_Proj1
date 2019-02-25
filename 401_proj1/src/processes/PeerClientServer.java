package processes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import data_model.PeerRecord;
import data_model.RFC;

public class PeerClientServer {

	
	public LinkedList<RFC> rfcIndex;
	// TODO: find correct non-local hostname 
	private static String hostName; 
	public static int portNumber = 65243;
	public LinkedList<PeerRecord> peerList; 
	public static int cookieNumber; 
	
	public static void main(String[] args) { 
		
		try { 
			InetAddress ip = InetAddress.getLocalHost();
			hostName = ip.getHostName();
			System.out.println("hostname: " + hostName);
			cookieNumber = (new Random()).nextInt(500);
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
//                BufferedReader stdIn =
//                    new BufferedReader(new InputStreamReader(System.in));
//                 String fromServer; 
                String fromUser;
                 
                
                Scanner inputScan = new Scanner(System.in);
                while((fromUser = inputScan.next()) != null) { 
                  if(fromUser.equals("PQuery")) { 
                    pQuery(out, in); 
                  }
                }
                inputScan.close();
                
                
                
                
               
            } 
            catch (UnknownHostException e) {
                System.err.println("Don't know about host " + hostName);
                System.exit(1);
            } 
            catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
                System.exit(1);
            }
		
	}
	
	
	public static void pQuery(PrintWriter out, BufferedReader in) { 
	  String test = "PQuery" + "\n" + 
        "Host: me" + "\n" + 
        "Cookie: " + cookieNumber + "\n" + 
        "RFCServerPortNumber: 6" + "\n";  

    out.println(test);

    
    try { 
      String fromServer; 
      
      
      while ((fromServer = in.readLine()) != null) {
        Scanner servLine = new Scanner(fromServer); 
        if(servLine.hasNext()) { 
          System.out.println(servLine.next());
        }
        else { 
          break; 
        }
        servLine.close(); 
          
         
      }
     
      
    }
    catch(IOException e) { 
      
    }
    
	}
	
	
	

}
