import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class PatchMap {

	int userID;
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
	}
	
	
	
	public static void main (String[] args)
	{
		Driver.CHUCK_CONNECTOR = new ChuckConnector();
		try {
			Driver.CHUCK_CONNECTOR.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PatchMap pTemp = new PatchMap(1);
		Driver.CHUCK_CONNECTOR.add("otf_05.ck");
	}
	
	
	/**
	 * Send shred to chuck connecter to be played
	 * @param shredName 
	 */
	public void sendShred(String shredName){
		Driver.CHUCK_CONNECTOR.add(shredName);
		
		
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
	
	public boolean searchGestures(GestureName gName){
		//search gestureList
		if (gestureToShred.containsKey(gName))
		{
			String shredName = gestureToShred.get(gName);
			if (shredName != null)
			{
				sendShred(shredName);
			}
			
			return true;
		}
		return false;
		
		//if match found
		//query hashmap for shred to send
		//sendShred
		//else do nothing
	}
	
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}

	
	
}
