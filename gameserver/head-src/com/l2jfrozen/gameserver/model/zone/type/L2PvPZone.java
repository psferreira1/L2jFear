/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfrozen.gameserver.model.zone.type;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javolution.util.FastList;

import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Skill;
import com.l2jfrozen.gameserver.model.L2Summon;
import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.zone.L2ZoneType;

/**
 *
 * @author Wyatt
 * @version 1.2
 *
 */

public class L2PvPZone extends L2ZoneType
{
      
       public L2PvPZone(int id)
       {
               super(id);
               loadConfigs();
       }
      
       public static boolean pvp_enabled, restart_zone, store_zone, logout_zone, revive_noblesse, revive_heal, revive, remove_buffs, remove_pets, give_noblesse;
       static int radius, enchant, revive_delay;
       static int[][] spawn_loc;
       L2Skill noblesse = SkillTable.getInstance().getInfo(1323, 1);
	private Object _task;
       private static List<String> items = new FastList<>();
       private static List<String> classes = new FastList<>();
       private static List<String> grades = new FastList<>();
       public static List<int[]> rewards = new ArrayList<>();
       static String[] gradeNames = {"","D","C","B","A","S"};
     
      
       @Override
       protected void onEnter(L2Character character)
       {
              character.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, true);
               character.setInsideZone(L2Character.ZONE_MULTIFUNCTION, true);
              
               if (character instanceof L2PcInstance)
               {
                       L2PcInstance activeChar = ((L2PcInstance) character);
                       if(classes != null && classes.contains(""+activeChar.getClassId().getId()))
                       {
                               activeChar.teleToLocation(83597,147888,-3405);
                               activeChar.sendMessage("Your class is not allowed in the Special PvP zone.");
                               return;
                       }
                      
                       for(L2ItemInstance o : activeChar.getInventory()._items)
                       {
                               if(o.isEquipable() && o.isEquipped() && !checkItem(o))
                               {
                                       int slot = activeChar.getInventory().getSlotFromItem(o);
                                       activeChar.getInventory().unEquipItemInBodySlotAndRecord(slot);
                                       activeChar.sendMessage(o.getItemName()+" unequiped because is not allowed inside this zone.");
                               }
                       }
                       
        	           ((L2PcInstance) character).sendMessage("You entered a Hero Boss Zone.");
                       activeChar.sendMessage("You entered in a Boss Zone.");
                       clear(activeChar);
                       if(give_noblesse)
                       noblesse.getEffects(activeChar, activeChar);  
                       if(pvp_enabled && activeChar.getPvpFlag() == 0)
                               activeChar.updatePvPFlag(1);                  
               }
       }
      
       @Override
	   protected void onExit(L2Character character)
	   {
	       if (character instanceof L2PcInstance)
	       {
	           ((L2PcInstance) character).setPvpFlag(0);
	           ((L2PcInstance) character).sendMessage("You left the Hero Boss Zone.");
	           ((L2PcInstance) character).broadcastUserInfo();
	       }
	      if(_characterList.isEmpty() && _task != null)
	      {

	         _task = null;
	      }
	   }
          
       

       @Override
       public void onReviveInside(L2Character character)
       {      
               if (character instanceof L2PcInstance)
           {
                       L2PcInstance activeChar = ((L2PcInstance) character);
                       if(revive_noblesse)
                               noblesse.getEffects(activeChar, activeChar);  
                       if(revive_heal)
                               heal(activeChar);
           }
       }
      
       static void heal(L2PcInstance activeChar)
       {
               activeChar.setCurrentHp(activeChar.getMaxHp());
               activeChar.setCurrentCp(activeChar.getMaxCp());
               activeChar.setCurrentMp(activeChar.getMaxMp());
       }
   	public static void updatepvps(L2PcInstance activeChar)
   	{

   		activeChar.updatePvPFlag(1);

   	}
       private void clear (L2PcInstance player)
       {
               if(remove_buffs)
               {
                       player.stopAllEffects();
                       if(remove_pets)
                       {
                               L2Summon pet = player.getPet();
                               if(pet!= null)
                               {
                                       pet.stopAllEffects();
                                       pet.unSummon(player);  
                               }      
                       }
               }
               else
               {
                       if(remove_pets)
                       {
                               L2Summon pet = player.getPet();
                               if(pet!= null)
                               {
                                       pet.unSummon(player);  
                               }      
                       }
               }
       }
      
       public static void givereward(L2PcInstance player)
       {
               if(player.isInsideZone(L2Character.ZONE_MULTIFUNCTION))
               {
           		updatepvps(player);
              }
       }
      
       public static boolean checkItem (L2ItemInstance item)
       {
               int o = item.getItem().getCrystalType();
               int e = item.getEnchantLevel();
              
               if(enchant != 0 && e >= enchant)
               {
                       return false;
               }
              
               if(grades.contains(gradeNames[o]))
                       return false;

               if(items != null && items.contains(""+item.getItemId()))
                      return false;
               return true;
       }
       
       private static void loadConfigs()
       {
               try
               {
                       Properties prop = new Properties();
                       prop.load(new FileInputStream(new File("./config/gevorakoc/BossZone.properties")));
                       pvp_enabled = Boolean.parseBoolean(prop.getProperty("EnablePvP", "False"));
                       spawn_loc = parseItemsList(prop.getProperty("SpawnLoc", "150111,144740,-12248"));
                       revive_delay = Integer.parseInt(prop.getProperty("ReviveDelay", "10"));
                       if(revive_delay != 0)
                       {
                               revive = true;
                       }
                      give_noblesse = Boolean.parseBoolean(prop.getProperty("GiveNoblesse", "False"));
                       String[] propertySplit = prop.getProperty("Items", "").split(",");
                       if (propertySplit.length != 0)
                       {
                               for(String i : propertySplit)
                               {
                                      items.add(i);
                               }
                      }
                       propertySplit = prop.getProperty("Grades", "").split(",");
                       if (propertySplit.length != 0)
                      {
                               for(String i : propertySplit)
                               {
                                       if(i.equals("D") || i.equals("C") || i.equals("B") || i.equals("A") || i.equals("S"))
                                               grades.add(i);
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
                       radius =  Integer.parseInt(prop.getProperty("RespawnRadius", "500"));
                       enchant =  Integer.parseInt(prop.getProperty("Enchant", "0"));
                       remove_buffs = Boolean.parseBoolean(prop.getProperty("RemoveBuffs", "False"));
                       remove_pets = Boolean.parseBoolean(prop.getProperty("RemovePets", "False"));
                       restart_zone = Boolean.parseBoolean(prop.getProperty("NoRestartZone", "False"));
                       store_zone = Boolean.parseBoolean(prop.getProperty("NoStoreZone", "False"));
                       logout_zone = Boolean.parseBoolean(prop.getProperty("NoLogoutZone", "False"));
                       revive_noblesse = Boolean.parseBoolean(prop.getProperty("ReviveNoblesse", "False"));
                       revive_heal = Boolean.parseBoolean(prop.getProperty("ReviveHeal", "False"));  
                       propertySplit = prop.getProperty("Rewards", "57,100000").split(";");
                       for (String reward : propertySplit)
                       {
                               String[] rewardSplit = reward.split(",");
                               if (rewardSplit.length == 2)
                               {
                                       try
                                       {
                                               rewards.add(new int[]{Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1])});
                                       }
                                       catch (NumberFormatException nfe)
                                       {
                                       }
                               }
                       }
               }
               catch(Exception e)
               {
                       e.printStackTrace();
               }      
       }
       private static int[][] parseItemsList(String line)
       {
               final String[] propertySplit = line.split(";");
               if (propertySplit.length == 0)
                       return null;

               int i = 0;
               String[] valueSplit;
               final int[][] result = new int[propertySplit.length][];
               for (String value : propertySplit)
               {
                       valueSplit = value.split(",");
                       if (valueSplit.length != 3)
                       {
                               return null;
                       }

                       result[i] = new int[3];
                       try
                       {
                               result[i][0] = Integer.parseInt(valueSplit[0]);
                       }
                       catch (NumberFormatException e)
                       {
                               return null;
                       }
                       try
                       {
                               result[i][1] = Integer.parseInt(valueSplit[1]);
                       }
                       catch (NumberFormatException e)
                       {
                               return null;
                       }   
                       try
                       {
                               result[i][2] = Integer.parseInt(valueSplit[2]);
                       }
                       catch (NumberFormatException e)
                       {
                               return null;
                       }
                       i++;
               }
               return result;
       }

	/* (non-Javadoc)
	 * @see com.l2jfrozen.gameserver.model.zone.L2ZoneType#onDieInside(com.l2jfrozen.gameserver.model.L2Character)
	 */
	@Override
	protected void onDieInside(L2Character character)
	{
		// TODO Auto-generated method stub
		
	}

	
	}
