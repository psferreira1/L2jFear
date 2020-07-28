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
package com.l2jfrozen.gameserver.model.actor.instance;
import javolution.text.TextBuilder;



import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;

/**
 * @author fofas123
 *
 */
public class L2ClanManagerInstance extends L2FolkInstance
{
	  public L2ClanManagerInstance(int objectId, L2NpcTemplate template)
	  {
	    super(objectId, template);
	  }
	  
	  @Override
	  public void onBypassFeedback(L2PcInstance player, String command)
	    {
	  	      if(player == null)
	  	      {
	  	         return;
	  	      }

	  	      if(command.startsWith("clan"))
	  	      {
	  	    	  clanReward(player);
	  	      }
	  	      
	  	      if(command.startsWith("reputation"))
	  	      {
	  	    	  reputation(player);
	  	      }
	  	      
	    }
      
		  @Override
		  public void onAction(L2PcInstance player)
		    {
		      if (!canTarget(player)) {
		        return;
		      }

		      if (this != player.getTarget())
		      {
		        player.setTarget(this);

		        player.sendPacket(new MyTargetSelected(getObjectId(), 0));

		        player.sendPacket(new ValidateLocation(this));
		      }
		      else if (!canInteract(player))
		      {
		        player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
		      }
		      else
		      {
		        showClanWindow(player);
		      }

		      player.sendPacket(new ActionFailed());
		    }
	  
	  public void showClanWindow(L2PcInstance activeChar)
	  {
	    NpcHtmlMessage nhm = new NpcHtmlMessage(5);
	    TextBuilder tb = new TextBuilder("");
	    
	    tb.append("<html><body><center>");
	    tb.append("<img src=\"l2ceriel.1\"width=300 height=100>");
	    tb.append("<br>");
	    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	    tb.append("<table bgcolor=000000 width=319>");
	    tb.append("<tr>");
	    tb.append("<td><center><font color=\"ae9988\">Hey, Clan Leaders Clan LvL UP Their Clans.</font><center></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	    tb.append("<br>");
	    tb.append("<table bgcolor=000000 width=220>");
	    tb.append("<tr>");
	    tb.append("<td><center><font color=\"FFFFFF\"></font>This is the only way to get a clan LvL 8 and</font></center></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<table bgcolor=000000 width=220>");
	    tb.append("<tr>");
	    tb.append("<td><center><font color=\"FFFFFF\"></font>also so easy to get reputation points.</font></center></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<br>"); 
	    tb.append("</tr>");
	    tb.append("<table width=210>");
	    tb.append("<tr>"); 
	    tb.append("<td> <button value=\"Make My Clan LvL 8\" action=\"bypass -h npc_" + getObjectId() + "_clan\"  width=132 height=21 back=\"l2ceriel.bat\" fore=\"l2ceriel.bat\"></center></td>");
	    tb.append("<td> <button value=\"Clan Reputation\" action=\"bypass -h npc_" + getObjectId() + "_reputation\"  width=132 height=21 back=\"l2ceriel.bat\" fore=\"l2ceriel.bat\"></center></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<table width=3000>");
	    tb.append("<br>"); 
	    tb.append("<tr>");
	    tb.append("<td><font color=\"00FFFFF\"></font>Hey , You can up your Clan's LeveL.</font></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<table width=3000>");
	    tb.append("<tr>");
	    tb.append("<td><font color=\"980000\"></font>You must be the leader of the clan.</font></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<table width=3000>");
	    tb.append("<tr>");
	    tb.append("<td><font color=\"989898\"></font>You Need 50 Vote Stone to make your Clan LvL 8.</font></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<table width=3000>");
	    tb.append("<tr>");
	    tb.append("<td><font color=\"00FFFFF\"></font>You Need 25 Clan Item to Get 1000000 Reputation Points.</font></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<table width=3000>");
	    tb.append("<tr>");
	    tb.append("<td><font color=\"00FFFFF\"></font>You take Clan Item From Vote and RaidBoss.</font></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<br>");
	    tb.append("<br>");
	    tb.append("<br>");
	    tb.append("<br>");	    tb.append("<br>");
	    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	    tb.append("<table bgcolor=000000 width=319>");	
	    tb.append("<tr>");
	    tb.append("<td><center><font color=\"E80000\">L2Ryan.eu</font></center></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	    tb.append("</body></html>");

	    nhm.setHtml(tb.toString());
	    activeChar.sendPacket(nhm);
	  }	  
	  
	  public static void ClanOk(L2PcInstance activeChar)
	  {
		    NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		    StringBuilder tb = new StringBuilder("");
		    
		    tb.append("<html><body><center>");
		    tb.append("<img src=\"l2ceriel.1\"width=300 height=100>");
		    tb.append("<br>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<table bgcolor=000000 width=319>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"ae9988\">Hey, Clan Leaders Clan LvL UP Their Clans.</font><center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<br>");
		    tb.append("<table bgcolor=000000 width=220>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"FFFFFF\"></font>This is the only way to get a clan LvL 8 and</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<table bgcolor=000000 width=220>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"FFFFFF\"></font>also so easy to get reputation points.</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<br>");	    
		    tb.append("<table width=3000>");
		    tb.append("<tr>");
		    tb.append("<td><font color=\"980000\"></font>Thank you very much</font></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<table width=3000>");
		    tb.append("<tr>");
		    tb.append("<td><font color=\"980000\"></font>Your clan is now level 8.</font></td>");
		    tb.append("</tr>");
		    tb.append("<tr>");
		    tb.append("</table>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<table bgcolor=000000 width=319>");	
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"E80000\">L2Ryan.eu</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("</body></html>");

		    nhm.setHtml(tb.toString());
		    activeChar.sendPacket(nhm);
	  }
	  
	  public static void repOk(L2PcInstance activeChar)
	  {
		    NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		    StringBuilder tb = new StringBuilder("");
		    
		    tb.append("<html><body><center>");
		    tb.append("<img src=\"l2ceriel.1\"width=300 height=100>");
		    tb.append("<br>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<table bgcolor=000000 width=319>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"ae9988\">Hey, Clan Leaders Clan LvL UP Their Clans.</font><center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<br>");
		    tb.append("<table bgcolor=000000 width=220>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"FFFFFF\"></font>This is the only way to get a clan LvL 8 and</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<table bgcolor=000000 width=220>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"FFFFFF\"></font>also so easy to get reputation points.</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<br>");	    
		    tb.append("<table width=3000>");
		    tb.append("<tr>");
		    tb.append("<td><font color=\"980000\"></font>Thank you very much</font></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<table width=3000>");
		    tb.append("<tr>");
		    tb.append("<td><font color=\"980000\"></font>Your clan have now +10000 Reputations points</font></td>");
		    tb.append("</tr>");
		    tb.append("<tr>");
		    tb.append("</table>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<table bgcolor=000000 width=319>");	
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"E80000\">L2Ryan.eu</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("</body></html>");
		    nhm.setHtml(tb.toString());
		    activeChar.sendPacket(nhm);
	  }
	  
	  public static void noclan(L2PcInstance activeChar)
	  {
		    NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		    StringBuilder tb = new StringBuilder("");
		    
		    
		    tb.append("<html><body><center>");
		    tb.append("<img src=\"l2ceriel.1\"width=300 height=100>");
		    tb.append("<br>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<table bgcolor=000000 width=319>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"ae9988\">Hey, Clan Leaders Clan LvL UP Their Clans.</font><center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<br>");
		    tb.append("<table bgcolor=000000 width=220>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"FFFFFF\"></font>This is the only way to get a clan LvL 8 and</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<table bgcolor=000000 width=220>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"FFFFFF\"></font>also so easy to get reputation points.</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<br>");	    
		    tb.append("<table width=3000>");
		    tb.append("<br>"); 
		    tb.append("<tr>");
		    tb.append("<td><font color=\"00FFFFF\"></font>Seems Something is Wrong.</font></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<table width=3000>");
		    tb.append("<tr>");
		    tb.append("<td><font color=\"980000\"></font>Check the Requirments and try again once you got them.</font></td>");
		    tb.append("</tr>");
		    tb.append("<tr>");
		    tb.append("</table>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<table bgcolor=000000 width=319>");	
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"E80000\">L2Ryan.eu</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("</body></html>");
		    nhm.setHtml(tb.toString());
		    activeChar.sendPacket(nhm);
	  }
	  
	  public static void norep(L2PcInstance activeChar)
	  {
		    NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		    StringBuilder tb = new StringBuilder("");
		    
		    tb.append("<html><body><center>");
		    tb.append("<img src=\"l2ceriel.1\"width=300 height=100>");
		    tb.append("<br>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<table bgcolor=000000 width=319>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"ae9988\">Hey, Clan Leaders Clan LvL UP Their Clans.</font><center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<br>");
		    tb.append("<table bgcolor=000000 width=220>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"FFFFFF\"></font>This is the only way to get a clan LvL 8 and</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<table bgcolor=000000 width=220>");
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"FFFFFF\"></font>also so easy to get reputation points.</font></center></td>");
		    tb.append("</tr>");
		    tb.append("<tr>");
		    tb.append("</table>");
		    tb.append("<br>");	    
		    tb.append("<table width=3000>");
		    tb.append("<br>"); 
		    tb.append("<tr>");
		    tb.append("<td><font color=\"00FFFFF\"></font>Seems Something is Wrong.</font></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<table width=3000>");
		    tb.append("<tr>");
		    tb.append("<td><font color=\"980000\"></font>Check the Requirments and try again once you got them.</font></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<br>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("<table bgcolor=000000 width=319>");	
		    tb.append("<tr>");
		    tb.append("<td><center><font color=\"E80000\">L2Ryan.eu</font></center></td>");
		    tb.append("</tr>");
		    tb.append("</table>");
		    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		    tb.append("</body></html>");
		    nhm.setHtml(tb.toString());
		    activeChar.sendPacket(nhm);
	  }
	  
	  public static void ClanEnable(L2PcInstance activeChar)
	  {
		    NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		    TextBuilder tb = new TextBuilder("");
		    
		    tb.append("<html><body><center>");
		    tb.append("<br>");
		    tb.append("<img src=\"systemfreaks.1\" width=256 height=90>");
		    tb.append("<br><font color=\"989898\">Clan Manager</font>");
	        tb.append("<br>");
		    tb.append("<center><font color=\"FF0000\">Soory Dont Enable Clan Manager!<br></font>");
	        tb.append("<br>");
	        tb.append("<font color=\"003366\" align=<center>");
	        tb.append("<img src=\"L2Font.mini_logo-k\" width=180 height=80>");
	        tb.append("<br><font color=\"003366\"></font>");
	        tb.append("</body></html>");

		    nhm.setHtml(tb.toString());
		    activeChar.sendPacket(nhm);
	  }	  
	  
	  public static void reputation(L2PcInstance activeChar)
	  {
			if(!activeChar.isClanLeader())
			{	
				norep(activeChar);
				return;
			}
			
		       if (activeChar.getInventory().getInventoryItemCount(6690, 0) >= 25)
				{		  
					  if(activeChar.isClanLeader())
						  repOk(activeChar);
					  activeChar.getClan().setReputationScore(activeChar.getClan().getReputationScore()+100000000, true);
					  if (!activeChar.destroyItemByItemId("Vote Coin", 6690, 25, activeChar, false))
						return;
				}
				else
			    {
				   norep(activeChar);
				}	
	      }
	  
	  public static void clanReward(L2PcInstance activeChar)
	  {
		  if(activeChar.isClanLeader() && activeChar.getClan().getLevel() == 8)
		  {
			  noclan(activeChar);
			  return;
		  }
		  
			if(!activeChar.isClanLeader())
			{	
				noclan(activeChar);
				return;
			}
			
		      if (activeChar.getInventory().getInventoryItemCount(7570, 0) >= 50)
				{		  
					  if(activeChar.isClanLeader() && activeChar.getClan().getLevel() < 8 && activeChar.isNoble())
						  ClanOk(activeChar);
						  activeChar.getClan().changeLevel(8);
						  activeChar.getClan().setReputationScore(1, true);
					  if (!activeChar.destroyItemByItemId("Vote Coin",7570, 50, activeChar, false))
						return;
				}
				else
			    {
				   noclan(activeChar);
				}	
	      }
     }