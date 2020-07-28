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
package com.l2jfrozen.gameserver.handler.itemhandlers;

import com.l2jfrozen.gameserver.handler.IItemHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfrozen.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author Crystalia
 *
 */
public class RecItem implements IItemHandler
{

	private static final int ITEM_IDS[] = {
		3172
    };

	@Override
	public int[] getItemIds()
    {
        return ITEM_IDS;
    }

	@Override
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if(!(playable instanceof L2PcInstance))
            return;
        L2PcInstance activeChar = (L2PcInstance)playable;
        {
        	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
        	activeChar.setRecomHave(255);
        	activeChar.sendPacket(new ExShowScreenMessage("Thanks for using our item in order to increase your current recommendations.", 4000));
           activeChar.broadcastUserInfo();
        }
		
	}
	
}