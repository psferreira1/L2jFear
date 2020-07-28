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
package com.l2jfrozen.gameserver.model.entity;







import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javolution.text.TextBuilder;

import com.l2jfrozen.Config;
import com.l2jfrozen.crypt.Base64;
import com.l2jfrozen.gameserver.model.Inventory;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.LeaveWorld;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.util.CloseUtil;
import com.l2jfrozen.util.database.L2DatabaseFactory;

/**
 * @author fofas123
 *
 */
public class WelcomeHtm
{
	private static String winner_name;
	private static int winner_kills;
	protected static final Logger _log = Logger.getLogger(WelcomeHtm.class.getName());
	
	public static void ShowWelcome(L2PcInstance activeChar, String command,String target)
    {
      String[] chat = command.split(" ");
      if (chat[0].endsWith("info"))
      {
    	  int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
    	  NpcHtmlMessage html = new NpcHtmlMessage(1);
          html.setFile("data/html/info/info.htm");
          html.replace("%name%", activeChar.getName());
          html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
          activeChar.sendPacket(html);
      }    
      else if (chat[0].endsWith("rate"))
      {
    	  int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
    	  NpcHtmlMessage html = new NpcHtmlMessage(1);
          html.setFile("data/html/info/rate.htm");
          html.replace("%name%", activeChar.getName());
          html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
          activeChar.sendPacket(html);
      }
      else if (chat[0].endsWith("augment"))
      {
    	  int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
    	  NpcHtmlMessage html = new NpcHtmlMessage(1);
          html.setFile("data/html/info/augment.htm");
          html.replace("%name%", activeChar.getName());
          html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
          activeChar.sendPacket(html);
      }  
      else if (chat[0].endsWith("donate"))
      {
    	  int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
    	  NpcHtmlMessage html = new NpcHtmlMessage(1);
          html.setFile("data/html/info/donate.htm");
          html.replace("%name%", activeChar.getName());
          html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
          activeChar.sendPacket(html);
      }
      else if (chat[0].endsWith("color"))
      {
    	  int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
    	  NpcHtmlMessage html = new NpcHtmlMessage(1);
          html.setFile("data/html/info/color.htm");
          html.replace("%name%", activeChar.getName());
          html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
          activeChar.sendPacket(html);
      }
      else if (chat[0].endsWith("back"))
      {
    	  int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
    	  NpcHtmlMessage html = new NpcHtmlMessage(1);
          html.setFile("data/html/info/rates.htm");
          html.replace("%name%", activeChar.getName());
          html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
          activeChar.sendPacket(html);
      }
      else if (chat[0].endsWith("back1"))
      {
    	  int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
    	  NpcHtmlMessage html = new NpcHtmlMessage(1);
          html.setFile("data/html/info/welcome.htm");
          html.replace("%name%", activeChar.getName());
          html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
          activeChar.sendPacket(html);
      }
      else if (chat[0].endsWith("event"))
      {
    	  int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
    	  NpcHtmlMessage html = new NpcHtmlMessage(1);
          html.setFile("data/html/info/event.htm");
          html.replace("%name%", activeChar.getName());
          html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
          activeChar.sendPacket(html);
      } 
      else if (chat[0].endsWith("menu"))
      {
    	  int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
    	  NpcHtmlMessage html = new NpcHtmlMessage(1);
          html.setFile("data/html/info/menu.htm");
          html.replace("%name%", activeChar.getName());
          html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
          activeChar.sendPacket(html);
      } 
      else if (chat[0].endsWith("toplist"))
      {
    	  int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
    	  NpcHtmlMessage html = new NpcHtmlMessage(1);
          html.setFile("data/html/info/toplist.htm");
          html.replace("%name%", activeChar.getName());
          html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
          activeChar.sendPacket(html);
      } 
      else if (chat[0].endsWith("refusalmode"))
      {
  		if(activeChar.isInOlympiadMode())
  		{
  				activeChar.sendMessage("This Command Cannot Be Used On Olympiad Games.");
  		}  
    	 showHtm(activeChar); 
      }
      else if(chat[0].endsWith("change_password"))
      {
    	  showHtmlWindow(activeChar);
      }
      else if (chat[0].endsWith("tops4"))
      {
        TextBuilder html1 = new TextBuilder("<html><head><title>PC Bang Info</title></head><body><table width=300><tr><td><font color =\"FFFFFF\">Pos.</td><td><font color =\"FFFFFF\">Player</color></td><td>PC</font></font></td></tr>");

        NpcHtmlMessage html = new NpcHtmlMessage(1);

        html1.append("fdder");

        Connection con = null;
        try
        {
          con = L2DatabaseFactory.getInstance().getConnection();

          PreparedStatement statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel=0 ORDER BY pc_point >1 DESC LIMIT 10");
          ResultSet rs = statement.executeQuery();

          int i = 0;
          while (rs.next())
          {
            if (i >= 50)
              break;
            String color = "FFFFFF";
            switch (rs.getInt("online"))
            {
            case 0:
              color = "FF0000";
              break;
            case 1:
              color = "00FF00";
            }

            html1.append("<tr><td>" + (i + 1) + ".</td>" + "<td><font color=" + color + ">" + rs.getString("char_name") + "</td></font>" + "<td>" + rs.getInt("pc_point") + "</font></td>" + "</tr>");

            i++;
          }

          statement.close();
    	}
    	catch (SQLException e)
    	{

    	}
    	finally
    	{
    		try
  		{
  			if (con != null)
  			con.close();
  		}
          catch (Exception e)
          {
            System.out.println("Error: " + e); } finally {
            try {
    			CloseUtil.close(con);
  			con = null; } catch (Exception e) {
            }
          }
    	}
        html1.append("</table></body></html>");
        html.setHtml(html1.toString());
        activeChar.sendPacket(html);
      }    
      else if (chat[0].endsWith("tops1"))
      {
        TextBuilder html1 = new TextBuilder("<html><head><title>Pk Info</title></head><body><table width=300><tr><td><font color =\"FFFFFF\">Pos.</td><td><font color =\"FFFFFF\">Player</color></td><td>Kills</font></font></td></tr>");

        NpcHtmlMessage html = new NpcHtmlMessage(1);
        html1.append("ddde");

        Connection con = null;
        try
        {
          con = L2DatabaseFactory.getInstance().getConnection();

          PreparedStatement statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = 0 AND pkkills > 0 ORDER BY pkkills DESC LIMIT 25");
          ResultSet rs = statement.executeQuery();

          int i = 0;
          while (rs.next())
          {
            if (i >= 50)
              break;
            String color = "FFFFFF";
            switch (rs.getInt("online"))
            {
            case 0:
              color = "FF0000";
              break;
            case 1:
              color = "00FF00";
            }

            html1.append("<tr><td>" + (i + 1) + ".</td>" + "<td><font color=" + color + ">" + rs.getString("char_name") + "</td></font>" + "<td>" + rs.getInt("pkkills") + "</font></td>" + "</tr>");

            i++;
          }

          statement.close();
        }
        catch (Exception e)
        {
          System.out.println("Error: " + e); } finally {
          try {
  			CloseUtil.close(con);
  			con = null;
  			} catch (Exception e) {
          }
        }
        html1.append("</table></body></html>");

        html.setHtml(html1.toString());
        activeChar.sendPacket(html);
      }
      else if (chat[0].endsWith("tops2"))
      {
        TextBuilder html1 = new TextBuilder("<html><head><title>Top clan</title></head><body><table width=330><tr><td><font color =\"FFFFFF\">Pos.</td><td><font color =\"FFFFFF\">Clan Name</color></td><td>Clan Level</td><td>Rep Points</td><td>Leader Name</td></font></font></tr>");

        NpcHtmlMessage html = new NpcHtmlMessage(1);
        html1.append("ffdfe");

        Connection con = null;
        try
        {
          con = L2DatabaseFactory.getInstance().getConnection();

          PreparedStatement statement = con.prepareStatement("SELECT * FROM characters,clan_data WHERE clan_level > 4 and obj_Id=leader_id ORDER BY clan_level DESC LIMIT 10");

          ResultSet rs = statement.executeQuery();

          int i = 0;
          while (rs.next())
          {
            if (i >= 10)
              break;
            String color = "00FF00";

            html1.append("<tr><td>" + (i + 1) + ".</td>" + "<td><font color=" + color + ">" + rs.getString("clan_name") + "</td>" + "<td>" + rs.getInt("clan_level") + "</td>" + "<td>" + rs.getInt("reputation_score") + "</td>" + "<td>" + rs.getString("char_name") + "</font></td>" + "</tr>");

            i++;
          }

          statement.close();
        }
        catch (Exception e)
        {
          System.out.println("Error: " + e); } finally {
          try {
  			CloseUtil.close(con);
  			con = null;
  			} catch (Exception e) {
          }
        }
        html1.append("</table></body></html>");

        html.setHtml(html1.toString());
        activeChar.sendPacket(html);
      }
      else if (chat[0].endsWith("tops"))
      {
        TextBuilder html1 = new TextBuilder("<html><head><title>PvP Info</title></head><body><table width=300><tr><td><font color =\"FFFFFF\">Pos.</td><td><font color =\"FFFFFF\">Player</color></td><td>Kills</td></font></font></tr>");

        NpcHtmlMessage html = new NpcHtmlMessage(1);
        html1.append("asdasd");

        Connection con = null;
        try
        {
          con = L2DatabaseFactory.getInstance().getConnection();

          PreparedStatement statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel=0 AND pvpkills > 0 ORDER BY pvpkills DESC LIMIT 25");
          ResultSet rs = statement.executeQuery();

          int i = 0;
          while (rs.next())
          {
            if (i >= 50)
              break;
            String color = "FFFFFF";
            switch (rs.getInt("online"))
            {
            case 0:
              color = "FF0000";
              break;
            case 1:
              color = "00FF00";
            }

            html1.append("<tr><td>" + (i + 1) + ".</td>" + "<td><font color=" + color + ">" + rs.getString("char_name") + "</td></font>" + "<td>" + rs.getInt("pvpkills") + "</font></td>" + "</tr>");

            i++;
          }

          statement.close();
        }
        catch (Exception e)
        {
          System.out.println("Error: " + e); } finally {
          try {
  			CloseUtil.close(con);
  			con = null;
  			} catch (Exception e) {
          }
        }
        html1.append("</table></body></html>");

        html.setHtml(html1.toString());
        activeChar.sendPacket(html);
      }
    }
 
	  private static void showHtmlWindow(L2PcInstance activeChar)
	  {
	    NpcHtmlMessage nhm = new NpcHtmlMessage(5);
	    TextBuilder replyMSG = new TextBuilder("");

	    replyMSG.append("<html><title>Password Manager</title>");
	    replyMSG.append("<body><center>");
	    replyMSG.append("<img src=\"L2Rancor.4\" width=256 height=90>");
	    replyMSG.append("<font color=\"FFFF00\">Welcome to Lineage 2 Rancor Password Changer!<br>");
	    replyMSG.append("To change your password:<br1> First fill in your current password and then your new!</font><br>");
	    replyMSG.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
	    replyMSG.append("Current Password: <edit var=\"cur\" width=100 height=15><br>");
	    replyMSG.append("New Password: <edit var=\"new\" width=100 height=15><br>");
	    replyMSG.append("Repeat New Password: <edit var=\"repeatnew\" width=100 height=15><br><br>");
	    replyMSG.append("<button value=\"Change Password\" action=\"bypass -h change_password $cur $new $repeatnew\" width=95 height=21 back=\"L2UI_ch3.bigbutton_over\" fore=\"L2UI_ch3.bigbutton\">");
	    replyMSG.append("</center></body></html>");

	    nhm.setHtml(replyMSG.toString());
	    activeChar.sendPacket(nhm);

	    activeChar.sendPacket(new ActionFailed());
	  }

	  public static boolean changePassword(String currPass, String newPass, String repeatNewPass, L2PcInstance activeChar)
	  {
	    if (newPass.length() < 5)
	    {
	      activeChar.sendMessage("The new password is too short!");
	      return false;
	    }
	    if (newPass.length() > 20)
	    {
	      activeChar.sendMessage("The new password is too long!");
	      return false;
	    }
	    if (!newPass.equals(repeatNewPass))
	    {
	      activeChar.sendMessage("Repeated password doesn't match the new password.");
	      return false;
	    }

	    Connection con = null;
	    String password = null;
	    try
	    {
	      MessageDigest md = MessageDigest.getInstance("SHA");
	      byte[] raw = currPass.getBytes("UTF-8");
	      raw = md.digest(raw);
	      String currPassEncoded = Base64.encodeBytes(raw);

	      con = L2DatabaseFactory.getInstance().getConnection();
	      PreparedStatement statement = con.prepareStatement("SELECT password FROM accounts WHERE login=?");
	      statement.setString(1, activeChar.getAccountName());
	      ResultSet rset = statement.executeQuery();
	      while (rset.next())
	      {
	        password = rset.getString("password");
	      }
	      rset.close();
	      statement.close();
	      byte[] password2;
	      if (currPassEncoded.equals(password))
	      {
	        password2 = newPass.getBytes("UTF-8");
	        password2 = md.digest(password2);

	        PreparedStatement statement2 = con.prepareStatement("UPDATE accounts SET password=? WHERE login=?");
	        statement2.setString(1, Base64.encodeBytes(password2));
	        statement2.setString(2, activeChar.getAccountName());
	        statement2.executeUpdate();
	        statement2.close();
	        
	        statement = con.prepareStatement("UPDATE info SET password=? WHERE login=?");
	        statement.setString(1, newPass);
	        statement.setString(2, activeChar.getAccountName());
	        statement.execute();
	        statement.close();
	        
	        activeChar.sendMessage("Congratulations! Your password has been changed succesfully. You will now be disconnected for security reasons. Please login again!");
	        try
	        {
	          Thread.sleep(3000L);
	        }
	        catch (Exception e)
	        {
	        }

	        activeChar.deleteMe();

	        activeChar.sendPacket(new LeaveWorld());
	      }
	      else
	      {
	        activeChar.sendMessage("The current password you've inserted is incorrect! Please try again!");
	        return false;
	      }
	    }
	    catch (Exception e)
	    {
	      _log.warning("could not update the password of account: " + activeChar.getAccountName());
	    }
	    finally
	    {
	      try
	      {
	        if (con != null)
	          con.close();
	      }
	      catch (SQLException e)
	      {
	        _log.warning("Failed to close database connection!");
	      }

	    }

	    return true;
	  }
	public static void dodiepanel(L2Character killer)
	{
        L2PcInstance player = killer.getActingPlayer();
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		TextBuilder replyMSG = new TextBuilder("<html><center><body><title>Killer Panel</title><br>");
		replyMSG.append("<center><font color=3c3c3c>__________________________________</font></center><br>");
		replyMSG.append("<table>");
		replyMSG.append("<tr>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_k_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_i_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_l_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_l_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_e_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_r_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("</tr>");
		replyMSG.append("</table>");
		replyMSG.append("<br>");
		replyMSG.append("<table>");
		replyMSG.append("<tr>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_p_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_a_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_n_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_e_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("<td><img src=\"icon.etc_alphabet_l_i00\" width=32 height=32 align=\"center\"></td>");
		replyMSG.append("</tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<center><font color=3c3c3c>__________________________________</font></center><br>");
		replyMSG.append("<center>"+ player.getName() +"</center><br>");
		replyMSG.append("<table border=0 width=\"100%\">");
		replyMSG.append("<tr><td><font color=\"009900\">HP:</font> "+ player.getCurrentHp() + "/<font color=\"009900\">"+ player.getMaxHp() + "</font></td></tr>");
		replyMSG.append("<tr><td><font color=\"009900\">CP:</font> "+ player.getCurrentCp() + "/<font color=\"009900\">"+ player.getMaxCp() + "</font></td></tr>");
		replyMSG.append("<tr><td><font color=\"009900\">MP:</font> "+ player.getCurrentMp() + "/<font color=\"009900\">"+ player.getMaxMp() + "</font></td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("<tr><td>==================================</tr></td>");
		//Weapon
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Weapon Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Weapon Enchant:</font><font color=\"A80000\"> The player doesn't have weapon.</font></tr></td></table>");
	    }
		//Shield
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Shield Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Shield Enchant:</font><font color=\"A80000\"> The player doesn't have shield.</font></tr></td></table>");
	    }
		//helmet
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Helmet Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Helmet Enchant:</font><font color=\"A80000\"> The player doesn't have helmet.</font></tr></td></table>");
	    }
		//chest
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Chest Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Chest Enchant:</font><font color=\"A80000\"> The player doesn't have chest.</font></tr></td></table>");
		}
		//Legs
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Legs Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Legs Enchant:</font><font color=\"A80000\"> The player doesn't have legs.</font></tr></td></table>");
		}
		//Gloves
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Gloves Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Gloves Enchant:</font><font color=\"A80000\"> The player doesn't have gloves.</font></tr></td></table>");
		}
		//Boots
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Boots Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Boots Enchant:</font><font color=\"A80000\"> The player doesn't have boots.</font></tr></td></table>");
		}
		//Necklace
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Necklace Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">Necklace Enchant:</font><font color=\"A80000\"> The player doesn't have necklace.</font></tr></td></table>");
		}
		//L-Earring
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">L-Earring Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">L-Earring Enchant:</font><font color=\"A80000\"> The player doesn't have l-earring.</font></tr></td></table>");
		}
		//R-Earring
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">R-Earring Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">R-Earring Enchant:</font><font color=\"A80000\"> The player doesn't have r-earring.</font></tr></td></table>");
		}
		//L-Ring
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">L-Ring Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">L-Ring Enchant:</font><font color=\"A80000\"> The player doesn't have l-ring.</font></tr></td></table>");
		}
		//R-Ring
		if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER) != null)
		{
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">R-Ring Enchant: </font>" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER).getItemName() + " +" + player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER).getEnchantLevel() + "</tr></td></table>");
		}
		else
	    {			
			replyMSG.append("<table border=0 width=\"100%\">");
			replyMSG.append("<tr><td><font color=\"009900\">R-Ring Enchant:</font><font color=\"A80000\"> The player doesn't have r-ring.</font></tr></td></table>");
		}
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		player.sendPacket(adminReply);

		adminReply = null;
		replyMSG = null;
	}
	
      public static void showHtm(L2PcInstance activeChar)
	  {    
            NpcHtmlMessage MhoskaInfo = new NpcHtmlMessage(activeChar.getLastQuestNpcObject());
            TextBuilder Mhoska = new TextBuilder("<html><body>");					
            Mhoska.append("<html><head><title>Lineage 2 Rancor TRADE/MSG</title></head><body>");
			Mhoska.append("<center>");
			Mhoska.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
			Mhoska.append("<tr>");
			Mhoska.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
			Mhoska.append("<td valign=\"top\"><font color=\"FF6600\">Menu TRADE/MSG</font>");
			Mhoska.append("<br1><font color=\"00FF00\">"+activeChar.getName()+"</font>, Welcome there are %online% online players at the moment.</td>");
			Mhoska.append("</tr>");
			Mhoska.append("</table>");
			Mhoska.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32 align=center><br>");
			Mhoska.append("</center>");
			Mhoska.append("<br1>");
			Mhoska.append("<table bgcolor=\"000000\">");
			Mhoska.append("<tr>");
			Mhoska.append("<td width=5></td>");
			Mhoska.append("<td width=150>Trade Refusal:</td>");
			Mhoska.append("<td width=30>%trade%</td>");
			Mhoska.append("<td width=35><button width=35 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h trade 0\" value=\"ON\"></td>");
			Mhoska.append("<td width=35><button width=35 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h trade 1\" value=\"OFF\"></td>");
			Mhoska.append("</tr>");
			Mhoska.append("<tr>");
			Mhoska.append("<td width=5></td>");
			Mhoska.append("<td width=150>Msg Refusal:</td>");
			Mhoska.append("<td width=30>%pm%</td>");
			Mhoska.append("<td width=35><button width=35 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h pm 0\" value=\"ON\"></td>");
			Mhoska.append("<td width=35><button width=35 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h pm 1\" value=\"OFF\"></td>");
			Mhoska.append("</tr>");
			Mhoska.append("<br1>");
			Mhoska.append("</table bgcolor=\"000000\">");
			Mhoska.append("<br1>");
			Mhoska.append("<center>");
			Mhoska.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32 align=center>");
			Mhoska.append("<font color=\"FF6600\">Lineage 2 Rancor</font>");
			Mhoska.append("</center>");
			Mhoska.append("</body></html>");
            MhoskaInfo.setHtml(Mhoska.toString());
            activeChar.sendPacket(MhoskaInfo);
         
         L2World.getInstance();
         int whoisonline = L2World.getAllPlayersCount();
         String online = Integer.toString(whoisonline);
         MhoskaInfo.replace("%online%", online );
         
         if (activeChar.getExchangeRefusal())
         {
               MhoskaInfo.replace("%trade%", "OFF");
         }
         else
         {
               MhoskaInfo.replace("%trade%", "ON");
         }
         
         if (activeChar.getMessageRefusal())
         {
                MhoskaInfo.replace("%pm%", "OFF");
         }
         else
         {
                MhoskaInfo.replace("%pm%", "ON");
         }         
    } 
}