package mobhoard.handlers;

import java.lang.reflect.Field;
import mobhoard.core.MH_Settings;
import mobhoard.core.MobHoard;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler
{
	public boolean isHoardNight = false;
	public int lastHoardNight = -1;
	public static Field nearbyOnly;
	public static Field checkSight;
	public static Field targetClass;
	
	@SubscribeEvent
	public void onLiving(LivingUpdateEvent event)
	{
		if(nearbyOnly == null || checkSight == null || targetClass == null || event.entityLiving.worldObj.isRemote)
		{
			return;
		}
		
		if(!(event.entityLiving instanceof EntityLiving))
		{
			return;
		}
		
		EntityLiving entityLiving = (EntityLiving)event.entityLiving;
		
		if(!shouldHoard(event.entityLiving.worldObj))
		{
        	if(entityLiving.getEntityData().hasKey("HOARD_BASE_AWARENESS"))
        	{
		        IAttributeInstance iattributeinstance = entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
		        if(iattributeinstance != null && iattributeinstance.getBaseValue() != entityLiving.getEntityData().getDouble("HOARD_BASE_AWARENESS"))
		        {
		        	iattributeinstance.setBaseValue(entityLiving.getEntityData().getDouble("HOARD_BASE_AWARENESS"));
		        }
        	}
        	
        	try
        	{
        		if(entityLiving.getEntityData().hasKey("HOARD_BASE_XRAY") && entityLiving.getEntityData().hasKey("HOARD_BASE_NEARBY"))
        		{
					for(int j = 0; j < entityLiving.targetTasks.taskEntries.size(); j++)
					{
						if(((EntityAITaskEntry)entityLiving.targetTasks.taskEntries.get(j)).action instanceof EntityAINearestAttackableTarget)
						{
							EntityAINearestAttackableTarget nearestTargetAI = (EntityAINearestAttackableTarget)((EntityAITaskEntry)entityLiving.targetTasks.taskEntries.get(j)).action;
							
							if(targetClass.get(nearestTargetAI) == EntityPlayer.class)
							{
								checkSight.set(nearestTargetAI, entityLiving.getEntityData().getBoolean("HOARD_BASE_XRAY"));
								nearbyOnly.set(nearestTargetAI, entityLiving.getEntityData().getBoolean("HOARD_BASE_NEARBY"));
							}
						}
					}
        		}
        	} catch(Exception e)
        	{
				MobHoard.logger.log(Level.ERROR, "An error occured while manipulating " + entityLiving.getCommandSenderName() + "'s AI", e);
        	}
			return;
		}
		int eID = EntityList.getEntityID(entityLiving);
		
		for(int i = 0; i < (MH_Settings.sendEverything? 1 : MH_Settings.hoardMobs.length); i++)
		{
			if(MH_Settings.sendEverything || eID == MH_Settings.hoardMobs[i])
			{
				try
				{
					for(int j = 0; j < entityLiving.targetTasks.taskEntries.size(); j++)
					{
						if(((EntityAITaskEntry)entityLiving.targetTasks.taskEntries.get(j)).action instanceof EntityAINearestAttackableTarget)
						{
							EntityAINearestAttackableTarget nearestTargetAI = (EntityAINearestAttackableTarget)((EntityAITaskEntry)entityLiving.targetTasks.taskEntries.get(j)).action;
							
							if(targetClass.get(nearestTargetAI) == EntityPlayer.class)
							{
								if(!entityLiving.getEntityData().hasKey("HOARD_BASE_XRAY") || !entityLiving.getEntityData().hasKey("HOARD_BASE_NEARBY"))
								{
									entityLiving.getEntityData().setBoolean("HOARD_BASE_XRAY", checkSight.getBoolean(nearestTargetAI));
									entityLiving.getEntityData().setBoolean("HOARD_BASE_NEARBY", nearbyOnly.getBoolean(nearestTargetAI));
								}
								
								checkSight.set(nearestTargetAI, !MH_Settings.xrayVision);
								nearbyOnly.set(nearestTargetAI, MH_Settings.awareness <= 24);
								
								if(MH_Settings.speedBoost > 0)
								{
									entityLiving.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 300, MH_Settings.speedBoost));
								} else if(MH_Settings.speedBoost < 0)
								{
									entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 300, -MH_Settings.speedBoost));
								}
								
						        IAttributeInstance iattributeinstance = entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
						        if(iattributeinstance != null && iattributeinstance.getAttributeValue() < MH_Settings.awareness)
						        {
						        	if(!entityLiving.getEntityData().hasKey("HOARD_BASE_AWARENESS"))
						        	{
						        		entityLiving.getEntityData().setDouble("HOARD_BASE_AWARENESS", iattributeinstance.getAttributeValue());
						        	}
						        	iattributeinstance.setBaseValue((double)MH_Settings.awareness);
						        }
							}
						}
					}
				} catch(Exception e)
				{
					MobHoard.logger.log(Level.ERROR, "An error occured while manipulating " + entityLiving.getCommandSenderName() + "'s AI", e);
				}
				break;
			}
		}
	}
	
	public boolean shouldHoard(World world)
	{
		switch(MH_Settings.hoardMode)
		{
			case 0:
			{
				return world.getMoonPhase() == 0 && !world.isDaytime();
			}
			case 1:
			{
				if(MathHelper.floor_double(world.getWorldTime()/24000D) != lastHoardNight)
				{
					lastHoardNight = MathHelper.floor_double(world.getWorldTime()/24000D);
					isHoardNight = world.rand.nextInt(10) == 0;
					return isHoardNight && !world.isDaytime();
				} else
				{
					return isHoardNight && !world.isDaytime();
				}
			}
			case 2:
			{
				return !world.isDaytime();
			}
			case 3:
			{
				return true;
			}
			default:
			{
				return true;
			}
		}
	}
	
	/**
	 * Get field names for AI and set them to be accessible for future use
	 */
	static
	{
		try
		{
			nearbyOnly = EntityAITarget.class.getDeclaredField("nearbyOnly");
			checkSight = EntityAITarget.class.getDeclaredField("shouldCheckSight");
			targetClass = EntityAINearestAttackableTarget.class.getDeclaredField("targetClass");
			
			nearbyOnly.setAccessible(true);
			checkSight.setAccessible(true);
			targetClass.setAccessible(true);
		} catch(Exception e)
		{
			try
			{
				nearbyOnly = EntityAITarget.class.getDeclaredField("field_75303_a");
				checkSight = EntityAITarget.class.getDeclaredField("field_75297_f");
				targetClass = EntityAINearestAttackableTarget.class.getDeclaredField("field_75307_b");
				
				nearbyOnly.setAccessible(true);
				checkSight.setAccessible(true);
				targetClass.setAccessible(true);
			} catch(Exception e1)
			{
				MobHoard.logger.log(Level.FATAL, "Unabled to get fields for AI manipulation!", e1);
				e1.printStackTrace();
				e.printStackTrace();
				nearbyOnly = checkSight = targetClass = null;
			}
		}
	}
}
