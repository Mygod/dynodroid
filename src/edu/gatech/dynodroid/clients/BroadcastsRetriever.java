package edu.gatech.dynodroid.clients;

import edu.gatech.dynodroid.android.AuthorityEntry;
import edu.gatech.dynodroid.android.BroadcastFilter;
import edu.gatech.dynodroid.android.PatternMatcher;
import edu.gatech.dynodroid.appHandler.AndroidManifestParser;
import edu.gatech.dynodroid.appHandler.BroadcastReceiver;
import edu.gatech.dynodroid.devHandler.ADevice;
import edu.gatech.dynodroid.deviceEvent.BroadcastIntent;
import edu.gatech.dynodroid.deviceEvent.PhoneStateChanged;
import edu.gatech.dynodroid.deviceEvent.SmsReceived;
import edu.gatech.dynodroid.hierarchyHelper.IDeviceAction;
import edu.gatech.dynodroid.hierarchyHelper.ViewElement;
import edu.gatech.dynodroid.testHarness.WidgetSelectionStrategy;
import edu.gatech.dynodroid.utilities.Logger;
import edu.gatech.dynodroid.utilities.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Mygod
 */
public class BroadcastsRetriever extends NonUiActionsRetriever {
	private static final Pattern
			PATTERN_APP = Pattern.compile("^    app=(\\d+):(.*?)(:.*)?/(\\d+|u\\d+[asi]\\d+) pid=\\1 uid=(\\d+) user=(\\d+)$");
	private static final HashSet<String>
			DEFAULT_SCHEMES = new HashSet<>();
	static {
		DEFAULT_SCHEMES.add("");
		DEFAULT_SCHEMES.add("content");
		DEFAULT_SCHEMES.add("file");
	}

	@Override
    protected Collection<Pair<ViewElement, IDeviceAction>> getActions(ADevice device, String packageName,
                                                                      Logger logger) {
		ArrayList<String> output = device.executeShellCommand("dumpsys activity broadcasts");
		int state = 0;
		BroadcastFilter filter = null;
        HashSet<Pair<ViewElement, IDeviceAction>> result = new HashSet<>();
		for (String line : output) {
			// formatting filters
			if (state == 3) if (filter.takeDump(line)) continue; else if (line.startsWith("      ")) {
				logger.logWarning("BroadcastsRetriever", "Unknown data: " + line);
				continue;
			} else {
				inflatePossibleActions(filter, packageName, null, result);
				filter = null;
				state = 2;
			}
			if (state == 2) if (line.startsWith("    Filter #")) {	// awaiting filters
				filter = new BroadcastFilter();
				state = 3;
				continue;
			} else state = 0;
			if (state == 1) {	// check if it's the correct package
				Matcher matcher = PATTERN_APP.matcher(line);
				state = matcher.matches() && packageName.equals(matcher.group(2)) ? 2 : 0;
				continue;
			}
			// state 0: awaiting ReceiverList
			if (line.startsWith("  * ReceiverList{")) state = 1;
			else if (line.length() == 0) break;
		}
		if (state == 3) inflatePossibleActions(filter, packageName, null, result);
		return result;
	}

    public static HashSet<Pair<ViewElement, IDeviceAction>> getManifestReceiverActions(
            AndroidManifestParser manifest, WidgetSelectionStrategy targetStrategy) {
        HashSet<Pair<ViewElement, IDeviceAction>> result = new HashSet<>();
        if (manifest != null && targetStrategy != null)
            for (BroadcastReceiver receiver : manifest.getAllBroadcastReceivers())
                for (BroadcastFilter filter : receiver.filters)
                    inflatePossibleActions(filter, manifest.getAppPackage(), receiver.componentName, result);
        return result;
    }

	private static void inflatePossibleActions(BroadcastFilter filter, String packageName, String componentName,
                                               HashSet<Pair<ViewElement, IDeviceAction>> result) {
		HashSet<String> schemes = filter.schemes, datas, types;
		if (filter.types.isEmpty() && schemes.isEmpty()) datas = types = NULL; else {
			if (schemes.isEmpty()) schemes = DEFAULT_SCHEMES;
			datas = new HashSet<>();
			for (PatternMatcher ssp : filter.ssps) for (String scheme : schemes)
				datas.add(scheme.isEmpty() ? getMatchingString(ssp) : scheme + ':' + getMatchingString(ssp));
			if (!filter.authorities.isEmpty()) {
				Set<String> paths = filter.paths.isEmpty() ? EMPTY
						: filter.paths.stream().map(BroadcastsRetriever::getMatchingString).collect(Collectors.toSet());
				for (String scheme : schemes) if (!scheme.isEmpty())
					for (AuthorityEntry authority : filter.authorities) for (String path : paths) {
						StringBuilder url = new StringBuilder(scheme);
						url.append("://");
						url.append(authority.host);
						if (authority.port >= 0) {
							url.append(':');
							url.append(authority.port);
						}
						url.append(path);
						datas.add(url.toString());
					}
			}
			types = filter.types.isEmpty() ? NULL : filter.types;
		}
		for (String action : filter.actions) for (String data : datas) for (String type : types) {
			IDeviceAction a = examine(new BroadcastIntent(packageName, componentName, action, data, type,
                    filter.categories));
			if (a != null) result.add(new Pair<>(null, a));
		}
	}

	private static IDeviceAction examine(BroadcastIntent intent) {
		switch (intent.action) {
			case "android.appwidget.action.APPWIDGET_UPDATE":
			case "android.media.AUDIO_BECOMING_NOISY":
			case "android.intent.action.BATTERY_CHANGED":
			case "android.intent.action.BATTERY_LOW":
			case "android.intent.action.BATTERY_OKAY":
			case "android.intent.action.BOOT_COMPLETED":
			case "android.net.conn.CONNECTIVITY_CHANGE":
			case "android.intent.action.DATE_CHANGED":
			case "android.intent.action.INPUT_METHOD_CHANGED":
			case "android.intent.action.MEDIA_EJECT":
			case "android.intent.action.MEDIA_MOUNTED":
			case "android.intent.action.MEDIA_SCANNER_FINISHED":
			case "android.intent.action.MEDIA_UNMOUNTED":
			case "android.intent.action.NEW_OUTGOING_CALL":
			case "android.intent.action.PACKAGE_ADDED":
			case "android.intent.action.PACKAGE_REMOVED":
			case "android.intent.action.PACKAGE_REPLACED":
            case "android.intent.action.ACTION_POWER_CONNECTED":    // todo: replace these with other better adb commands
			case "android.intent.action.ACTION_POWER_DISCONNECTED":
			case "android.intent.action.ACTION_SHUTDOWN":
			case "android.intent.action.TIME_SET":
            case "android.intent.action.TIMEZONE_CHANGED":
            case "android.intent.action.UMS_CONNECTED":
            case "android.intent.action.UMS_DISCONNECTED":
            case "android.intent.action.USER_PRESENT":
				return intent;
            case "android.intent.action.PHONE_STATE": return new PhoneStateChanged();   // TODO: what about the rest of the data in intent?
            case "android.provider.Telephony.SMS_RECEIVED": return new SmsReceived();   // TODO: what about the rest of the data in intent?
			default: return null;   // TODO: doesn't support that yet
		}
	}

	private static String getMatchingString(PatternMatcher matcher) {
		if (matcher.type == 2) {
			// PatternMatcher supports [.*\]. So what we need to do is to ignore '.'s, throw away '*'s and unescape '\'s
			StringBuilder builder = new StringBuilder();
			boolean escaped = false;
			for (char c : matcher.path.toCharArray())
				if (escaped) {
					builder.append(c);
					escaped = false;
				} else switch (c) {
					case '*':
						break;
					case '\\':
						escaped = true;
						break;
					default:
						builder.append(c);
						break;
				}
			return builder.toString();
		} else return matcher.path;
	}
}
