package edu.gatech.dynodroid.deviceEvent;

import edu.gatech.dynodroid.devHandler.ADevice;
import edu.gatech.dynodroid.hierarchyHelper.DeviceActionPerformer;
import edu.gatech.dynodroid.hierarchyHelper.IDeviceAction;

import java.util.ArrayList;

public abstract class NonMonkeyEvent implements IDeviceAction {
	public abstract boolean triggerAction(ADevice targetDevice, DeviceActionPerformer performer);

	@Override
	public ArrayList<String> getMonkeyCommand() {
		return null;
	}

	@Override
	public String actionName() {
		return null;
	}

	@Override
	public String getCallBackName() {
		return null;
	}
}
