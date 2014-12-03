package mobhoard.core;

import org.apache.logging.log4j.Logger;
import mobhoard.core.proxies.CommonProxy;
import mobhoard.handlers.ConfigHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MobHoard.MODID, version = MobHoard.VERSION, name = MobHoard.NAME)
public class MobHoard
{
    public static final String MODID = "mobhoard";
    public static final String VERSION = "MOB_HOARD_KEY";
    public static final String NAME = "MobHoard";
    public static final String PROXY = "mobhoard.core.proxies";
	
	@Instance(MODID)
	public static MobHoard instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	ConfigHandler.initConfigs(event.getSuggestedConfigurationFile());
    	proxy.registerHandlers();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
