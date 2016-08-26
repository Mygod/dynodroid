package edu.gatech.dynodroid.clients;

import edu.gatech.dynodroid.devHandler.ADevice;
import edu.gatech.dynodroid.deviceEvent.PhoneStateChanged;
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
public class AudioFocusRetriever extends NonUiActionsRetriever {
	private static final Pattern PATTERN_AUDIO_FOCUS = Pattern.compile(" -- pack: (.*) -- ");

	@Override
	protected Collection<Pair<ViewElement, IDeviceAction>> getActions(ADevice device, String packageName,
																	  Logger logger) {
		ArrayList<String> output = device.executeShellCommand("dumpsys audio");
		boolean started = false;
		for (String line : output)
			if (started) {
				if (line.isEmpty()) break;
				Matcher matcher = PATTERN_AUDIO_FOCUS.matcher(line);
				if (matcher.matches()) {
					if (packageName.equals(matcher.group(1))) return Collections.singletonList(
							new Pair<ViewElement, IDeviceAction>(null, new PhoneStateChanged()));
				} else logger.logError("AudioFocusRetriever", "Match failed for line: " + line);
			} else if ("Audio Focus stack entries (last is top of stack):".equals(line)) started = true;
		return null;
	}
}
