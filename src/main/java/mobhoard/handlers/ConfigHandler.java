package mobhoard.handlers;

import java.io.File;
import mobhoard.core.MH_Settings;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler
{
	static final String catMain = "Main";
	
	public static void initConfigs(File file)
	{
		Configuration config = new Configuration(file);
		
		config.load();
		
		MH_Settings.awareness = config.getInt("Awareness radius", catMain, MH_Settings.awareness, 16, 256, "How far away hoards can see players (16-256)");
		MH_Settings.speedBoost = config.getInt("Speed boost", catMain, MH_Settings.speedBoost, -5, 5, "How much fast or slow hoards move");
		MH_Settings.lunarHoard = config.getBoolean("Lunar hoard", catMain, MH_Settings.lunarHoard, "Whether to wait for a full moon to hoard");
		MH_Settings.xrayVision = config.getBoolean("XRay hoard", catMain, MH_Settings.xrayVision, "Whether hoards can see through walls");
		MH_Settings.sendEverything = config.getBoolean("Send EVERYTHING", catMain, MH_Settings.sendEverything, "Ignore 'Hoard mob IDs' and just send every compatible mob!");
		MH_Settings.hoardMobs = config.get(catMain, "Hoard mob IDs", MH_Settings.hoardMobs, "Which mobs are involved in the hoard (AI mobs only)").getIntList();
		
		config.save();
	}
}
