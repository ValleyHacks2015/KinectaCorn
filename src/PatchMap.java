import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class PatchMap {
	boolean DEBUG = true;
	int userID;
	int mapping;
	private String lastGesture;
	ChuckConnector chuckConnector;
	//ArrayList<GestureName> gestureList = new ArrayList<GestureName>();
	//ArrayList<String> shredList; //TODO initialize <---
	HashMap<GestureName, String> gestureToShred = new HashMap<GestureName, String>();
	/*
	gestureList.add();
	
	for(GestureName g: gestureList){
		
	}
	*/
	
	public PatchMap(int userID)
	{
		this.userID = userID;
		mapping = 1;
		
		chuckConnector = new ChuckConnector();
		//System.out.println("Specify ip adress for chuck server (leave blank for localhost):");
		//Scanner in = new Scanner(System.in);
		//String ip = in.next();
		//if (!ip.equals("")) chuckConnector.setIp(ip);
		loadDefaultMapping();
		if(DEBUG) System.out.println("Loading default mapping");
	}
	
	
	
	public void loadDefaultMapping() {
		
		//TODO add defaults to rethinkDB
		if(mapping == 1){
			gestureToShred.put(GestureName.RH_DOWN, "lead.ck");
			gestureToShred.put(GestureName.RH_OUT, "break_lead.ck");
			gestureToShred.put(GestureName.LH_DOWN, "harmony.ck");
			gestureToShred.put(GestureName.LEAN_LEFT, "LiSa-munger2.ck");
			gestureToShred.put(GestureName.LH_OUT, "harmony2.ck");
			gestureToShred.put(GestureName.LEAN_RIGHT, "powerup.ck");
		} else if(mapping == 2){
			gestureToShred.put(GestureName.RH_DOWN, "drum_beat.ck");
			gestureToShred.put(GestureName.RH_OUT, "drum_break.ck");
			gestureToShred.put(GestureName.LH_DOWN, "bass_hit_trip.ck");
			gestureToShred.put(GestureName.LH_DOWN, "powerup.ck");
			gestureToShred.put(GestureName.LEAN_RIGHT, "harmony.ck");
			gestureToShred.put(GestureName.LEAN_LEFT, "harmony2.ck");
		} else if(mapping == 3) {
			gestureToShred.put(GestureName.RH_DOWN, "LiSa-munger2.ck");
			gestureToShred.put(GestureName.LH_DOWN, "powerup.ck");
			gestureToShred.put(GestureName.LH_OUT, "pad2.ck");
			
		}
		mapping++;
		if(mapping > 1){
			mapping = 1;
		}
		
	}



	
	
	/**
	 * Send shred to chuck connecter to be played
	 * @param shredName 
	 */
	public void sendShred(String shredName){
		chuckConnector.add(shredName);
		
		
	}
	
	
	/**
	 * Add new mapping: gesture to shred relationship
	 * @param gName
	 * @param shredName
	 * 
	 * 
	 */
	public void addMap(GestureName gName, String shredName){
		// add relation to hashmap
		gestureToShred.put(gName, shredName);
		//gestureList.add(gName);
		//shredList.add(shredName);
		
		//add gName to gestureList
		//add shredName to shredList
		
	}
	
	public String checkGestures(GestureName gName){
		//search gestureList
		lastGesture = gName.toString();
		if (gestureToShred.containsKey(gName))
		{
			String shredName = gestureToShred.get(gName);
			sendShred(shredName);
			return shredName;
			
			
		}
		return null;
		
		
	}
	
	public void addShred(String shredName){
		if (shredName != null)
		{
			sendShred(shredName);	
		}
	}
	
	public String getLastGesture(){
		return lastGesture;
	}
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}



	public int getMapping() {
		return mapping;
	}



	public void setMapping(int mapping) {
		this.mapping = mapping;
	}

	
	
}
