package edu.gatech.dynodroid.devHandler;

import java.util.*;

import com.android.ddmlib.*;

import edu.gatech.dynodroid.utilities.Logger;

/**
 * This class is the abstract class for Android Device different implementations
 * (a.k.a for real device or for emulator) should use this base class to
 * implement the functionality
 * 
 * @author machiry
 * 
 */
public class ADevice {

	private static final HashMap<String, ADevice> assignedDevices = new HashMap<>();

	/**
	 * This is the lock object to access all the static objects Access to all
	 * static objects should be guarded by this object
	 */
	private static final Object sSync = new Object();
	private static boolean adbInitialized = false;
	private static AndroidDebugBridge adbBridge = null;

	/***
	 * This method gets the name of the free device that can be used for testing
	 * 
	 * @return name of the free device
	 */
	public static ADevice getFreeDevice() {
		ADeviceSetup.isInitialized();
		initializeBridge();
		synchronized (sSync) {

			// get the current devices and return the device that is not
			// assigned
			IDevice[] recognizedDevices = adbBridge.getDevices();
			if (recognizedDevices != null) {
				for (IDevice currDev : recognizedDevices) {
					if (currDev.isOnline()
							&& !assignedDevices.containsKey(currDev.toString())) {
						ADevice result = new ADevice(currDev);
						assignedDevices.put(currDev.toString(), result);
						return result;
					}
				}
			}

			return null;

		}
	}

	public static LogCatObserver getLogCatObserver(String devName) {
		if (devName != null) {
			synchronized (assignedDevices) {
				if (assignedDevices.containsKey(devName)) return assignedDevices.get(devName).getLogCatObserver();
			}

		}
		return null;
	}

	private static boolean initializeBridge() {
		synchronized (sSync) {
			if (!adbInitialized) {
				try {
					AndroidDebugBridge.init(false);
					//AndroidDebugBridge.init(true);
					adbBridge = AndroidDebugBridge.createBridge(
							ADeviceSetup.adbPath, true);
					Thread.sleep(ADeviceSetup.timeForAdbInitialization);
					
					//New Changes
					//AndroidDebugBridge.addClientChangeListener(new ClientChangeListener());
					/*try{
					ClientData.setMethodProfilingHandler(new MethodProfileHandler(new TextLogger(PropertyParser.baseWorkingDir+"/MethodProfilerLog.txt")));
					} catch(Exception e){
						Logger.logError("Problem occured while setting the profile handler");
						Logger.logException(e);
					}	*/				
					adbInitialized = true;
				} catch (Exception e) {
					Logger.logException(e);
				}
			}
			return adbInitialized;
		}
	}

	/*private static boolean isDeviceBusy(String deviceName) {
		assert (initializeBridge());
		synchronized (sSync) {
			return assignedDevices.contains(deviceName);
		}
	}*/

	public static IDevice getIDevice(String deviceName) {
		assert (initializeBridge());
		//assert (!isDeviceBusy(deviceName));
		synchronized (sSync) {
			IDevice targetDevice = null;
			IDevice[] recognizedDevices = adbBridge.getDevices();
			for (IDevice currDev : recognizedDevices) {
				if (currDev.isOnline()
						&& currDev.toString().equalsIgnoreCase(deviceName)) {
					targetDevice = currDev;
					break;
				}
			}
			return targetDevice;
		}
	}

	private IDevice androidDevice = null;
	private static final int defaultShellCommandTimeOut = 10000;
	private String deviceName;

	//Just to be on safer side we limit defaults to more than maximum values
	//TODO: this is definitely not right
	public static final int maxEmulatorWidth = 500;
	public static final int maxEmulatorHeight = 1000;

	private LogCatObserver targetLogCatObserver = null;

	public ADevice(IDevice device) {
		androidDevice = device;
		this.deviceName = device.toString();
		if (deviceName != null && this.targetLogCatObserver == null) {
			this.targetLogCatObserver = new LogCatObserver(deviceName);
		}
		this.targetLogCatObserver.startMonitoring();
	}

	public LogCatObserver getLogCatObserver() {
		return targetLogCatObserver;
	}

	/***
	 * This methods gets the file from the device and saves it to the provided
	 * locationOnDisc
	 * 
	 * @param locationOnDevice
	 *            on device absolute location
	 * @param locationOnDisc
	 *            on disc absolute location
	 * @return true/false depending on whether the pull is sucessfull or not
	 *         respectively
	 */
	public boolean getFileFromDevice(String locationOnDevice,
									 String locationOnDisc) {
		boolean gotFile = false;
		try {
			this.androidDevice.pullFile(locationOnDevice, locationOnDisc);
			gotFile = true;
		} catch (Exception e) {

		}

		return gotFile;
	}

	/***
	 * This methods puts the file from the disc and saves it to the provided
	 * locationOnDevice
	 * 
	 * @param locationOnDisc
	 *            on disc absolute location of the file to be put
	 * @param locationOnDevice
	 *            on device absolute location
	 * @return true/false depending on whether the push is sucessfull or not
	 *         respectively
	 */
	public boolean putFileInToDevice(String locationOnDisc,
									 String locationOnDevice) {
		boolean placedFile = false;
		try {
			this.androidDevice.pushFile(locationOnDisc, locationOnDevice);
			placedFile = true;
		} catch (Exception e) {

		}
		return placedFile;
	}

	/***
	 * This method executes the provided command on device using shell
	 * 
	 * @param command
	 *            command to be executed on device
	 * @param timeOutInMilliSec
	 *            timeout for command to respond
	 * @return response of the shell command executed
	 */
	public ArrayList<String> executeShellCommand(String command,
												 int timeOutInMilliSec) {
		ArrayList<String> output = null;
		int retryCount = 4;
		boolean shellResponsive = true;
		StringOutputReceiver outputReceiver = new StringOutputReceiver();
		while (shellResponsive && (retryCount > 0)) {
			try {
				this.androidDevice.executeShellCommand(command, outputReceiver,
						timeOutInMilliSec);
				break;
			} catch (ShellCommandUnresponsiveException e) {
				Logger.logException("ShellCommandUnRespeonsive..will be retried");
				retryCount--;
			} catch (Exception e) {
				// shellResponsive = false;
				Logger.logException(e);
				retryCount--;
			}
		}
		output = outputReceiver.output;
		return output;
	}

	/***
	 * This method executes the provided command on device using shell and with
	 * default timeout value
	 * 
	 * @param command
	 *            command to be executed on device
	 * @return response of the shell command executed
	 */
	public ArrayList<String> executeShellCommand(String command) {
		return executeShellCommand(command, defaultShellCommandTimeOut);
	}

	/***
	 * This method returns the device name that this object is handling
	 * 
	 * @return human readable device name
	 */
	public String getDeviceName() {
		return this.deviceName;
	}

	/***
	 * This method sends SMS to the device with the given number and message
	 * 
	 * @param number
	 *            The number from which the SMS needs to be sent
	 * @param message
	 *            The target message that needs to be sent
	 * @return true on success or false on failure
	 */
	public boolean sendSMS(String number, String message) {
		boolean retValue = false;
		try {
			int portNo = Integer.parseInt(getDeviceName().substring(
					getDeviceName().indexOf('-') + 1));
			DeviceConnection devC = new DeviceConnection(androidDevice, portNo);
			devC.sendCommand("sms send " + number + " " + message);
			retValue = true;
			devC.close();
		} catch (Exception e) {
			Logger.logException(getDeviceName() + ":SendSMS:" + e.getMessage());
		}
		return retValue;
	}

	/***
	 * This method is used to un install the provided app package form the
	 * device
	 * 
	 * @param targetPackageName
	 *            that target package that needs to be un installed
	 * @return output after un installing the app package from the device
	 */
	public String uninstallAppPackage(String targetPackageName) {
		String output = "";
		int retryCount = 4;
		boolean shellResponsive = true;
		while (shellResponsive && (retryCount > 0)) {
			try {
				output = this.androidDevice.uninstallPackage(targetPackageName);
				output = output == null ? "Success" : output;
				break;
			} catch (InstallException e) {
				Logger.logException("InstallException..will be retried");
				retryCount--;
			} catch (Exception e) {
				shellResponsive = false;
				Logger.logException(e);
			}
		}

		return output;
	}

	/***
	 * This method is used to create TCP forward from host machine to the device
	 * 
	 * @param srcPortNumber
	 *            src port number of the host
	 * @param dstPortNumber
	 *            dst port number of the device
	 * @return true on success or false on failure
	 */
	public boolean createForward(int srcPortNumber, int dstPortNumber) {
		try {
			this.androidDevice.createForward(srcPortNumber, dstPortNumber);
			return true;
		} catch (Exception e) {
			Logger.logException(e);
		}
		return false;
	}

	private LogCatObserver getTargetObserver() {
		return getLogCatObserver();
	}

	public boolean destroyDevice() {
		synchronized (assignedDevices) {
			return assignedDevices.remove(deviceName) != null;
		}
	}

	public void startLogMonitoring() {
		LogCatObserver targetObserver = getTargetObserver();
		if (targetObserver != null) {
			targetObserver.startMonitoring();
		}

	}

	public void stopLogMonitoring() {
		LogCatObserver targetObserver = getTargetObserver();
		if (targetObserver != null) {
			targetObserver.stopMonitoring();
		}
	}

	public void cleanLogEntries(String fileName) {
		LogCatObserver targetObserver = getTargetObserver();
		if (targetObserver != null) {
			targetObserver.cleanLogEntries(fileName);
		}
	}

	public boolean installApk(String apkFile, boolean reinstall) {
		int retryCount = 4;
		boolean shellResponsive = true;
		boolean retVal = false;
		while (shellResponsive && (retryCount > 0)) {
			try {
				retryCount--;
				String output = this.androidDevice.installPackage(apkFile,
						reinstall);
				output = output == null ? "Success" : output;
				retVal = true;
				break;
			} catch (InstallException e) {
				Logger.logException("InstallException..will be retried");
			} catch (Exception e) {
				Logger.logException(e);
			}
		}
		return retVal;
	}

	public DeviceConnection getDeviceConnection() {
		DeviceConnection retVal = null;
		try {
			int portNo = Integer.parseInt(getDeviceName().substring(
					getDeviceName().indexOf('-') + 1));
			retVal = new DeviceConnection(androidDevice, portNo);
		} catch (Exception e) {
			Logger.logException(getDeviceName() + ":GetDeviceConnection:"
					+ e.getMessage());
		}
		return retVal;
	}

	public boolean executeDeviceCommand(String command) {
		boolean retValue = false;
		try {
			int portNo = Integer.parseInt(getDeviceName().substring(
					getDeviceName().indexOf('-') + 1));
			DeviceConnection devC = new DeviceConnection(androidDevice, portNo);
			devC.sendCommand(command);
			retValue = true;
			devC.close();
		} catch (Exception e) {
			Logger.logException(getDeviceName() + ":executeDeviceCommand:" + e.getMessage());
		}
		return retValue;
	}

	@Override
	public int hashCode() {
		return this.deviceName.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ADevice) {
			ADevice that = (ADevice) o;
			return this.deviceName.equals(that.getDeviceName());
		}
		return false;
	}

}

class StringOutputReceiver extends MultiLineReceiver {

	ArrayList<String> output = null;

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processNewLines(String[] arg0) {
		if (output == null) {
			output = new ArrayList<>();
		}
		for (String s : arg0) {
			output.add(s);
		}

	}
}
