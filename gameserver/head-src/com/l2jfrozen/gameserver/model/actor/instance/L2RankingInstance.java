package com.l2jfrozen.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javolution.text.TextBuilder;

import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.util.CloseUtil;
import com.l2jfrozen.util.database.L2DatabaseFactory;

public class L2RankingInstance  extends L2FolkInstance
{
  public L2RankingInstance(int objectId, L2NpcTemplate template)
  {
    super(objectId, template);
  }
  
  @Override
public void onAction(L2PcInstance player)
  {
    if (!canTarget(player)) {
      return;
    }
    player.setLastFolkNPC(this);
    if (this != player.getTarget())
    {
      player.setTarget(this);
      

      MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
      player.sendPacket(my);
      my = null;
      

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
    player.sendPacket(ActionFailed.STATIC_PACKET);
  }
  
  @Override
public void onBypassFeedback(L2PcInstance player, String command)
  {
    if (command.equals("toppvp"))
    {
      NpcHtmlMessage htm = new NpcHtmlMessage(getObjectId());
      TextBuilder tb = new TextBuilder("<html><head><title>Ranking PvP</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Pvp's</center></td><td><center>Status</center></td></tr>");
      Connection con = null;
      try
      {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement statement = con.prepareStatement("SELECT char_name,pvpkills,online FROM characters WHERE pvpkills>0 AND accesslevel=0 order by pvpkills desc limit 15");
        ResultSet rs = statement.executeQuery();
        int pos = 0;
        while (rs.next())
        {
          String pvps = rs.getString("pvpkills");
          String name = rs.getString("char_name");
          pos++;
          String statu = rs.getString("online");
          String status;
          if (statu.equals("1")) {
            status = "<font color=00FF00>Online</font>";
          } else {
            status = "<font color=FF0000>Offline</font>";
          }
          tb.append("<tr><td><center><font color =\"AAAAAA\">" + pos + "</td><td><center><font color=00FFFF>" + name + "</font></center></td><td><center>" + pvps + "</center></td><td><center>" + status + "</center></td></tr>");
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      finally
      {
        CloseUtil.close(con);
        con = null;
      }
      tb.append("</body></html>");
      
      htm.setHtml(tb.toString());
      player.sendPacket(htm);
    }
    if (command.equals("toppk"))
    {
      NpcHtmlMessage htm = new NpcHtmlMessage(getObjectId());
      TextBuilder tb = new TextBuilder("<html><head><title>Ranking PK</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Pk's</center></td><td><center>Status</center></td></tr>");
      Connection con = null;
      try
      {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement statement = con.prepareStatement("SELECT char_name,pkkills,online FROM characters WHERE pvpkills>0 AND accesslevel=0 order by pkkills desc limit 15");
        ResultSet rs = statement.executeQuery();
        int pos = 0;
        while (rs.next())
        {
          String pks = rs.getString("pkkills");
          String name = rs.getString("char_name");
          pos++;
          String statu = rs.getString("online");
          String status;
          if (statu.equals("1")) {
            status = "<font color=00FF00>Online</font>";
          } else {
            status = "<font color=FF0000>Offline</font>";
          }
          tb.append("<tr><td><center><font color =\"AAAAAA\">" + pos + "</td><td><center><font color=00FFFF>" + name + "</font></center></td><td><center>" + pks + "</center></td><td><center>" + status + "</center></td></tr>");
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      finally
      {
        CloseUtil.close(con);
        con = null;
      }
      tb.append("</body></html>");
      
      htm.setHtml(tb.toString());
      player.sendPacket(htm);
    }
    if (command.equals("Clan"))
    {
      NpcHtmlMessage htm = new NpcHtmlMessage(getObjectId());
      TextBuilder tb = new TextBuilder("<html><head><title>Clan Ranking</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Level</center></td><td><center>Clan Name</center></td><td><center>Reputation</center></td></tr>");
      Connection con = null;
      try
      {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement statement = con.prepareStatement("SELECT clan_name,clan_level,reputation_score FROM clan_data WHERE clan_level>0 order by reputation_score desc limit 15");
        ResultSet rs = statement.executeQuery();
        int pos = 0;
        while (rs.next())
        {
          String clan_name = rs.getString("clan_name");
          String clan_level = rs.getString("clan_level");
          String clan_score = rs.getString("reputation_score");
          pos++;
          
          tb.append("<tr><td><center><font color =\"AAAAAA\">" + pos + "</center></td><td><center>" + clan_level + "</center></td><td><center><font color=00FFFF>" + clan_name + "</font></center></td><td><center><font color=00FF00>" + clan_score + "</font></center></td></tr>");
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      finally
      {
        CloseUtil.close(con);
        con = null;
      }
      tb.append("</body></html>");
      
      htm.setHtml(tb.toString());
      player.sendPacket(htm);
    }
    if (command.equals("Online"))
    {
      NpcHtmlMessage htm = new NpcHtmlMessage(getObjectId());
      TextBuilder tb = new TextBuilder("<html><head><title>Top Online Ranking</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Time Online</center></td></tr>");
      Connection con = null;
      try
      {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement statement = con.prepareStatement("SELECT char_name,onlinetime FROM characters WHERE onlinetime>0 and accesslevel=0 order by onlinetime desc limit 1000000");
        ResultSet rs = statement.executeQuery();
        int pos = 0;
        while (rs.next())
        {
          String char_name = rs.getString("char_name");
          String char_onlinetime = rs.getString("onlinetime");
          pos++;
          
          tb.append("<tr><td><center><font color =\"AAAAAA\">" + pos + "</center></td><td><center><font color=00FFFF>" + char_name + "</font></center></td><td><center><font color=00FF00>" + char_onlinetime + "</font></center></td></tr>");
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      finally
      {
        CloseUtil.close(con);
        con = null;
      }
      tb.append("</body></html>");
      
      htm.setHtml(tb.toString());
      player.sendPacket(htm);
    }
  }
  
  public void showHtmlWindow(L2PcInstance player)
  {
    String filename = "data/html/mods/ranking/" + getNpcId() + ".htm";
    
    filename = getHtmlPath(getNpcId(), 0);
    NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
    html.setFile(filename);
    html.replace("%objectId%", String.valueOf(getObjectId()));
    html.replace("%npcname%", getName());
    player.sendPacket(html);
  }
  
  @Override
public String getHtmlPath(int npcId, int val)
  {
    String pom = "";
    if (val == 0) {
      pom = "" + npcId;
    } else {
      pom = npcId + "-" + val;
    }
    return "data/html/mods/ranking/" + pom + ".htm";
  }
}
