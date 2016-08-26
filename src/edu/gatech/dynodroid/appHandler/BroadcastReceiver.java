package edu.gatech.dynodroid.appHandler;

import edu.gatech.dynodroid.android.BroadcastFilter;

import java.util.ArrayList;

/***
 * 
 * @author machiry
 *
 */
public class BroadcastReceiver {
	public String componentName;
	public ArrayList<BroadcastFilter> filters = new ArrayList<>();

	@Override
	public String toString() {
		String retVal = null;
		if (componentName != null) {
			retVal = "Receiver:" + componentName;
		}
		if (retVal == null) {
			retVal = "No Receiver";
		}
		retVal +="\n";
		for (BroadcastFilter f : filters) {
			retVal += "\t\tIntentFilter:" + f.toString()+"\n";
		}
		return retVal;
	}
}
