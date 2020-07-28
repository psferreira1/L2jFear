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

import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfrozen.gameserver.handler.VoteHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;

public class L2VoteManager1Instance extends L2FolkInstance
{
  public L2VoteManager1Instance(int objectId, L2NpcTemplate template)
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

 		if (command.startsWith("votetopzone"))
 		{
 			VoteHandler.tzvote(player);
 		}
 		if (command.startsWith("votehopzone"))
 		{
 			VoteHandler.HZvote(player);
 		}
 		if (command.startsWith("votenetwork"))
 		{
 			VoteHandler.NZvote(player);
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
      showHtmlWindow(player);
    }

    player.sendPacket(new ActionFailed());
  }

  private void showHtmlWindow(L2PcInstance activeChar)
  { 
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	    String filename = "data/html/vote.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		activeChar.sendPacket(html);
  }
}