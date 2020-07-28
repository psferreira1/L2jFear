package com.l2jfrozen.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.xml.AugmentationData;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.SkillList;
import com.l2jfrozen.gameserver.network.serverpackets.StatusUpdate;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;
import com.l2jfrozen.gameserver.templates.L2Item;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
/*
 * Povis111
 * ciulpkit bybi mentai
 */

public class L2AugmenterInstance extends L2NpcInstance
{
	public L2AugmenterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2PcInstance player, int val)
	{
		if(player.getActiveWeaponInstance() != null && player.getActiveWeaponInstance().isAugmented() && player.getActiveWeaponInstance().getAugmentation().getSkill() != null)
			showHtm(player, player.getActiveWeaponInstance().getAugmentation().getSkill().getName(), false);
		else
			showHtm(player, "", false);
	}
	
	private void showHtm(L2PcInstance player, String stat, boolean naujas)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile("data/html/augmenter.htm");
		
		if(naujas)
		{
			html.replace("%text%", "<center><font color=00FF00>Gratz</font>, <font color=FFFF00>"+ player.getActiveWeaponInstance().getAugmentation().getSkill().getName()+ "</font>. </center><br><center><button value=\"Thanks\" action=\"bypass augmenter thanks\" width=\"134\" height=\"21\" back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\"></center>");
		}
		else
			html.replace("%text%", getText(player));
		
		html.replace("%stat%", "<center>"+stat+"</center>");
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.sendPacket(html);		
	}
	
	private String getText(L2PcInstance activeChar)
	{
		String text = "";
		
		if(activeChar.getActiveWeaponInstance() == null)
		{
			text = "Please equip the item that you wish to modify.";
			return text;
		}
		
		if(activeChar.getActiveWeaponInstance() != null && activeChar.getActiveWeaponInstance().isAugmented())
		{
			text = "Welcome, i see your weapon is augmented.. If you want to use my services, i can help you.";
			
			text += "<br><center><button value=\"Remove Augmentation\" action=\"bypass augmenter remove\" width=\"134\" height=\"21\" back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\"></center>";
			
			return text;
		}
		
		if(activeChar.getActiveWeaponInstance() != null && activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID) == null && !activeChar.getActiveWeaponInstance().isAugmented())
		{
			text = "Too bad.. You don't have any "+ activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID).getName() + ", please come back when you get some.";
			return text;
		}
		
		if(activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID) != null)
		{
			if(activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID).getCount() < Config.AUGMENT_ITEM_AMOUNT && !activeChar.getActiveWeaponInstance().isAugmented())
			{
				text = "Too bad.. You don't have enough "+ activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID).getName() + " for my services. Come back when you will have "+Config.AUGMENT_ITEM_AMOUNT+" or more!";		
				return text;
			}
			
			if(activeChar.getActiveWeaponInstance() != null && activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID).getCount() >= Config.AUGMENT_ITEM_AMOUNT && !activeChar.getActiveWeaponInstance().isAugmented())
			{
				text = "<table><tr><td>Welcome, i see you have some "+ activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID).getName() + " there.. If you want to use them good, i can help you.</td></tr></table>";				
				text += "<br><center><button value=\"Augment Weapon\" action=\"bypass augmenter augment\" width=\"134\" height=\"21\" back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\">";
			}	
		}
		return text;
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance activeChar, String command)
	{
		if (activeChar == null)
				return;
		
		showHtm(activeChar, "", false);
		
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		String actualCommand = st.nextToken(); // Get actual command
		
		if (actualCommand.startsWith("thanks"))
		{
			if(activeChar.getActiveWeaponInstance() != null && activeChar.getActiveWeaponInstance().isAugmented() && activeChar.getActiveWeaponInstance().getAugmentation().getSkill() != null)
				showHtm(activeChar, activeChar.getActiveWeaponInstance().getAugmentation().getSkill().getName(), false);
			else
				showHtm(activeChar, "", false);
		}	
		
		if (actualCommand.startsWith("augment"))
		{	
			if(activeChar.getActiveWeaponInstance()!=null && activeChar.getActiveWeaponInstance().isAugmented() && activeChar.getActiveWeaponInstance().getAugmentation().getSkill() != null)
				showHtm(activeChar, activeChar.getActiveWeaponInstance().getAugmentation().getSkill().getName(), false);
			else
				showHtm(activeChar, "", false);
			
			L2ItemInstance targetItem = null;
			
			if(activeChar.getActiveWeaponInstance() != null)
			{
				targetItem = activeChar.getActiveWeaponInstance();
			}
			else
			{
				activeChar.sendMessage("Don't play with me! You must have your weapon equipped!");
				return;
			}
			
			if(targetItem!=null && targetItem.isAugmented())
			{
				activeChar.sendMessage("I'm afraid your weapon is already augmented. You must remove the augmentation first!");
				return;
			}
			
			if(activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID) == null)
			{
				activeChar.sendMessage("Get some  "+ activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID).getName() + " and come back.");
				return;
			}
			
			if(activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID) != null)
				if(activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID).getCount() < Config.AUGMENT_ITEM_AMOUNT)
				{
					activeChar.sendMessage("I see You lack  "+ activeChar.getInventory().getItemByItemId(Config.AUGMENT_ITEM_ID).getName() + ". Go farm some monsters untill you reach at least "+Config.AUGMENT_ITEM_AMOUNT+" and I will augment.");
					return;
				}
			
			if (targetItem.isEquipped())
				activeChar.disarmWeapons();
			
			//L2ItemInstance refinerItem = activeChar.getInventory().getItemByItemId(8762);
			
			if(activeChar != null && targetItem != null/* && refinerItem != null*/)
			{
				if (TryAugmentItem(activeChar, targetItem))
				{
					activeChar.destroyItemByItemId("destroy augment items", Config.AUGMENT_ITEM_ID, Config.AUGMENT_ITEM_AMOUNT, activeChar, true);
					//activeChar.reduceAdena("for augmentation", Config.AUGMENT_PRICE, activeChar, true);
					activeChar.sendPacket(SystemMessageId.THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED);
				}
			}
			else
			{
				showHtm(activeChar, "", false);
				return;
			}
			
			if(targetItem != null && !targetItem.isEquipped())				
			{
				activeChar.getInventory().equipItemAndRecord(targetItem);				
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_EQUIPPED);
				sm.addItemName(targetItem.getItemId());
				activeChar.sendPacket(sm);
				sm = null;
				activeChar.broadcastUserInfo();
			}
			
			if(activeChar.getActiveWeaponInstance() != null)
				if(activeChar.getActiveWeaponInstance().getAugmentation() != null && activeChar.getActiveWeaponInstance().getAugmentation().getSkill() !=null )
					showHtm(activeChar, activeChar.getActiveWeaponInstance().isAugmented() ? activeChar.getActiveWeaponInstance().getAugmentation().getSkill().getName() : "", true);
				else
					showHtm(activeChar, "", false);
		}
		
		if (actualCommand.startsWith("remove"))
		{	
			
			L2ItemInstance targetItem = null;
			
			if(activeChar.getActiveWeaponInstance() != null)
			{
				targetItem = activeChar.getActiveWeaponInstance();
			}
			else
			{
				activeChar.sendMessage("Don't play with me! You must have your weapon equipped!");
				return;
			}
			
			if(targetItem.isAugmented())
			{			
				if (targetItem.isEquipped())
					activeChar.disarmWeapons();

				targetItem.removeAugmentation();
				InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(targetItem);
				activeChar.sendPacket(iu);

				// send system message
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1);
				sm.addString(targetItem.getItemName());
				activeChar.sendPacket(sm);
				sm = null;
				if(targetItem != null)
				{
					activeChar.getInventory().equipItemAndRecord(targetItem);				
					sm = new SystemMessage(SystemMessageId.S1_EQUIPPED);
					sm.addItemName(targetItem.getItemId());
					activeChar.sendPacket(sm);
					sm = null;
					activeChar.broadcastUserInfo();
				}		
			}
			
			showHtm(activeChar, targetItem.isAugmented() ? targetItem.getAugmentation().getSkill().getName() : "", false);
		}		
		
		super.onBypassFeedback(activeChar, command);
	}
	
	boolean TryAugmentItem(L2PcInstance player, L2ItemInstance targetItem)
	{
		if (targetItem.isAugmented() || targetItem.isWear())
			return false;

		if (player.isDead())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD);
			return false;
		}
		if (player.isCastingNow())
			return false;		
		if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN);
			return false;
		}
		if (player.isFishing())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING);
			return false;
		}
		if (player.isParalyzed())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED);
			return false;
		}
		if (player.getActiveTradeList() != null)
		{
			player.sendPacket(SystemMessageId.AUGMENTED_ITEM_CANNOT_BE_DISCARDED);
			return false;
		}
		if (player.getPrivateStoreType() != L2PcInstance.STORE_PRIVATE_NONE)
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION);
			return false;
		}

		// check for the items to be in the inventory of the owner
		//if (player.getInventory().getItemByObjectId(refinerItem.getObjectId()) == null)
		{
			//Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to refine an item with wrong LifeStone-id.", Config.DEFAULT_PUNISH);
		//	return false;
		}
		if (player.getInventory().getItemByObjectId(targetItem.getObjectId()) == null)
		{
			//Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to refine an item with wrong Weapon-id.", Config.DEFAULT_PUNISH);
			return false;
		}

		int itemGrade = targetItem.getItem().getItemGrade();
		int itemType = targetItem.getItem().getType2();
	//	int lifeStoneId = refinerItem.getItemId();

		// is the refiner Item a life stone?
		//if (lifeStoneId < 8723 || lifeStoneId > 8762)
		//	return false;

		// must be a weapon, must be > d grade
		// TODO: can do better? : currently: using isdestroyable() as a check for hero / cursed weapons
		if (itemGrade < L2Item.CRYSTAL_C || itemType != L2Item.TYPE2_WEAPON || !targetItem.isDestroyable())
			return false;

		// player must be able to use augmentation
		if (player.getPrivateStoreType() != L2PcInstance.STORE_PRIVATE_NONE || player.isDead() || player.isParalyzed() || player.isFishing() || player.isSitting())
			return false;

		int lifeStoneLevel = getLifeStoneLevel(8762);
		int lifeStoneGrade = getLifeStoneGrade(8762);

		// check if the lifestone is appropriate for this player
		switch (lifeStoneLevel)
		{
			case 1:
				if (player.getLevel() < 46)
					return false;
			break;
			case 2:
				if (player.getLevel() < 49)
					return false;
			break;
			case 3:
				if (player.getLevel() < 52)
					return false;
			break;
			case 4:
				if (player.getLevel() < 55)
					return false;
			break;
			case 5:
				if (player.getLevel() < 58)
					return false;
			break;
			case 6:
				if (player.getLevel() < 61)
					return false;
			break;
			case 7:
				if (player.getLevel() < 64)
					return false;
			break;
			case 8:
				if (player.getLevel() < 67)
					return false;
			break;
			case 9:
				if (player.getLevel() < 70)
					return false;
			break;
			case 10:
				if (player.getLevel() < 76)
					return false;
			break;
		}

		//stackable lifestone
		//if(!player.destroyItem("RequestRefine",refinerItem.getObjectId(),1,null,false))
        //    return false;
		
		// generate augmentation
		targetItem.setAugmentation(AugmentationData.getInstance().generateRandomAugmentation(targetItem, lifeStoneLevel, lifeStoneGrade));

		// finish and send the inventory update packet
		InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(targetItem);
		player.sendPacket(iu);

		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
		player.sendPacket(new SkillList());

		return true;
	}
	
	private int getLifeStoneGrade(int itemId)
	{
		itemId -= 8723;
		if (itemId < 10)
			return 0; // normal grade
		if (itemId < 20)
			return 1; // mid grade
		if (itemId < 30)
			return 2; // high grade
		return 3; // top grade
	}

	private int getLifeStoneLevel(int itemId)
	{
		itemId -= 10 * getLifeStoneGrade(itemId);
		itemId -= 8722;
		return itemId;
	}
}
