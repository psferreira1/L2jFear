/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jfrozen.gameserver.model.actor.instance;


import java.util.concurrent.ScheduledFuture;

import javolution.text.TextBuilder;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;

/**
 * @author fofas123
 */
public class L2Protector1Instance extends L2NpcInstance
{
	private ScheduledFuture<?> _aiTask;
	
	private class ProtectorAI implements Runnable
	{
		private L2Protector1Instance _caster;

		protected ProtectorAI(L2Protector1Instance caster)
		{
			_caster = caster;
		}

		@Override
		public void run()
		{
			/**
			 * For each known player in range, cast sleep if pvpFlag != 0 or Karma >0 Skill use is just for buff
			 * animation
			 */
			for(L2PcInstance player : getKnownList().getKnownPlayers().values())

			{
				if(player.getKarma() != 0 && Config.PROTECTOR_PLAYER_PK)
				{
					kill(player,_caster);
				}
			}
		}

		private boolean kill(L2PcInstance player, L2Character target)
		{
			if(player.isGM() || player.isDead() || !player.isVisible() || !isInsideRadius(player, Config.PROTECTOR_RADIUS_ACTION, false, false))
				return false;
			
			NpcHtmlMessage msg = new NpcHtmlMessage(this.getObjectId());
			msg.setHtml(sendCompleteWindow(player));
			msg.replace("objectId", String.valueOf(this.getObjectId()));
		    player.sendPacket(msg);			
			player.getStatus().setCurrentHp(0);
			player.doDie(player);
			player.setKarma(0);
			player.removeSkill(SkillTable.getInstance().getInfo(3603, 1));
			return true;
		}

		private int getObjectId() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	String sendCompleteWindow(L2PcInstance player)
	{
		TextBuilder tb = new TextBuilder();
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		
	    tb.append("<html><body><center>");
	    tb.append("<img src=\"l2ceriel.1\" width=300 height=100>");
	    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	    tb.append("<table bgcolor=000000 width=319>");
	    tb.append("<tr>");
	    tb.append("<td><center><font color=\"ae9988\">Zone Protector</font><center></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	    tb.append("<br>");
		tb.append("<br>");
		tb.append("<center><font color=\"FFFF00\">NO PK HERE!</font></center>");
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
	      tb.append("<td><center><font color=\"E80000\">l2xs.eu</font></center></td>");
	      tb.append("</tr>");
	      tb.append("</table>");
	      tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	      tb.append("</body></html>");
		html.setHtml(tb.toString());
		player.sendPacket(html);
		return tb.toString();
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
	    	  showChatWindow(player);
	      }

	      player.sendPacket(new ActionFailed());
	    }

  @Override
	public void showChatWindow(L2PcInstance player)
    {
	    NpcHtmlMessage nhm = new NpcHtmlMessage(this.getObjectId());
	    TextBuilder tb = new TextBuilder("");
	   
	    tb.append("<html><body><center>");
	    tb.append("<img src=\"l2ceriel.1\" width=300 height=100>");
	    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	    tb.append("<table bgcolor=000000 width=319>");
	    tb.append("<tr>");
	    tb.append("<td><center><font color=\"ae9988\">Zone Protector</font><center></td>");
	    tb.append("</tr>");
	    tb.append("</table>");
	    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	    tb.append("<br>");
		tb.append("<br>");
		tb.append("<center><font color=\"FFFF00\">NO PK HERE!</font></center>");
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
	    tb.append("<br>");
        tb.append("<br>");
	    tb.append("<br>");
	    tb.append("<br>");
	    tb.append("<br>");
	      tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	      tb.append("<table bgcolor=000000 width=319>");	
	      tb.append("<tr>");
	      tb.append("<td><center><font color=\"E80000\">l2xs.eu</font></center></td>");
	      tb.append("</tr>");
	      tb.append("</table>");
	      tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
	      tb.append("</body></html>");
	    nhm.setHtml(tb.toString());
	    nhm.replace("%objectId%", String.valueOf(this.getObjectId()));
	    player.sendPacket(nhm);

	    player.sendPacket(new ActionFailed());
    }
    public static void Protect(L2PcInstance player) {
		// TODO Auto-generated method stub
		
	}
	public L2Protector1Instance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		
		if(_aiTask != null)
		{
			_aiTask.cancel(true);
		}

		_aiTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new ProtectorAI(this), 3000, 3000);
		
	}
    
	@Override
	public void deleteMe()
	{
		if(_aiTask != null)
		{
			_aiTask.cancel(true);
			_aiTask = null;
		}
		
		super.deleteMe();
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
}

