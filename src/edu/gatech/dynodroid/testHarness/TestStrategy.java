/**
 * 
 */
package edu.gatech.dynodroid.testHarness;

import edu.gatech.dynodroid.utilities.Logger;

/**
 * @author machiry
 * 
 */
public abstract class TestStrategy {

	protected Logger textLogger;

	/***
	 * This is the preparation step of test strategy. Here all the setup
	 * required by the test strategy needs to be performed if this method
	 * returns false then the test strategy will not be executed further
	 * 
	 * @return true/false indicating success or failure of this step.
	 */
	public abstract boolean prepare();

	/***
	 * This is the method that runs the underlying test strategy Calling of this
	 * method by framework depends on the success of the prepare call.
	 * 
	 * @return true/false indicating success of failure of this step
	 */
	public abstract boolean perform();

	/***
	 * This is the method that does the clean up required for the test strategy
	 * this method will be called only if prepare returns true, irrespective of
	 * whether perform returned false or not
	 * 
	 * @return true/false indicating success of failure of this step
	 */
	public abstract boolean cleanup();

	// Property Names for different properties that will be used by the test
	// strategy
	public static final String workDirPropertyName = "workDir";
	public static final String appSrcPropertyName = "appSrc";
	public static final String appStartUpTimeProperty = "app_start_wait";
}
