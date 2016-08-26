package edu.gatech.dynodroid.deviceEvent;

import edu.gatech.dynodroid.devHandler.ADevice;
import edu.gatech.dynodroid.hierarchyHelper.DeviceActionPerformer;

import java.util.Random;

/**
 * @author Mygod
 */
public class RandomMediaKeyEvent extends KeyEvent {
    private static final Random RANDOM = new Random();

    @Override
    public boolean triggerAction(ADevice targetDevice, DeviceActionPerformer performer) {
        targetKeyCode = KeyEvent.mediaButtons.get(RANDOM.nextInt(KeyEvent.mediaButtons.size()));
        return super.triggerAction(targetDevice, performer);
    }
}
