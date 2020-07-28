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
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.taskmanager.AttackStanceTaskManager;


public class MultiFunction implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = {"exit",};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if(!activeChar.isInsideZone(L2Character.ZONE_MULTIFUNCTION))
		{
			activeChar.sendMessage("This Command work Only in PvP Zone!");
			return false;
		}
		
		if (command.startsWith("exit") && activeChar.isInsideZone(L2Character.ZONE_MULTIFUNCTION))
		{	
	        if (AttackStanceTaskManager.getInstance().getAttackStanceTask(activeChar))
	        {	  
				activeChar.sendMessage("You can't use Command while you're in combat.");
				return false;
	        }
			activeChar.teleToLocation(81168, 148626, -3472);
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}