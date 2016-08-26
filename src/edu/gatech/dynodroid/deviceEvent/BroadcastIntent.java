package edu.gatech.dynodroid.deviceEvent;

import edu.gatech.dynodroid.devHandler.ADevice;
import edu.gatech.dynodroid.hierarchyHelper.DeviceActionPerformer;
import edu.gatech.dynodroid.utilities.Logger;

import java.util.HashSet;

/**
 * TODO: extras
 *
 [-e|--es <EXTRA_KEY> <EXTRA_STRING_VALUE> ...]
 [--esn <EXTRA_KEY> ...]
 [--ez <EXTRA_KEY> <EXTRA_BOOLEAN_VALUE> ...]
 [--ei <EXTRA_KEY> <EXTRA_INT_VALUE> ...]
 [--el <EXTRA_KEY> <EXTRA_LONG_VALUE> ...]
 [--ef <EXTRA_KEY> <EXTRA_FLOAT_VALUE> ...]
 [--eu <EXTRA_KEY> <EXTRA_URI_VALUE> ...]
 [--ecn <EXTRA_KEY> <EXTRA_COMPONENT_NAME_VALUE>]
 [--eia <EXTRA_KEY> <EXTRA_INT_VALUE>[,<EXTRA_INT_VALUE...]]
 (mutiple extras passed as Integer[])
 [--eial <EXTRA_KEY> <EXTRA_INT_VALUE>[,<EXTRA_INT_VALUE...]]
 (mutiple extras passed as List<Integer>)
 [--ela <EXTRA_KEY> <EXTRA_LONG_VALUE>[,<EXTRA_LONG_VALUE...]]
 (mutiple extras passed as Long[])
 [--elal <EXTRA_KEY> <EXTRA_LONG_VALUE>[,<EXTRA_LONG_VALUE...]]
 (mutiple extras passed as List<Long>)
 [--efa <EXTRA_KEY> <EXTRA_FLOAT_VALUE>[,<EXTRA_FLOAT_VALUE...]]
 (mutiple extras passed as Float[])
 [--efal <EXTRA_KEY> <EXTRA_FLOAT_VALUE>[,<EXTRA_FLOAT_VALUE...]]
 (mutiple extras passed as List<Float>)
 [--esa <EXTRA_KEY> <EXTRA_STRING_VALUE>[,<EXTRA_STRING_VALUE...]]
 (mutiple extras passed as String[]; to embed a comma into a string,
 escape it using "\,")
 [--esal <EXTRA_KEY> <EXTRA_STRING_VALUE>[,<EXTRA_STRING_VALUE...]]
 (mutiple extras passed as List<String>; to embed a comma into a string,
 escape it using "\,")
 [--grant-read-uri-permission] [--grant-write-uri-permission]
 [--grant-persistable-uri-permission] [--grant-prefix-uri-permission]
 [--debug-log-resolution] [--exclude-stopped-packages]
 [--include-stopped-packages]
 [--activity-brought-to-front] [--activity-clear-top]
 [--activity-clear-when-task-reset] [--activity-exclude-from-recents]
 [--activity-launched-from-history] [--activity-multiple-task]
 [--activity-no-animation] [--activity-no-history]
 [--activity-no-user-action] [--activity-previous-is-top]
 [--activity-reorder-to-front] [--activity-reset-task-if-needed]
 [--activity-single-top] [--activity-clear-task]
 [--activity-task-on-home]
 [--receiver-registered-only] [--receiver-replace-pending]
 [--selector]
 *
 * @author Mygod
 */
public class BroadcastIntent extends NonMonkeyEvent {
    public String packageName, componentName, action, dataUri, mimeType;
    public HashSet<String> categories;
    public boolean registeredOnly;

    public BroadcastIntent(String packageName, String componentName, String action, String dataUri, String mimeType,
                           HashSet<String> categories) {
        this.packageName = packageName;
        this.componentName = componentName;
        this.action = action;
        this.categories = categories;
        this.dataUri = dataUri;
        this.mimeType = mimeType;
        registeredOnly = componentName == null;
    }

    @Override
    public boolean triggerAction(ADevice targetDevice, DeviceActionPerformer performer) {
        // TODO: --user uid?
        StringBuilder command = new StringBuilder("am broadcast -a ");
        command.append(action);
        if (dataUri != null) {
            command.append(" -d ");
            command.append(dataUri);
        }
        if (mimeType != null) {
            command.append(" -t ");
            command.append(mimeType);
        }
        if (categories != null) for (String category : categories) {
            command.append(" -c ");
            command.append(category);
        }
        if (registeredOnly) command.append(" --receiver-registered-only");
        if (componentName == null) {
            command.append(' ');
            command.append(packageName);
        } else {
            command.append(" -n ");
            command.append(packageName);
            command.append('/');
            command.append(componentName);
        }
        for (String line : targetDevice.executeShellCommand(command.toString()))
            if (line.startsWith("Broadcast completed")) {
                Logger.logInfo(line);
                return true;
            }
        return false;
    }

    @Override
    public int hashCode() {
        return action.hashCode();   // TODO: other stuff?
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BroadcastIntent)) return false;
        BroadcastIntent that = (BroadcastIntent) o;
        return action.equals(that.action);
    }
}
