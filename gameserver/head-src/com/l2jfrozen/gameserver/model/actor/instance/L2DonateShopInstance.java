package com.l2jfrozen.gameserver.model.actor.instance;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringTokenizer;
 
import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.cache.HtmCache;
import com.l2jfrozen.gameserver.custom.DonateAudit;
import com.l2jfrozen.gameserver.datatables.CharNameTable;
import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.model.Inventory;
import com.l2jfrozen.gameserver.model.L2Augmentation;
import com.l2jfrozen.gameserver.model.L2Skill;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.multisell.L2Multisell;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.EtcStatusUpdate;
import com.l2jfrozen.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jfrozen.gameserver.network.serverpackets.ItemList;
import com.l2jfrozen.gameserver.network.serverpackets.LeaveWorld;
import com.l2jfrozen.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.PartySmallWindowAll;
import com.l2jfrozen.gameserver.network.serverpackets.PartySmallWindowDeleteAll;
import com.l2jfrozen.gameserver.network.serverpackets.SocialAction;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;
import com.l2jfrozen.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfrozen.gameserver.templates.L2EtcItemType;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.util.Util;
import com.l2jfrozen.util.database.L2DatabaseFactory;
 
/**
 * @author gevorakoC
 */
@SuppressWarnings("unused")
public class L2DonateShopInstance extends L2FolkInstance
{
    public L2DonateShopInstance(int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
    }
   
    // Config Donate Shop
    private static int itemid = 5556;
    private static int[] clanSkills =
    {
        370,
        371,
        372,
        373,
        374,
        375,
        376,
        377,
        378,
        379,
        380,
        381,
        382,
        383,
        384,
        385,
        386,
        387,
        388,
        389,
        390,
        391
    }; 
    @Override
    public void onBypassFeedback(L2PcInstance player, String command)
    {
        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken(); // Get actual command
       
        switch (command)
        {
            case "clan":
                clanReward(player, 20);
                break;
            case "windows":
                winds(player, 7);
                break;
            case "augments":
                winds(player, 8);
                break;
            case "augmentpanel":
                winds(player, 13);
                break;
            case "passive":
                winds(player, 14);
                break;
            case "passive2":
                winds(player, 15);
                break;
            case "page2":
                winds(player, 9);
                break;
            case "page3":
                winds(player, 10);
                break;
            case "page4":
                winds(player, 11);
                break;
            case "page5":
                winds(player, 12);
                break;
            case "chars":
                winds(player, 5);
                break;
            case "noblesse":
                noblesse(player, 5);
                break;
            case "donate":
                showEnchantSkillList(player, player.getClassId());
                break;
            case "donatewin":
                winds(player, 2);
                break;
            case "sexwin":
                winds(player, 4);
                break;
            case "noblessewin":
                winds(player, 1);
                break;
            case "clanwin":
                winds(player, 3);
                break;
            case "herowin":
                winds(player, 6);
                break;
            case "sex":
                sex(player, 10);
                break;
            case "sethero":
                hero(player, 50, 0);
                break;
            case "sethero1":
                hero(player, 5, 1);
                break;
            case "sethero7":
                hero(player, 25, 30);
                break;
            case "weapon":
                enchantw(player);
                break;
            case "armor":
                enchanta(player);
                break;
            case "jewel":
                enchantj(player);
                break;
            case "rhand":
                Enchant(player, 16, 8, Inventory.PAPERDOLL_RHAND);
                break;
            case "lhand":
                Enchant(player, 16, 8, Inventory.PAPERDOLL_LHAND);
                break;
            case "rear":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_REAR);
                break;
            case "lear":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_LEAR);
                break;
            case "rf":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_RFINGER);
                break;
            case "lf":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_LFINGER);
                break;
            case "neck":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_NECK);
                break;
            case "head":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_HEAD);
                break;
            case "feet":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_FEET);
                break;
            case "gloves":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_GLOVES);
                break;
            case "chest":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_CHEST);
                break;
            case "legs":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_LEGS);
                break;
            case "tattoo":
                Enchant(player, 16, 3, Inventory.PAPERDOLL_UNDER);
                break;
        }
        if (command.startsWith("addaugment"))
        {
            StringTokenizer sts = new StringTokenizer(command);
            sts.nextToken();
            try
            {
                String type = sts.nextToken();
                switch (type)
                {
                    case "DuelMight":
                        augments(player, 10, 1062406807, 3134, 10);
                        break;
                    case "Might":
                        augments(player, 10, 1062079106, 3132, 10);
                        break;
                    case "Shield":
                        augments(player, 10, 968884225, 3135, 10);
                        break;
                    case "MagicBarrier":
                        augments(player, 10, 956760065, 3136, 10);
                        break;
                    case "Empower":
                        augments(player, 10, 1061423766, 3133, 10);
                        break;
                    case "BattleRoar":
                        augments(player, 10, 968228865, 3125, 10);
                        break;
                    case "Agility":
                        augments(player, 10, 1060444351, 3139, 10);
                        break;
                    case "Heal":
                        augments(player, 10, 1061361888, 3123, 10);
                        break;
                    case "CelestialShield":
                        augments(player, 10, 974454785, 3158, 1);
                        break;
                    case "Guidance":
                        augments(player, 10, 1061034178, 3140, 10);
                        break;
                    case "Focus":
                        augments(player, 10, 1067523168, 3141, 10);
                        break;
                    case "WildMagic":
                        augments(player, 10, 1067850844, 3142, 10);
                        break;
                    case "ReflectDamage":
                        augments(player, 10, 1067588698, 3204, 3);
                        break;
                    case "Stone":
                        augments(player, 10, 1060640984, 3169, 10);
                        break;
                    case "HealEmpower":
                        augments(player, 10, 1061230760, 3138, 10);
                        break;
                    case "ShadowFlare":
                        augments(player, 10, 1063520931, 3171, 10);
                        break;
                    case "AuraFlare":
                        augments(player, 10, 1063455338, 3172, 10);
                        break;
                    case "Prominence":
                        augments(player, 10, 1063327898, 3165, 10);
                        break;
                    case "HydroBlast":
                        augments(player, 10, 1063590051, 3167, 10);
                        break;
                    case "SolarFlare":
                        augments(player, 10, 1061158912, 3177, 10);
                        break;
                    case "ManaBurn":
                        augments(player, 10, 956825600, 3154, 10);
                        break;
                    case "Refresh":
                        augments(player, 10, 997392384, 3202, 3);
                        break;
                    case "Hurricane":
                        augments(player, 10, 1064108032, 3168, 10);
                        break;
                    case "SpellRefresh":
                        augments(player, 10, 1068302336, 3200, 3);
                        break;
                    case "SkillRefresh":
                        augments(player, 10, 1068040192, 3199, 3);
                        break;
                    case "Stun":
                        augments(player, 10, 969867264, 3189, 10);
                        break;
                    case "Prayer":
                        augments(player, 10, 991297536, 3126, 10);
                        break;
                    case "Cheer":
                        augments(player, 10, 979828736, 3131, 10);
                        break;
                    case "BlessedSoul":
                        augments(player, 10, 991690752, 3128, 10);
                        break;
                    case "BlessedBody":
                        augments(player, 10, 991625216, 3124, 10);
                        break;
                    case "DuelMightp":
                        augments(player, 10, 1067260101, 3243, 10);
                        break;
                    case "Mightp":
                        augments(player, 10, 1067125363, 3240, 10);
                        break;
                    case "Shieldp":
                        augments(player, 10, 1067194549, 3244, 10);
                        break;
                    case "MagicBarrierp":
                        augments(player, 10, 962068481, 3245, 10);
                        break;
                    case "Empowerp":
                        augments(player, 10, 1066994296, 3241, 10);
                        break;
                    case "Agilityp":
                        augments(player, 10, 965279745, 3247, 10);
                        break;
                    case "Guidancep":
                        augments(player, 10, 1070537767, 3248, 10);
                        break;
                    case "Focusp":
                        augments(player, 10, 1070406728, 3249, 10);
                        break;
                    case "WildMagicp":
                        augments(player, 10, 1070599653, 3250, 10);
                        break;
                    case "ReflectDamagep":
                        augments(player, 10, 1070472227, 3259, 3);
                        break;
                    case "HealEmpowerp":
                        augments(player, 10, 1066866909, 3246, 10);
                        break;
                    case "Prayerp":
                        augments(player, 10, 1066932422, 3238, 10);
                        break;
                }
            }
            catch (Exception e)
            {
                player.sendMessage("Usage : Bar>");
            }
        }
        else if (command.startsWith("name"))
        {
            try
            {
                String commands[] = command.split(" ");
                name(player, 10, commands);
            }
            catch (StringIndexOutOfBoundsException e)
            {
                // Case of empty character name
                player.sendMessage("Usage: enter box your name");
            }
        }
        if (actualCommand.equalsIgnoreCase("Multisell"))
        {
            if (st.countTokens() < 1)
                return;
            L2Multisell.getInstance().SeparateAndSend(Integer.parseInt(st.nextToken()), player, false, getCastle().getTaxRate());
        }
    }
   
    @Override
    public void onAction(L2PcInstance player)
    {
        player.setLastFolkNPC(this);
        if (this != player.getTarget())
        {
            player.setTarget(this);
           
            player.sendPacket(new MyTargetSelected(getObjectId(), 0));
           
            player.sendPacket(new ValidateLocation(this));
        }
        else if (!canInteract(player))
        {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, this);
        }
        else
        {
            showClanWindow(player);
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
   
    public void showClanWindow(L2PcInstance activeChar)
    {
        NpcHtmlMessage nhm = new NpcHtmlMessage(5);
        StringBuilder tb = new StringBuilder("");
       
        tb.append("<html><head><title>L2WildHunters Donate Shop</title></head><body>");
        tb.append("<center>");
        tb.append("<table width=300 height=20 bgcolor=000000 border=0 cellspacing=0 cellpadding=0>");
        tb.append("<tr>");
        tb.append("<td align=center><font color=\"FF6600\">Hello," + activeChar.getName() + " Here You can Buy with Donate Coin.</font></td>");
        tb.append("</tr></table>");
        tb.append("<img src=\"L2UI.SquareGray\" width=\"300\" height=\"1\"><br>");
        tb.append("<table width=300 align=center>");
        tb.append("<tr>");
        tb.append("<td align=center><img src=\"icon.skill0371\" width=32 height=32></td>");
        tb.append("<td align=center><button value=\"Full Clan\" action=\"bypass -h npc_" + getObjectId() + "_clanwin\" width=\"95\" height=\"24\" back=\"L2UI_CH3.bigbutton_down\" fore=\"L2UI_CH3.bigbutton\"></td>");
        tb.append("<td align=center><button value=\"Augment Skills\" action=\"bypass -h npc_" + getObjectId() + "_augmentpanel\" width=\"95\" height=\"24\" back=\"L2UI_CH3.bigbutton_down\" fore=\"L2UI_CH3.bigbutton\"></td>");
        tb.append("<td align=center><img src=\"icon.skill3123\" width=32 height=32></td>");
        tb.append("</tr>");
        tb.append("<tr></tr>");
        tb.append("<tr>");
        tb.append("<td align=center><img src=\"icon.weapon_draconic_bow_i01\" width=32 height=32></td>");
        tb.append("<td align=center><button value=\"Enchant Item\" action=\"bypass -h npc_" + getObjectId() + "_windows\" width=\"95\" height=\"24\" back=\"L2UI_CH3.bigbutton_down\" fore=\"L2UI_CH3.bigbutton\"></td>");
        tb.append("<td align=center><button value=\"Change Sex\" action=\"bypass -h npc_" + getObjectId() + "_sexwin\" width=\"95\" height=\"24\" back=\"L2UI_CH3.bigbutton_down\" fore=\"L2UI_CH3.bigbutton\"></td>");
        tb.append("<td align=center><img src=\"icon.skill1297\" width=32 height=32></td>");
        tb.append("</tr>");
        tb.append("<tr></tr>");
        tb.append("<tr>");
        tb.append("<td align=center><img src=\"icon.etc_permit_card_i00\" width=32 height=32></td>");
        tb.append("<td align=center><button value=\"Change Name\" action=\"bypass -h npc_" + getObjectId() + "_chars\" width=\"95\" height=\"24\" back=\"L2UI_CH3.bigbutton_down\" fore=\"L2UI_CH3.bigbutton\"></td>");
        tb.append("<td align=center><button value=\"Noblesse Status\" action=\"bypass -h npc_" + getObjectId() + "_noblessewin\" width=\"95\" height=\"24\" back=\"L2UI_CH3.bigbutton_down\" fore=\"L2UI_CH3.bigbutton\"></td>");
        tb.append("<td align=center><img src=\"icon.skill1323\" width=32 height=32></td>");
        tb.append("</tr>");
        tb.append("<tr></tr>");
        tb.append("<tr>");
        tb.append("<td align=center><img src=\"icon.skill1405\" width=32 height=32></td>");
        tb.append("<td align=center><button value=\"Skill Enchanter\" action=\"bypass -h npc_" + getObjectId() + "_donatewin\" width=\"95\" height=\"24\" back=\"L2UI_CH3.bigbutton_down\" fore=\"L2UI_CH3.bigbutton\"></td>");
        tb.append("<td align=center><button value=\"Hero Status\" action=\"bypass -h npc_" + getObjectId() + "_herowin\" width=\"95\" height=\"24\" back=\"L2UI_CH3.bigbutton_down\" fore=\"L2UI_CH3.bigbutton\"></td>");
        tb.append("<td align=center><img src=\"icon.skill1374\" width=32 height=32></td>");
        tb.append("</tr>");
        tb.append("</table>");
        tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
        tb.append("<button value=\"Donate Shop\" action=\"bypass -h npc_" + getObjectId() + "_multisell 94203\" width=\"95\" height=\"24\" back=\"L2UI_CH3.bigbutton_down\" fore=\"L2UI_CH3.bigbutton\"><br>");
        tb.append("</center>");
        tb.append("<table width=300>");
        tb.append("<tr>");
        tb.append("<td><center><font color=\"0088ff\">WebSite:</font>  <font color=\"a9a9a2\">www.l2WildHunters.com</font></center></td>");
        tb.append("</tr>");
        tb.append("</table>");
        tb.append("</body></html>");
       
        nhm.setHtml(tb.toString());
        activeChar.sendPacket(nhm);
    }
   
    public static void augments(L2PcInstance activeChar, int ammount, int attributes, int idaugment, int levelaugment)
    {
        L2ItemInstance rhand = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
        if (activeChar.getInventory().getInventoryItemCount(itemid, 0) >= ammount)
        {
            if (rhand == null)
            {
                activeChar.sendMessage(activeChar.getName() + " have to equip a weapon.");
                return;
            }
            else if (rhand.getItem().getCrystalType() == 0 || rhand.getItem().getCrystalType() == 1 || rhand.getItem().getCrystalType() == 2)
            {
                activeChar.sendMessage("You can't augment under " + rhand.getItem().getCrystalType() + " Grade Weapon!");
                return;
            }
            else if (rhand.isHeroItem())
            {
                activeChar.sendMessage("You Cannot be add Augment On " + rhand.getItemName() + " !");
                return;
            }
           
            if (!rhand.isAugmented())
            {
                activeChar.sendMessage("Successfully To Add " + SkillTable.getInstance().getInfo(idaugment, levelaugment).getName() + ".");
                augmentweapondatabase(activeChar, attributes, idaugment, levelaugment);
               
                DonateAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "Donated " + SkillTable.getInstance().getInfo(idaugment, levelaugment).getName() + " Stuck " + rhand.getItemName() + ".", "Donate Coins:" + ammount);
            }
            else
            {
                activeChar.sendMessage("You Have Augment on weapon!");
                return;
            }
           
            if (!activeChar.destroyItemByItemId("Donate Coin", itemid, ammount, activeChar, false))
                return;
           
        }
        else
        {
            activeChar.sendMessage("You do not have enough Donate Coin.");
        }
    }
   
    public static void augmentweapondatabase(L2PcInstance player, int attributes, int id, int level)
    {
        L2ItemInstance item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
        L2Augmentation augmentation = new L2Augmentation(item, attributes, id, level, true);
        augmentation.applyBoni(player);
        item.setAugmentation(augmentation);
       
        try (
            Connection con = L2DatabaseFactory.getInstance().getConnection())
        {
            PreparedStatement statement = con.prepareStatement("REPLACE INTO augmentations VALUES(?,?,?,?)");
            statement.setInt(1, item.getObjectId());
            statement.setInt(2, attributes);
            statement.setInt(3, id);
            statement.setInt(4, level);
            InventoryUpdate iu = new InventoryUpdate();
            player.sendPacket(iu);
            statement.execute();
            statement.close();
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
    }
   
    public static void clanReward(L2PcInstance activeChar, int ammount)
    {
        if (activeChar.getInventory().getInventoryItemCount(itemid, 0) >= ammount)
        {
            if (activeChar.isClanLeader() && activeChar.getClan().getLevel() == 8)
            {
                activeChar.sendMessage("You are the leader and you have clan lvl 8.");
                return;
            }
           
            if (!activeChar.isClanLeader())
            {
                activeChar.sendMessage("You are not the leader of your Clan.");
                return;
            }
           
            if (activeChar.isClanLeader() && activeChar.getClan().getLevel() < 8)
            {
                activeChar.getClan().changeLevel(8);
                activeChar.getClan().setReputationScore(10000, true);
                activeChar.getSkills();
                activeChar.sendPacket(new EtcStatusUpdate(activeChar));
                activeChar.getClan().broadcastClanStatus();
                activeChar.sendMessage("Your Buy clan level 8 and full clan skills was successful.");
                DonateAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "Donated Clan level 8 and full clan skills", "Donate Coins:" + ammount);
            }
            if (!activeChar.destroyItemByItemId("Donate Coin", itemid, ammount, activeChar, false))
                return;
        }
        else
        {
            activeChar.sendMessage("You do not have enough Donate Coin.");
        }
    }
   
    public static void noblesse(L2PcInstance activeChar, int ammount)
    {
        if (activeChar.getInventory().getInventoryItemCount(itemid, 0) >= ammount)
        {
            if (activeChar.isNoble())
            {
                activeChar.sendMessage("You Are Already A Noblesse!.");
                return;
            }
           
            if (!activeChar.isNoble())
            {
                activeChar.setNoble(true);
                activeChar.broadcastUserInfo();
                activeChar.getInventory().addItem("Tiara", 7694, 1, activeChar, null);
                activeChar.sendMessage("You Are Now a Noble,You Are Granted With Noblesse Status , And Noblesse Skills.");
                DonateAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "Donated Noblesse Status", "Donate Coins:" + ammount);
            }
            if (!activeChar.destroyItemByItemId("Donate Coin", itemid, ammount, activeChar, false))
                return;
        }
        else
        {
            activeChar.sendMessage("You do not have enough Donate Coin.");
        }
    }
 
    @SuppressWarnings({
    })
    public static void hero(final L2PcInstance activeChar, int ammount,int heroTime )
     
      {
            if (activeChar.getInventory().getInventoryItemCount(itemid, 0) >= ammount)
            {      
              if(activeChar.isHero())
              {
                activeChar.sendMessage("You Are Already A Hero!");
                return;
              }
            activeChar.setHero(true);
            activeChar.sendMessage("You Are Now a Donate Hero,You Are Granted With Hero Status , Skills ,Aura.");
            activeChar.broadcastUserInfo();
           
            String days = null;
           
            String INSERT_DATA = "REPLACE INTO characters_custom_data (obj_Id, char_name, hero, noble, donator, hero_end_date) VALUES (?,?,?,?,?,?)";
           
           
         
            Connection con = null;
            try
            {          
                    if (activeChar == null)
                        return;
                   
                con = L2DatabaseFactory.getInstance().getConnection(false);
                PreparedStatement stmt = con.prepareStatement(INSERT_DATA);
               
                stmt.setInt(1, activeChar.getObjectId());
                stmt.setString(2, activeChar.getName());
                stmt.setInt(3, 1);
                stmt.setInt(4, activeChar.isNoble() ? 1 : 0);
                stmt.setInt(5, activeChar.isDonator() ? 1 : 0);
                stmt.setLong(6, heroTime == 0 ? 0 : System.currentTimeMillis() + heroTime);
                stmt.execute();
                stmt.close();
                stmt = null;
            }
            catch (final Exception e)
            {
                if (Config.ENABLE_ALL_EXCEPTIONS)
                    e.printStackTrace();
               
                LOGGER.error("Error: could not update database: ", e);
            }
           
            switch(heroTime)
            {
              case 0:
               days = " 4ever";
               break;
              case 1:
              days = " Days";
              break;
              case 30:
              days = " Days";
              break;
            }
            DonateAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "Donated Hero for " + heroTime + days + " Status", "Donate Coins:" + ammount);
           
            if (!activeChar.destroyItemByItemId("Donate Coin", itemid, ammount, activeChar, false))
                return;
        }
        else
        {
            activeChar.sendMessage("You do not have enough Donate Coin.");
        }
    }
   
    /*
     * public static void donatestatus(L2PcInstance activeChar,int ammount) { if (activeChar.getInventory().getInventoryItemCount(itemid, 0) >= ammount) { if(activeChar.isdonator()) { activeChar.sendMessage("You Are Already A Donate Status!."); return; } if(!activeChar.isdonator()) {
     * activeChar.setdonator(true); activeChar.updateNameTitleColor(); try (Connection connection = L2DatabaseFactory.getInstance().getConnection()) { PreparedStatement statement = connection.prepareStatement("SELECT obj_id FROM characters where char_name=?");
     * statement.setString(1,activeChar.getName()); ResultSet rset = statement.executeQuery(); int objId = 0; if (rset.next()) { objId = rset.getInt(1); } rset.close(); statement.close(); if (objId == 0) { connection.close(); return; } statement = connection.prepareStatement(
     * "UPDATE characters SET donator=1 WHERE obj_id=?"); statement.setInt(1, objId); statement.execute(); statement.close(); connection.close(); } catch (Exception e) { System.out.println("could not set donator stats of char:"+ e); } activeChar.sendMessage("You Are Now a Have Donate Status.");
     * activeChar.broadcastUserInfo(); DonateAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]","Donated Donate Status","Donate Coins:"+ammount); } if (!activeChar.destroyItemByItemId("Donate Coin",itemid, ammount, activeChar, false)) return; } else {
     * activeChar.sendMessage("You do not have enough Donate Coin."); } }
     */
   
    public void enchantj(L2PcInstance activeChar)
    {
        // jewels
        L2ItemInstance rear = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR);
        L2ItemInstance lear = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR);
        L2ItemInstance rfinger = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER);
        L2ItemInstance lfinger = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER);
        L2ItemInstance neck = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK);
       
        NpcHtmlMessage nhm = new NpcHtmlMessage(5);
        StringBuilder tb = new StringBuilder("");
       
        tb.append("<html><head><title>Wildhunters Donate Shop</title></head><body>");
        tb.append("<center><font color=\"FF0000\">Donate Shop</font><br>");
        tb.append("<center>Enchant Jewel's Part +16 Will Cost <font color=\"FF6600\">3 Donate Coin</font>.<br></center>");
        tb.append("<center>");
        tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
        tb.append("<br>");
        if (rear != null)
        {
            tb.append("<button value=\"" + rear.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_rear\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        if (lear != null)
        {
            tb.append("<button value=\"" + lear.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_lear\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        if (rfinger != null)
        {
            tb.append("<button value=\"" + rfinger.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_rf\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        if (lfinger != null)
        {
            tb.append("<button value=\"" + lfinger.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_lf\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        if (neck != null)
        {
            tb.append("<button value=\"" + neck.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_neck\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
        tb.append("</center>");
        tb.append("</body></html>");
       
        nhm.setHtml(tb.toString());
        activeChar.sendPacket(nhm);
    }
   
    public void enchanta(L2PcInstance activeChar)
    {
        // armors
        L2ItemInstance head = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
        L2ItemInstance chest = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
        L2ItemInstance legs = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
        L2ItemInstance feet = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);
        L2ItemInstance gloves = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
        L2ItemInstance tattoo = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_UNDER);
        NpcHtmlMessage nhm = new NpcHtmlMessage(5);
        StringBuilder tb = new StringBuilder("");
       
        tb.append("<html><head><title>WildHunters Donate Shop</title></head><body>");
        tb.append("<center><font color=\"FF0000\">Donate Shop</font><br>");
        tb.append("<center>Enchant Armor Part +16 Will Cost <font color=\"FF6600\">3 Donate Coin</font>.<br></center>");
        tb.append("<center>");
        tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
        tb.append("<br>");
        if (head != null)
        {
            tb.append("<button value=\"" + head.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_head\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        if (chest != null)
        {
            tb.append("<button value=\"" + chest.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_chest\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        if (legs != null)
        {
            tb.append("<button value=\"" + legs.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_legs\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        if (feet != null)
        {
            tb.append("<button value=\"" + feet.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_feet\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        if (gloves != null)
        {
            tb.append("<button value=\"" + gloves.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_gloves\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        if (tattoo != null)
        {
            tb.append("<button value=\"" + tattoo.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_tattoo\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
        tb.append("</center>");
        tb.append("</body></html>");
       
        nhm.setHtml(tb.toString());
        activeChar.sendPacket(nhm);
    }
   
    public void enchantw(L2PcInstance activeChar)
    {
        L2ItemInstance rhand = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
        L2ItemInstance lhand = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
       
        NpcHtmlMessage nhm = new NpcHtmlMessage(5);
        StringBuilder tb = new StringBuilder("");
       
        tb.append("<html><head><title>L2WildHunters Donate Shop</title></head><body>");
        tb.append("<center><font color=\"FF0000\">Donate Shop</font><br>");
        tb.append("<center>Enchant Weapon +16 Will Cost <font color=\"FF6600\">8 Donate Coin</font>.<br></center>");
        tb.append("<center>");
        tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
        tb.append("<br>");
        if (rhand != null)
        {
            tb.append("<button value=\"" + rhand.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_rhand\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
        if (lhand != null && lhand.getItem().getItemType() != L2EtcItemType.ARROW)
        {
            tb.append("<button value=\"" + lhand.getItemName() + "\" action=\"bypass -h npc_" + getObjectId() + "_lhand\" width=204 height=20 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">");
        }
       
        tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
        tb.append("</center>");
        tb.append("</body></html>");
       
        nhm.setHtml(tb.toString());
        activeChar.sendPacket(nhm);
    }
   
    public static void sex(L2PcInstance activeChar, int ammount)
    {
        if (activeChar.getInventory().getInventoryItemCount(itemid, 0) >= ammount)
        {
            if (activeChar.getClassId().getId() == activeChar.getBaseClass())
            {
                activeChar.getAppearance().setSex(activeChar.getAppearance().getSex() ? false : true);
                activeChar.broadcastUserInfo();
                L2PcInstance.setSexDB(activeChar, 1);
                activeChar.decayMe();
                activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());
                activeChar.sendMessage("Congratulations! Your Sex has been changed succesfully. You will now be disconnected for Update Sex. Please login again!");
                DonateAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "Donated Change Sex", "Donate Coins:" + ammount);
                try
                {
                    Thread.sleep(3000L);
                }
                catch (Exception e)
                {
                }
                activeChar.deleteMe();
                activeChar.sendPacket(LeaveWorld.STATIC_PACKET);
               
                if (!activeChar.destroyItemByItemId("Donate Coin", itemid, ammount, activeChar, false))
                    return;
            }
            else
            {
                activeChar.sendMessage("In Order To Get Sex You Need To Be On Main Class");
                return;
            }
        }
        else
        {
            activeChar.sendMessage("You do not have enough Donate Coin.");
        }
    }
   
    private void winds(L2PcInstance player, int count)
    {
        L2ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
        NpcHtmlMessage html = new NpcHtmlMessage(1);
        switch (count)
        {
            case 1:
                String htmContent = HtmCache.getInstance().getHtm("data/html/mods/donate/noblesse.htm");
                html.setHtml(htmContent);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 2:
                String htmContent1 = HtmCache.getInstance().getHtm("data/html/mods/donate/donate.htm");
                html.setHtml(htmContent1);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 3:
                String htmContent2 = HtmCache.getInstance().getHtm("data/html/mods/donate/clan.htm");
                html.setHtml(htmContent2);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 4:
                String htmContent3 = HtmCache.getInstance().getHtm("data/html/mods/donate/sex.htm");
                html.setHtml(htmContent3);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 5:
                String htmContent4 = HtmCache.getInstance().getHtm("data/html/mods/donate/name.htm");
                html.setHtml(htmContent4);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 6:
                String htmContent5 = HtmCache.getInstance().getHtm("data/html/mods/donate/hero.htm");
                html.setHtml(htmContent5);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 7:
                String htmContent6 = HtmCache.getInstance().getHtm("data/html/mods/donate/enchant.htm");
                html.setHtml(htmContent6);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 8:
                String htmContent8 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/active/page1.htm");
                html.setHtml(htmContent8);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1)
                {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 9:
                String htmContent9 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/active/page2.htm");
                html.setHtml(htmContent9);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1)
                {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 10:
                String htmContent10 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/active/page3.htm");
                html.setHtml(htmContent10);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1)
                {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 11:
                String htmContent11 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/active/page4.htm");
                html.setHtml(htmContent11);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1)
                {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 12:
                String htmContent12 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/active/page5.htm");
                html.setHtml(htmContent12);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1)
                {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 13:
                String htmContent13 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment.htm");
                html.setHtml(htmContent13);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 14:
                String htmContent14 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/passive/page1.htm");
                html.setHtml(htmContent14);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1)
                {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 15:
                String htmContent15 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/passive/page2.htm");
                html.setHtml(htmContent15);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1)
                {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
        }
    }
   
    private static void name(L2PcInstance activeChar, int ammount, String val[])
    {
        if (activeChar.getInventory().getInventoryItemCount(itemid, 0) >= ammount)
        {
            if (val.length != 2)
            {
                activeChar.sendMessage("Enter a new name or remove the space between the names.");
                return;
            }
            else if (val[1].length() < 1 || val[1].length() > 16)
            {
                activeChar.sendMessage("Maximum number of characters: 16");
                return;
            }
            else if (!Util.isAlphaNumeric(val[1]))
            {
                activeChar.sendMessage("The name must only contain alpha-numeric characters.");
                return;
            }
            else if (CharNameTable.doesCharNameExist(val[1]))
            {
                activeChar.sendMessage("The name chosen is already in use. Choose another name.");
                return;
            }
           
            if (activeChar.isInParty())
            {
                activeChar.getParty().broadcastToPartyMembers(activeChar, new PartySmallWindowDeleteAll());
                for (L2PcInstance member : activeChar.getParty().getPartyMembers())
                {
                    if (member != activeChar)
                        member.sendPacket(new PartySmallWindowAll(member, activeChar.getParty()));
                }
            }
            if (activeChar.getClan() != null)
                activeChar.getClan().broadcastClanStatus();
           
            L2World.getInstance().removeFromAllPlayers(activeChar);
            activeChar.setName(val[1]);
            activeChar.store();
            L2World.getInstance().addToAllPlayers(activeChar);
            activeChar.sendMessage("Your name has been changed successfully.");
            activeChar.broadcastUserInfo();
           
            if (!activeChar.destroyItemByItemId("Donate Coin", itemid, ammount, activeChar, false))
                return;
        }
        else
        {
            activeChar.sendMessage("You do not have enough Donate Coin.");
        }
    }
   
    public static void Enchant(L2PcInstance activeChar, int enchant, int ammount, int type)
    {
        L2ItemInstance item = activeChar.getInventory().getPaperdollItem(type);
       
        if (activeChar.getInventory().getInventoryItemCount(itemid, 0) >= ammount)
        {
            if (item == null)
            {
                activeChar.sendMessage("That item doesn't exist in your inventory.");
                return;
            }
            else if (item.getEnchantLevel() == 20)
            {
                activeChar.sendMessage("Your " + item.getItemName() + " is already on maximun enchant!");
                return;
            }
            else if (item.getItem().getCrystalType() == 0 || item.getItem().getCrystalType() == 1 || item.getItem().getCrystalType() == 2)
            {
                activeChar.sendMessage("You can't Enchant under " + item.getItem().getCrystalType() + " Grade Weapon!");
                return;
            }
            else if (item.isHeroItem())
            {
                activeChar.sendMessage("You Cannot be Enchant On " + item.getItemName() + " !");
                return;
            }
           
            if (item.isEquipped())
            {
                item.setEnchantLevel(enchant);
                item.updateDatabase();
                activeChar.sendPacket(new ItemList(activeChar, false));
                activeChar.broadcastUserInfo();
                activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S2_SUCCESSFULLY_ENCHANTED).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
                DonateAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "Donated: " + item.getItemName() + " +" + item.getEnchantLevel(), "Donate Coins:" + ammount);
            }
            if (!activeChar.destroyItemByItemId("Donate Coin", itemid, ammount, activeChar, false))
                return;
        }
        else
        {
            activeChar.sendMessage("You do not have enough Donate Coin.");
        }
    }
}