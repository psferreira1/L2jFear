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
package com.l2jfrozen.gameserver.handler.admincommandhandlers;

import com.l2jfrozen.gameserver.custom.BalanceManager;
import com.l2jfrozen.gameserver.custom.ClassBalanceBBS;
import com.l2jfrozen.gameserver.handler.IAdminCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;



/**
 * This class handles following admin commands: - target name = sets player with respective name as target
 */
public class AdminClassBalancer implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_classbalancer",
		"admin_loadclassbalancer",
		"admin_updateclassbalancer"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_classbalancer"))
		{
			ClassBalanceBBS.getInstance().parseCmd(command, activeChar);
		}
		else if (command.equalsIgnoreCase("admin_loadclassbalancer"))
		{
			BalanceManager.getInstance().loadBalances();
			activeChar.sendMessage("Class balances has successfully been loaded!");
		}
		else if (command.equalsIgnoreCase("admin_updateclassbalancer"))
		{
			BalanceManager.getInstance().updateBalances(activeChar);
			activeChar.sendMessage("Class balances has successfully been updated!");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}