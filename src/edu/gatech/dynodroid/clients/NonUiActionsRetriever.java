package edu.gatech.dynodroid.clients;

import edu.gatech.dynodroid.devHandler.ADevice;
import edu.gatech.dynodroid.hierarchyHelper.IDeviceAction;
import edu.gatech.dynodroid.hierarchyHelper.ViewElement;
import edu.gatech.dynodroid.utilities.Logger;
import edu.gatech.dynodroid.utilities.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Mygod
 */
public abstract class NonUiActionsRetriever {
	private static final ArrayList<NonUiActionsRetriever> retrievers = new ArrayList<>();
	private static boolean retrieversInitialized = false;
    protected static final HashSet<String>
            NULL = new HashSet<>(),
            EMPTY = new HashSet<>();

    static {
        NULL.add(null);
        EMPTY.add("");
    }

	public static HashSet<Pair<ViewElement, IDeviceAction>> getAllActions(ADevice device, String packageName,
																		  Logger logger) {
		synchronized (retrievers) {
			if (!retrieversInitialized) {
                retrievers.add(new AudioFocusRetriever());
				retrievers.add(new BroadcastsRetriever());
                retrievers.add(new MediaButtonReceiverRetriever());
				// TODO: add more here
				retrieversInitialized = true;
			}
		}
		HashSet<Pair<ViewElement, IDeviceAction>> result = new HashSet<>();
		for (NonUiActionsRetriever retriever : retrievers) {
            Collection<Pair<ViewElement, IDeviceAction>> actions = retriever.getActions(device, packageName, logger);
            if (actions != null) result.addAll(actions);
        }
		return result;
	}

	protected abstract Collection<Pair<ViewElement, IDeviceAction>> getActions(ADevice device, String packageName,
                                                                               Logger logger);
}
