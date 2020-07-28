package com.l2jfrozen.gameserver.handler.voicedcommandhandlers;

import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;

public class menu1
  implements IVoicedCommandHandler
{
  private static final String[] _voicedCommands = { "menu1", "MENU1", "enableTrade", "disableTrade", "enableMessage", "disableMessage", "xpOn", "xpOff", "partyOn", "partyOff" };
  
  @Override
public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
  {
    if (command.equalsIgnoreCase("menu1"))
    {
      showMainPage(activeChar);
      return true;
    }
    if (command.equalsIgnoreCase("MENU1"))
    {
      showMainPage(activeChar);
      return true;
    }
    if (command.startsWith("enableTrade"))
    {
      activeChar.setIsInTradeProt(false);
      showMainPage(activeChar);
      return false;
    }
    if (command.startsWith("disableTrade"))
    {
      activeChar.setIsInTradeProt(true);
      showMainPage(activeChar);
      return false;
    }
    if (command.startsWith("enableMessage"))
    {
      activeChar.setMessageRefusal(false);
      showMainPage(activeChar);
      return false;
    }
    if (command.startsWith("disableMessage"))
    {
      activeChar.setMessageRefusal(true);
      showMainPage(activeChar);
      return false;
    }
    if (command.startsWith("xpOn"))
    {
      activeChar.cantGainXP(false);
      showMainPage(activeChar);
      return false;
    }
    if (command.startsWith("xpOff"))
    {
      activeChar.cantGainXP(true);
      showMainPage(activeChar);
      return false;
    }
    if (command.startsWith("partyOn"))
    {
      activeChar.setIsPartyInvProt(false);
      showMainPage(activeChar);
      return false;
    }
    if (command.startsWith("partyOff"))
    {
      activeChar.setIsPartyInvProt(true);
      showMainPage(activeChar);
      return false;
    }
    return false;
  }
  
  private static String getTradeMode(L2PcInstance activeChar)
  {
    String result = "<font color=\"FF0000\">OFF</font>";
    if (activeChar.isInTradeProt()) {
      result = "<font color=\"00FF00\">ON</font>";
    }
    return result;
  }
  
  private static String getMessageMode(L2PcInstance activeChar)
  {
    String result = "<font color=\"FF0000\">OFF</font>";
    if (activeChar.getMessageRefusal()) {
      result = "<font color=\"00FF00\">ON</font>";
    }
    return result;
  }
  
  private static String getGainExpMode(L2PcInstance activeChar)
  {
    String result = "<font color=\"FF0000\">OFF</font>";
    if (activeChar.cantGainXP()) {
      result = "<font color=\"00FF00\">ON</font>";
    }
    return result;
  }
  
  private static String getPartyMode(L2PcInstance activeChar)
  {
    String result = "<font color=\"FF0000\">OFF</font>";
    if (activeChar.isPartyInvProt()) {
      result = "<font color=\"00FF00\">ON</font>";
    }
    return result;
  }
  
  private static void showMainPage(L2PcInstance activeChar)
  {
    NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
    html.setFile("data/html/mods/menu/Menu.htm");
    html.replace("%notrade%", getTradeMode(activeChar));
    html.replace("%partymod%", getPartyMode(activeChar));
    html.replace("%nomsg%", getMessageMode(activeChar));
    html.replace("%gainxp%", getGainExpMode(activeChar));
    activeChar.sendPacket(html);
  }
  
  @Override
public String[] getVoicedCommandList()
  {
    return _voicedCommands;
  }
}
