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
package com.l2jfrozen.gameserver.handler.voicedcommandhandlers;

import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;

public class menu implements IVoicedCommandHandler
{
	private static String[] _voicedCommands =
	{
		"menu"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if(activeChar.isInOlympiadMode())
		{
				activeChar.sendMessage("This Command Cannot Be Used On Olympiad Games.");
		}  
		
		if(command.equalsIgnoreCase("menu"))
		{
			int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size();
			NpcHtmlMessage html = new NpcHtmlMessage(1);
		    html.setFile("data/html/info/welcome.htm");
			html.replace("%name%", activeChar.getName());
			html.replace("%online%", "<font color=\"FFFF00\">Players Online:</font>" + PLAYERS_ONLINE);
		   activeChar.sendPacket(html);
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}