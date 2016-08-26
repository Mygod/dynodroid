package edu.gatech.dynodroid.deviceEvent;

import edu.gatech.dynodroid.devHandler.ADevice;
import edu.gatech.dynodroid.hierarchyHelper.DeviceActionPerformer;

public class SmsReceived extends NonMonkeyEvent {
	@Override
	public boolean triggerAction(ADevice targetDevice,DeviceActionPerformer performer) {
		if(targetDevice != null){
			return targetDevice.sendSMS("6789077112", "Come over! lets have a coffe");
		}
		return false;
	}

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SmsReceived;
    }
}
