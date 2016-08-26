/**
 * 
 */
package edu.gatech.dynodroid.testHarness;

import edu.gatech.dynodroid.testHarness.ScreenBasedGraphAbstraction.GraphBasedSelectionStrategy;
import edu.gatech.dynodroid.testHarness.WidgetFrequencySelection.WidgetFrequencyBasedSelectionStrategy;
import edu.gatech.dynodroid.testHarness.WidgetRandomBiasSelection.WidgetRandomBiasBasedSelectionStrategy;
import edu.gatech.dynodroid.testHarness.WidgetRandomSelection.WidgetRandomBasedSelectionStrategy;
import edu.gatech.dynodroid.utilities.Logger;

/**
 * This is used to get the requested widget selection strategy 
 * @author machiry
 *
 */
public class StrategyFactory {
	
	public static final String widgetGraph = "GraphBased";
	public static final String frequencyBased = "FrequencyBased";
	public static final String randomBased = "RandomBased";
	public static final String randomBiasBased = "RandomBiasBased";
	
	public static WidgetSelectionStrategy getWidgetSelectionStrategy(String strategyName,String workingDir){
		if(strategyName.equals(widgetGraph)){
			try{
			return new GraphBasedSelectionStrategy(workingDir);
			} catch(Exception e){
				Logger.logException(e);
			}
		}
		if(strategyName.equals(frequencyBased)){
			try{
			return new WidgetFrequencyBasedSelectionStrategy(workingDir);
			} catch(Exception e){
				Logger.logException(e);
			}
		}
		if(strategyName.equals(randomBased)){
			try{
			return new WidgetRandomBasedSelectionStrategy(workingDir);
			} catch(Exception e){
				Logger.logException(e);
			}
		}
                if(strategyName.equals(randomBiasBased)){
			try{
			return new WidgetRandomBiasBasedSelectionStrategy(workingDir);
			} catch(Exception e){
				Logger.logException(e);
			}
		}

		return null;
	}
}
