package data_model;


public class PeerRecord {

	/** hostname of peer */ 
	public String hostname; 
	/** cookie for this registered peer */ 
	public int cookie; 
	/** whether the peer is still in the system */ 
	public boolean active; 
	/** Time To Live */ 
	public int ttl; 
	/** port number the RFC server is listening to */ 
	public int portNumber; 
	/** number of times the peer has registered during the last 30 days */ 
	public int activityNumber; 
	/** last time /date the peer registered */ 
	public String lastRegisteredDate; 
	
	
	
	public PeerRecord() { 
		
		
	}
	
	
}
