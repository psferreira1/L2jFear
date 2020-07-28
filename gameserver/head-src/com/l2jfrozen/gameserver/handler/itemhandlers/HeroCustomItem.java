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

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.handler.IItemHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfrozen.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jfrozen.gameserver.network.serverpackets.SocialAction;



public class HeroCustomItem implements IItemHandler
{
    private static final int ITEM_IDS[] = 
    {
        Config.HERO_CUSTOM_ITEM_ID
    };
    
    @Override
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
    {
            if(!(playable instanceof L2PcInstance))
                return;
            L2PcInstance activeChar = (L2PcInstance)playable;
            if(activeChar.isHero())
            {
            	activeChar.sendPacket(new ExShowScreenMessage("You Are Already A Server Hero!", 4000));
                return;
            }
			activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 16));
			activeChar.setHero(true);
			activeChar.sendPacket(new ExShowScreenMessage("You Are Now a Server Hero, And Hero Skills, After Restart Hero Removed!", 4000));
			activeChar.broadcastUserInfo();
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
        }

    @Override
	public int[] getItemIds()
    {
        return ITEM_IDS;
    }

	/* (non-Javadoc)
	 * @see com.l2jfrozen.gameserver.handler.IItemHandler#useItem(com.l2jfrozen.gameserver.model.actor.instance.L2PlayableInstance, com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance)
	 */
    {
	}
}