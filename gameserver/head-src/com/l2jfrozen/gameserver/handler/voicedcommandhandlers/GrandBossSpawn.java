package com.l2jfrozen.gameserver.handler.voicedcommandhandlers;

import java.util.Iterator;
import java.util.logging.Logger;

import javolution.text.TextBuilder;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.managers.GrandBossManager;
import com.l2jfrozen.gameserver.managers.RaidBossSpawnManager;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.templates.StatsSet;

public class GrandBossSpawn
  implements IVoicedCommandHandler
{
  private static Logger _log = Logger.getLogger(GrandBossSpawn.class.getName());
  private static final String[] _voicedCommands = { "raidinfo" };
  
  @Override
public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
  {
    if (command.startsWith("raidinfo")) {
      showMainPage(activeChar);
    }
    return true;
  }
  
  private static void showMainPage(L2PcInstance activeChar)
  {
    TextBuilder tb = new TextBuilder();
    tb.append("<html><title>L2Ryan Boss Spawn</title><body><center>");
    tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
    tb.append("Epic's Boss respawn time<br>");
    tb.append("<img src=\"L2UI.SquareGray\" width=210 height=1><br>");
    for (Iterator i$ = Config.RAID_INFO_IDS_LIST.iterator(); i$.hasNext();)
    {
      int boss = ((Integer)i$.next()).intValue();
      
      String name = "";
      L2NpcTemplate template = null;
      if ((template = NpcTable.getInstance().getTemplate(boss)) != null)
      {
        name = template.getName();
      }
      else
      {
        _log.warning("[RaidInfoHandler][sendInfo] Raid Boss with ID " + boss + " is not defined into NpcTable");
        continue;
      }
      StatsSet actual_boss_stat = null;
      GrandBossManager.getInstance().getStatsSet(boss);
      long delay = 0L;
      if (NpcTable.getInstance().getTemplate(boss).type.equals("L2RaidBoss"))
      {
        actual_boss_stat = RaidBossSpawnManager.getInstance().getStatsSet(boss);
        if (actual_boss_stat != null) {
          delay = actual_boss_stat.getLong("respawnTime");
        }
      }
      else
      {
        if (!NpcTable.getInstance().getTemplate(boss).type.equals("L2GrandBoss")) {
          continue;
        }
        actual_boss_stat = GrandBossManager.getInstance().getStatsSet(boss);
        if (actual_boss_stat != null) {
          delay = actual_boss_stat.getLong("respawn_time");
        }
      }
      if (delay <= System.currentTimeMillis())
      {
        tb.append("<font color=\"00C3FF\">" + name + "</font>: " + "<font color=\"9CC300\">Is Alive</font>" + "<br1>");
      }
      else
      {
        int hours = (int)((delay - System.currentTimeMillis()) / 1000L / 60L / 60L);
        int mins = (int)((delay - hours * 60 * 60 * 1000 - System.currentTimeMillis()) / 1000L / 60L);
        int seconts = (int)((delay - (hours * 60 * 60 * 1000 + mins * 60 * 1000) - System.currentTimeMillis()) / 1000L);
        tb.append("<font color=\"00C3FF\">" + name + "</font>" + "<font color=\"FFFFFF\">" + " " + "Respawn in :</font>" + " " + " <font color=\"32C332\">" + hours + " : " + mins + " : " + seconts + "</font><br1>");
      }
    }
    tb.append("<img src=\"L2UI.SquareGray\" width=210 height=1><br>");
    tb.append("<a action=\"bypass voiced_menu\">L2Ryan.eu</a>");
    tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
    tb.append("</center></body></html>");
    
    NpcHtmlMessage msg = new NpcHtmlMessage(5);
    msg.setHtml(tb.toString());
    
    activeChar.sendPacket(msg);
  }
  
  @Override
public String[] getVoicedCommandList()
  {
    return _voicedCommands;
  }
}
