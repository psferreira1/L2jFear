package com.l2jfrozen.gameserver.handler.voicedcommandhandlers;

import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.olympiad.Olympiad;

public class JoinOly implements IVoicedCommandHandler
{
      
         private static final String[] VOICED_COMMANDS = { "joinoly" , "leaveoly" };


@Override
public boolean useVoicedCommand(String command, L2PcInstance activeChar,String target)
{
    if (command.equalsIgnoreCase("joinoly"))
    {
     if (!activeChar.isInOlympiadMode() )
     {
        Olympiad.getInstance().registerNoble(activeChar, true);
        return true;
   }
        else if (!activeChar.isNoble() || activeChar.isDead() || activeChar.isFestivalParticipant() || activeChar.atEvent
                || activeChar.isSubClassActive() || activeChar.isInDuel() || activeChar.isAlikeDead()
                || activeChar.getPvpFlag() > 0 || activeChar.getKarma() > 0 || activeChar.isInCombat() || activeChar.inObserverMode())    
    {
    activeChar.sendMessage("Your Status Doesnt Allow You To Join Olympiad!");
    return false;
         }
     }
  
    if (command.equalsIgnoreCase("leaveoly"))
     {
        if (activeChar.isInOlympiadMode())
        {
                Olympiad.getInstance().unRegisterNoble(activeChar);
                return true;
        }
        else if (activeChar.isAlikeDead() || activeChar.isInCombat()
                        || activeChar.inObserverMode() || !activeChar.isInOlympiadMode())
                        {
                activeChar.sendMessage("You Can't Leave Olympiad while Participating somewhere.");
                return false;
         }
     }
    
    
    return true;
}

       @Override
	public String[] getVoicedCommandList()
       {
       return VOICED_COMMANDS;
       }
}
