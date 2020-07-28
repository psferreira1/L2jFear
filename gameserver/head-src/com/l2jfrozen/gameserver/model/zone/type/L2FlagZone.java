package com.l2jfrozen.gameserver.model.zone.type;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import javolution.util.FastList;

import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Skill;
import com.l2jfrozen.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfrozen.gameserver.model.zone.L2ZoneType;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.util.random.Rnd;

/**
 * @author Strato
 * @author Elfocrash (for the correction)
 */
public class L2FlagZone extends L2ZoneType
{
    private static List<String> classes = new FastList<>();
   int _skillId;
   private int _chance;
   private int _initialDelay;
   int _skillLvl;
   private int _reuse;
   private boolean _enabled;
   private String _target;
   public static boolean  revive_noblesse, give_noblesse;
   static int[][] spawn_loc;
   public static boolean  revive_heal;
   L2Skill noblesse = SkillTable.getInstance().getInfo(1323, 1);
   private Future<?> _task;
public L2FlagZone(int id)

   {
       super(id);
       loadConfigs();
      _skillId = 1323;
      _skillLvl = 1;
      _chance = 100;
      _initialDelay = 360000000;
      _reuse = 200;
      _enabled = true;
      _target = "pc";
   }
   

   @Override
   public void setParameter(String name, String value)
   {
      if(name.equals("skillId"))
      {
         _skillId = Integer.parseInt(value);
      }
      else if(name.equals("skillLvl"))
      {
         _skillLvl = Integer.parseInt(value);
      }
      else if(name.equals("chance"))
      {
         _chance = Integer.parseInt(value);
      }
      else if(name.equals("initialDelay"))
      {
         _initialDelay = Integer.parseInt(value);
      }
      else if(name.equals("default_enabled"))
      {
         _enabled = Boolean.parseBoolean(value);
      }
      else if(name.equals("target"))
      {
         _target = String.valueOf(value);
     }
      else if(name.equals("reuse"))
      {
         _reuse = Integer.parseInt(value);
      }
      else
      {
         super.setParameter(name, value);
      }
   }
   
   @Override
   protected void onEnter(L2Character character)
   {
    	   if (character instanceof L2PcInstance)
           {
                   L2PcInstance activeChar = ((L2PcInstance) character);
                   if(classes != null && classes.contains(""+activeChar.getClassId().getId()))
                   {
                           activeChar.teleToLocation(83597,147888,-3405);
                           activeChar.sendMessage("Your class is not allowed in the Special PvP zone.");
                           return;
                   }
				// Set pvp flag
           ((L2PcInstance) character).sendMessage("You entered a Pvp Flag zone.");
           activeChar.updatePvPFlag(1);
           ((L2PcInstance) character).broadcastUserInfo();
            if((character instanceof L2PlayableInstance && _target.equalsIgnoreCase("pc") || character instanceof L2PcInstance && _target.equalsIgnoreCase("pc_only") || character instanceof L2MonsterInstance && _target.equalsIgnoreCase("npc")) && _task == null)
	         {
	         _task = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new ApplySkill(/*this*/), _initialDelay, _reuse);
	         }
       }
   }
   
   @Override
   protected void onExit(L2Character character)
   {
       if (character instanceof L2PcInstance)
       {
           ((L2PcInstance) character).sendMessage("You left the Pvp Flag zone.");
           ((L2PcInstance) character).updatePvPFlag(0);
           ((L2PcInstance) character).broadcastUserInfo();
       }
      if(_characterList.isEmpty() && _task != null)
      {
         _task.cancel(true);
         _task = null;
      }
   }      @Override
   public void onReviveInside(L2Character character)
   {      
           if (character instanceof L2PcInstance)
       {
                   L2PcInstance activeChar = ((L2PcInstance) character);
                   if(revive_noblesse)
                           noblesse.getEffects(activeChar, activeChar);  
                   if(revive_heal)
                           heal(activeChar);
                   
                   activeChar.setCurrentHp(activeChar.getMaxHp());
                   activeChar.setCurrentCp(activeChar.getMaxCp());
                   activeChar.setCurrentMp(activeChar.getMaxMp());
       }
   }
   private static void loadConfigs()
   {
           try
           {
                   Properties prop = new Properties();
                   prop.load(new FileInputStream(new File("./config/gevorakoc/SoloZone.properties")));
                   Boolean.parseBoolean(prop.getProperty("EnablePvP", "True"));
                   {
                   }
                  give_noblesse = Boolean.parseBoolean(prop.getProperty("GiveNoblesse", "True"));
                   String[] propertySplit = prop.getProperty("Items", "").split(",");
                   if (propertySplit.length != 0)
                   {
                   {
                   }

                   Boolean.parseBoolean(prop.getProperty("RemoveBuffs", "False"));
                   revive_noblesse = Boolean.parseBoolean(prop.getProperty("ReviveNoblesse", "True"));
                   revive_heal = Boolean.parseBoolean(prop.getProperty("ReviveHeal", "True"));  
                   for (String reward : propertySplit)
                   {
                           String[] rewardSplit = reward.split(",");
                           if (rewardSplit.length == 2)
                           {
                           }
                   }
                   propertySplit = prop.getProperty("Classes", "").split(",");
                   if (propertySplit.length != 0)
                   {
                          for(String i : propertySplit)
                           {
                                   classes.add(i);
                           }
                   }
           }
   }
   catch(Exception e)
   {
           e.printStackTrace();
   }      
}
public static void updatepvps(L2PcInstance activeChar)
{

	activeChar.updatePvPFlag(1);

}
/**
 * @param activeChar
 */
private void heal(L2PcInstance activeChar)
{
	// TODO Auto-generated method stub
	
}

public L2Skill getSkill()
   {
      return SkillTable.getInstance().getInfo(_skillId, _skillLvl);
   }

   public String getTargetType()
   {
      return _target;
   }

   public boolean isEnabled()
   {
      return _enabled;
   }

   public int getChance()
   {
      return _chance;
   }

   public void setZoneEnabled(boolean val)
   {
      _enabled = val;
   }
   class ApplySkill implements Runnable
   {
      @SuppressWarnings("null")
	@Override
      public void run()
      {
         if(isEnabled())
         {
        	 L2PcInstance activeChar = null;
            for(L2Character temp : _characterList.values())
            {
               if(temp != null && !temp.isDead())
              {
                  if((temp instanceof L2PlayableInstance && getTargetType().equalsIgnoreCase("pc") || temp instanceof L2PcInstance && getTargetType().equalsIgnoreCase("pc_only") || temp instanceof L2MonsterInstance && getTargetType().equalsIgnoreCase("npc")) && Rnd.get(100) < getChance())
                  {
                     L2Skill skill = null;
                     if((skill=getSkill())==null){
                         
          	           activeChar.setCurrentHp(activeChar.getMaxHp());
          	           activeChar.setCurrentCp(activeChar.getMaxCp());
          	           activeChar.setCurrentMp(activeChar.getMaxMp());
          	           
                        System.out.println("ATTENTION: error on zone with id "+getId());
                        System.out.println("Skill "+_skillId+","+_skillLvl+" not present between skills");
                     }else
                        skill.getEffects(temp, temp);
                  }
               }
            }
         }
      }
   }
   
   @Override
   public void onDieInside(L2Character character)
   {
     

   }
}