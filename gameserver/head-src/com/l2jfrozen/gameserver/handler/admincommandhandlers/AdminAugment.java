package com.l2jfrozen.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.handler.IAdminCommandHandler;
import com.l2jfrozen.gameserver.model.Inventory;
import com.l2jfrozen.gameserver.model.L2Object;
import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.AugmentDatabase;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.util.random.Rnd;



public class AdminAugment implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_augment"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if(command.startsWith("admin_augment"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				 String type = st.nextToken();
				 if(type.startsWith("weapon"))
				 {
					    L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a weapon.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The weapon is already augmented.");
							}
						    else
							{
							    int id = Integer.parseInt(st.nextToken());
							    int level = Integer.parseInt(st.nextToken());
							    int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentweapondatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("legs"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a Legs.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The Legs is already augmented.");
							}
						    else
							{
							    int id = Integer.parseInt(st.nextToken());
							    int level = Integer.parseInt(st.nextToken());
							    int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentlegsdatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("chest"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a Chest.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The Chest is already augmented.");
							}
						    else
							{
							    int id = Integer.parseInt(st.nextToken());
							    int level = Integer.parseInt(st.nextToken());
							    int attributes = Rnd.get(12177);
						        AugmentDatabase.augmentchestdatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
						    }
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("helmet"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a Helmet.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The Helmet is already augmented.");
							}
						    else
							{
							    int id = Integer.parseInt(st.nextToken());
							    int level = Integer.parseInt(st.nextToken());
							    int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmenthelmentdatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("gloves"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a Gloves.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The Gloves is already augmented.");
							}
						    else
							{
							    int id = Integer.parseInt(st.nextToken());
							    int level = Integer.parseInt(st.nextToken());
							    int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentglovesdatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("boots"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a Boots.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The Boots is already Augmented.");
							}
						    else
							{
							    int id = Integer.parseInt(st.nextToken());
							    int level = Integer.parseInt(st.nextToken());
							     int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentbootsdatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("underwear"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_UNDER) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a Underwear.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_UNDER).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The Underwear is already augmented.");
							}
						    else
							{
							     int id = Integer.parseInt(st.nextToken());
							     int level = Integer.parseInt(st.nextToken());
							     int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentunderweardatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("rring"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a R-Ring.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The R-Ring is already augmented.");
							}
						    else
							{
							     int id = Integer.parseInt(st.nextToken());
							     int level = Integer.parseInt(st.nextToken());
							     int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentrringdatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("lring"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a L-Ring.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The L-Ring is already augmented.");
							}
						    else
							{
							     int id = Integer.parseInt(st.nextToken());
							     int level = Integer.parseInt(st.nextToken());
							     int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentlringdatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("necklace"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a Necklace.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The Necklace is already augmented.");
							}
						    else
							{
							     int id = Integer.parseInt(st.nextToken());
							     int level = Integer.parseInt(st.nextToken());
							     int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentneklacedatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("rearring"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a R-Earring.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The R-Earring is already augmented.");
							}
						    else
							{
							     int id = Integer.parseInt(st.nextToken());
							     int level = Integer.parseInt(st.nextToken());
							     int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentrearringdatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("learring"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a L-Earring.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The L-Earring is already augmented.");
							}
						    else
							{
							     int id = Integer.parseInt(st.nextToken());
							     int level = Integer.parseInt(st.nextToken());
							     int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentlearringdatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("shield"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) == null)
							{
						    	activeChar.sendMessage(activeChar.getTarget().getName()+" have to equip a Shield.");
							}
							
							if (activeChar.getTarget().getActingPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND).isAugmented())
							{
								activeChar.sendMessage(activeChar.getTarget().getName()+" The Shield is already augmented.");
							}
						    else
							{
							     int id = Integer.parseInt(st.nextToken());
							     int level = Integer.parseInt(st.nextToken());
							     int attributes = Rnd.get(12177);
						    	AugmentDatabase.augmentshielddatabase(activeChar.getTarget().getActingPlayer(),attributes,id,level);
						    	activeChar.getTarget().getActingPlayer().sendMessage("Successfully To Add "+ SkillTable.getInstance().getInfo(id,level).getName() +" By "+activeChar.getName()+".");
							}
							activeChar = (L2PcInstance) target;
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
				 else if(type.startsWith("rminfo"))
				 {   
					 AugmentDatabase.HtmAugment2(activeChar);
				 }
				 else if(type.startsWith("remove"))
				 {   
						L2Object target = activeChar.getTarget();
						if (target instanceof L2PcInstance)
						{
							L2ItemInstance item = null;
							int items = Integer.parseInt(st.nextToken());
							switch(items)
							{
							  case 1:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break;
							  case 2:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break;  
							  case 3:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break;  
							  case 4:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break;  
							  case 5:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break;  
							  case 6:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break;  
							  case 7:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break;  
							  case 8:
							  activeChar = (L2PcInstance) target;  
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break; 
							  case 9:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break; 
							  case 10:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break; 
							  case 11:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break; 
							  case 12:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break; 
							  case 13:
							  activeChar = (L2PcInstance) target;
							  item = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_UNDER);
							  AugmentDatabase.augmentremove(item, activeChar);
							  break;
							}
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
							return false;
						}
				 }
			}
			catch (Exception e)
			{
				AugmentDatabase.HtmAugment(activeChar);
			}
		}
		return false;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
