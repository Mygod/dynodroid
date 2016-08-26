package edu.gatech.dynodroid.deviceEvent;

import edu.gatech.dynodroid.devHandler.ADevice;
import edu.gatech.dynodroid.hierarchyHelper.DeviceActionPerformer;
import edu.gatech.dynodroid.utilities.Logger;

public class PhoneStateChanged extends NonMonkeyEvent {
	@Override
	public boolean triggerAction(ADevice targetDevice,DeviceActionPerformer performer) {
		String targetNumber = "6789077112";
		if (targetDevice != null) {
			try {
				if (targetDevice.executeDeviceCommand("gsm call "
						+ targetNumber)) {
					Logger.logInfo("Generating Call on device:"+targetDevice.toString());
					Thread.sleep(6000);
					if (targetDevice.executeDeviceCommand("gsm cancel "
							+ targetNumber)) {
						Logger.logInfo("Cancelling call on device:"+targetDevice.toString());
						return true;
					} else{
						Logger.logError("Problem occured while trying to cancel the call on device:"+targetDevice.toString());
					}
				} else{
					Logger.logError("Problem occured while trying to make call on device:"+targetDevice.toString());
				}
			} catch (Exception e) {
				Logger.logException(e);
			}

		}
		return false;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof PhoneStateChanged;
	}
}
