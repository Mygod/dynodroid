package edu.gatech.dynodroid.clients;

import edu.gatech.dynodroid.devHandler.ADevice;
import edu.gatech.dynodroid.deviceEvent.PhoneStateChanged;
import edu.gatech.dynodroid.deviceEvent.RandomMediaKeyEvent;
import edu.gatech.dynodroid.hierarchyHelper.IDeviceAction;
import edu.gatech.dynodroid.hierarchyHelper.ViewElement;
import edu.gatech.dynodroid.utilities.Logger;
import edu.gatech.dynodroid.utilities.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mygod
 */
public class MediaButtonReceiverRetriever extends NonUiActionsRetriever {
	private static final Pattern PATTERN_MEDIA_BUTTON_RECEIVER = Pattern.compile(
			"^  mediaButtonReceiver=PendingIntent\\{[0-9a-f]+: PendingIntentRecord\\{[0-9a-f]+ (.*) broadcastIntent\\}\\}$");

	@Override
	protected Collection<Pair<ViewElement, IDeviceAction>> getActions(ADevice device, String packageName,
																	  Logger logger) {
		ArrayList<String> output = device.executeShellCommand("dumpsys media_session");	// TODO: only work on 5.0+
		for (String line : output) {
			Matcher matcher = PATTERN_MEDIA_BUTTON_RECEIVER.matcher(line);
			if (matcher.matches() && packageName.equals(matcher.group(1))) return Collections.singletonList(
					new Pair<ViewElement, IDeviceAction>(null, new RandomMediaKeyEvent()));
		}
		return null;
	}
}
