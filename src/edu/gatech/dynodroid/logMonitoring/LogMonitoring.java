/**
 * 
 */
package edu.gatech.dynodroid.logMonitoring;

import java.util.HashMap;

import edu.gatech.dynodroid.devHandler.ADevice;
import edu.gatech.dynodroid.testHarness.WidgetSelectionStrategy;
import edu.gatech.dynodroid.utilities.FileUtilities;
import edu.gatech.dynodroid.utilities.Logger;

/**
 * @author machiry
 * 
 */
public class LogMonitoring {

	/**
	 * This method initializes the monitoring of the log for each device This
	 * method could be called multiple times
	 * 
	 * @param targetDevice
	 *            device on which monitoring clients need to be initialized
	 * @param baseWorkDir
	 *            base working directory
	 * @param kernelModules
	 *            folder in which all the required kernel modules are present
	 * @return true/false This depends on whether the monitoring is successful
	 *         or not
	 */
	public static boolean initializeMonitoring(ADevice targetDevice,
			String baseWorkDir, String kernelModules,
			WidgetSelectionStrategy feedBack) {
		boolean allSuccess = true;
		if (targetDevice != null && baseWorkDir != null
				&& kernelModules != null) {
			allSuccess = true;
			FileUtilities.createDirectory(baseWorkDir);
		}
		return true;
	}

	/***
	 * 
	 * @param cleanTheLogs
	 * @return
	 */
	public static boolean stopMonitoring(ADevice targetDevice,
			boolean cleanTheLogs) {
		boolean allSucess = true;
		try {
			if (targetDevice != null) {
				allSucess = true;
				targetDevice.stopLogMonitoring();
			}
		} catch (Exception e) {
			Logger.logException(e);
			allSucess = false;
		}
		return allSucess;
	}

	public static boolean startMonitoring(ADevice targetDevice) {
		boolean allSucess = true;// false;
		try {
			if (targetDevice != null) {
				allSucess = true;
				targetDevice.startLogMonitoring();
			}
		} catch (Exception e) {
			Logger.logException(e);
			allSucess = false;
		}

		return allSucess;
	}

	public static boolean cleanMonitoring(ADevice targetDevice, String workDir) {
		boolean allSucess = true;// false;
		try {
			if (targetDevice != null && workDir != null) {
				allSucess = true;
				targetDevice
						.cleanLogEntries(workDir + "/GenerelLogCatLogs.log");
			}
		} catch (Exception e) {
			Logger.logException(e);
			allSucess = false;
		}

		return allSucess;
	}

}
