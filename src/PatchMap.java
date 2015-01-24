import java.util.ArrayList;
import java.util.HashMap;


public class PatchMap {

	int userID;
	ArrayList<GestureName> gestureList = new ArrayList<GestureName>();
	ArrayList<String> shredList; //TODO initialize <---
	HashMap<GestureName, String> gestureToShred = new HashMap<GestureName, String>();
	/*
	gestureList.add();
	
	for(GestureName g: gestureList){
		
	}
	*/
	
	/**
	 * Send shred to chuck connecter to be played
	 */
	public void sendShred(){
		
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
		//add gName to gestureList
		//add shredName to shredList
		
	}
	
	public void searchGestures(GestureName gName){
		//search gestureList
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
	public ArrayList<GestureName> getGestureList() {
		return gestureList;
	}
	public void setGestureList(ArrayList<GestureName> gestureList) {
		this.gestureList = gestureList;
	}
	
	
}
