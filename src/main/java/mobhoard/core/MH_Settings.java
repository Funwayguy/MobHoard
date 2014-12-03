package mobhoard.core;

/**
 * A container for all the configurable settings in the mod
 */
public class MH_Settings
{
	/**
	 * How far the hoard will 'look' for targets
	 */
	public static int awareness = 64;
	
	/**
	 * Sets whether mobs can see targets through walls when hoarding
	 */
	public static boolean xrayVision = true;
	
	/**
	 * Whether the hoard waits for a full moon or not
	 */
	public static boolean lunarHoard = true;
	
	/**
	 * Ignore 'hoardMobs' and just send every compatible mob
	 */
	public static boolean sendEverything = false;
	
	/**
	 * Adds speed boosts to zombies or slowness if negative
	 */
	public static int speedBoost = 2;
	
	/**
	 * List of entity IDs that will be used when calling the hoard
	 */
	public static int[] hoardMobs = new int[]{54};
}
