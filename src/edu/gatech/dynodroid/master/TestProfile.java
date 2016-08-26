package edu.gatech.dynodroid.master;

import java.util.UUID;

public class TestProfile {
	public String baseWorkingDir = "";
	public int touchPercentage = 50;
	public int smallNavigationPercentage = 20;
	public int majorNavigationPercentage = 0;
	public int trackballPercentage = 30;
	public long delayBetweenEvents = 500;
	public int eventCount = 100;
	public int verboseLevel = 3;
	public long responseDelay = 3000;
        public long appStartUpDelay = 8000;
	public String sdkInstallPath = null;
	public String baseAppDir = "";
	public int maxNoOfWidgets = 10000;
	public String testStrategy = "";
	public String appName = "";
	public String widgetSelectionStrategy = "GraphBased";
	public String targetEmailAlias = null;
	public UUID requestUUID = null;
	
	public String baseLogDir = null;

	@Override
	public String toString() {
		return this.appName + ":" + this.testStrategy + ":"
				+ this.widgetSelectionStrategy + ":" + this.appName + ":"
				+ this.baseWorkingDir + ":" + this.eventCount;
	}

}
