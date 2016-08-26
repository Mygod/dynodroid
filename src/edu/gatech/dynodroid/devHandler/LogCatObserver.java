package edu.gatech.dynodroid.devHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.gatech.dynodroid.utilities.FileUtilities;
import edu.gatech.dynodroid.utilities.Logger;

public class LogCatObserver {
	public String deviceName;
	public LogCatObserverThread targetThread;

	public LogCatObserver(String devName) {
		this.deviceName = devName;
		this.targetThread = new LogCatObserverThread(deviceName);
		Thread newThread = new Thread(this.targetThread);
		newThread.start();
	}

	public void startMonitoring() {
		this.targetThread.startMonitoring();
	}

	public void stopMonitoring() {
		this.targetThread.stopMonitoring();
	}

	public void cleanLogEntries(String fileName) {
		this.targetThread.cleanLogEntries(fileName);
	}

}

class LogCatObserverThread implements Runnable {

	public boolean monitor = false;
	private final Object sync = new Object();
	private ArrayList<String> logEntries = new ArrayList<String>();
	public boolean isMontoringPossible = false;
	public boolean quitMonitoring = false;

	public String deviceName;

	// public ADevice targetDevice;

	public LogCatObserverThread(String devName) {
		this.deviceName = devName;
	}

	@Override
	public void run() {
		Process theProcess = null;
		BufferedReader inStream = null;
		String tempStr = null;
		String javaCommandLine = "adb -s " + this.deviceName + " logcat";
		boolean allFine = true;
		try {
			theProcess = Runtime.getRuntime().exec(javaCommandLine);
			Logger.logInfo("Running_M3:"+javaCommandLine);
		} catch (IOException e) {
			Logger.logException(e);
			allFine = false;
		}

		// read from the called program's standard output stream
		try {
			if (allFine) {
				this.isMontoringPossible = true;
				inStream = new BufferedReader(new InputStreamReader(
						theProcess.getInputStream()));
				while (!(this.quitMonitoring)
						&& ((tempStr = inStream.readLine()) != null)) {
					// Logger.logInfo(tempStr);
					synchronized (sync) {
						if (this.monitor) {
							logEntries.add(tempStr);
						}
					}
				}
				// theProcess.destroy();
			} else {
				Logger.logError("Error occured while trying to monitor device output:"
						+ this.deviceName);
			}
		} catch (IOException e) {
			Logger.logException(e);
		}

	}

	public boolean cleanLogEntries(String fileName) {
		synchronized (sync) {
			try {
				if (fileName != null) {
					if (!FileUtilities.appendLinesToFile(fileName, logEntries)) {
						Logger.logError("Problem occured while writing entries to the provied file, provided file:"
								+ fileName == null ? "NULL" : fileName);
					}
				}
				logEntries.clear();
				return true;
			} catch (Exception e) {
				Logger.logException(e);
			}
			return false;
		}
	}

	public void startMonitoring() {
		synchronized (sync) {
			this.monitor = true;
		}
	}

	public void stopMonitoring() {
		synchronized (sync) {
			this.monitor = false;
		}
	}

}
