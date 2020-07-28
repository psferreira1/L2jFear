package com.l2jfrozen.gameserver.model.actor.instance;

import javolution.text.TextBuilder;

import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jfrozen.gameserver.network.serverpackets.CreatureSay;
import com.l2jfrozen.gameserver.network.serverpackets.MagicSkillUser;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.SetupGauge;
import com.l2jfrozen.gameserver.network.serverpackets.SocialAction;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.gameserver.util.Broadcast;
import com.l2jfrozen.util.random.Rnd;


public class L2CasinoInstance extends L2NpcInstance
{
    //public String filename;

    public L2CasinoInstance(int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(L2PcInstance player, String command)
    {
      int ammount = 0;
      if (command.startsWith("play1"))
      {
        StringTokenizer st = new StringTokenizer(command);
        try
        {
          st.nextToken();
          ammount = Integer.parseInt(st.nextToken());
        }
        catch (NoSuchElementException nse)
        {
        }
        if (ammount >0)
        {
            Casino1(player, ammount);
        }
        else return;
      }
    }

    public static void displayCongrats(L2PcInstance player)
    {
        player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
        MagicSkillUser  MSU = new MagicSkillUser(player, player, 2024, 1, 1, 0);
        player.broadcastPacket(MSU);
        ExShowScreenMessage screen = new ExShowScreenMessage("Congratulations "+player.getName()+"! You won!", 6000);
        String name = player.getName();
        player.sendPacket(new CreatureSay(player.getObjectId(), 0, name, "Congratulations You won!"));
        player.sendPacket(screen);
    }

    public static void displayCongrats2(L2PcInstance player)
    {
        ExShowScreenMessage screen = new ExShowScreenMessage(""+player.getName()+"! You lost!", 6000);
        String name = player.getName();
        player.sendPacket(new CreatureSay(player.getObjectId(), 0, name, "You lost!"));
        player.sendPacket(screen);
    }

    @Override
    public void showChatWindow(L2PcInstance player, int val)
    {
        //filename = (getHtmlPath(getNpcId(), val));
        NpcHtmlMessage msg = new NpcHtmlMessage(this.getObjectId());
        msg.setHtml(casinoWindow(player));
        msg.replace("%objectId%", String.valueOf(this.getObjectId()));
        player.sendPacket(msg);
    }

    private String casinoWindow(L2PcInstance player)
    {
  	    StringBuilder tb = new StringBuilder();
  	    tb.append("<html><body><center>");
  	    tb.append("<img src=\"l2ceriel.1\"width=300 height=100>");
  	    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
  	    tb.append("<table bgcolor=000000 width=319>");
  	    tb.append("<tr>");
  	    tb.append("<td><center><font color=\"ae9988\">Hey, Here you can Bet your Vote Stone</font><center></td>");
  	    tb.append("</tr>");
  	    tb.append("</table>");
  	    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
  	    tb.append("<br>");
  	    tb.append("<br>");
  	    tb.append("<table bgcolor=000000 width=200>");
  	    tb.append("<tr>");
  	    tb.append("<td><center><font color=\"FFFFFF\"></font>Pleace your best, Only Vote Stone,</font></center></td>");
  	    tb.append("</tr>");
  	    tb.append("</table>");
  	    tb.append("<tr>"); 
  	    tb.append("<br>");
  		tb.append("<tr>");
  		tb.append("<br>");
  	    tb.append("<br>");
  		tb.append("<td align=center><edit var=\"qbox\" width=120 height=15><br></td> <td align=right></td>");
  		tb.append("<td align=center><button value=\"Bet\" action=\"bypass -h npc_%objectId%_play1 $qbox\" width=132 height=21 back=\"l2ceriel.bat\" fore=\"l2ceriel.bat\"></center>");
        tb.append("</tr>");
  	    tb.append("<br>"); 
  	    tb.append("<br>"); 
  	    tb.append("<br>"); 
  	    tb.append("<br>"); 
  		tb.append("<br>");
  		tb.append("<br>");
  	    tb.append("<br>");
  	    tb.append("<br>"); 
  	    tb.append("<br>"); 
  		tb.append("<br>");
  	    tb.append("<br>"); 
          tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
  	    tb.append("<table bgcolor=000000 width=319>");	
  	    tb.append("<tr>");
  	    tb.append("<td><center><font color=\"ae9977\">L2Ryan.eu</font></center></td>");
  	    tb.append("</tr>");
  	    tb.append("</table>");
          tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
          tb.append("</body></html>");
        return tb.toString();
    }

    public static void Casino1(L2PcInstance player,int ammount)
    {
       
    	int unstuckTimer = (1*1000 );
        player.setTarget(player);
        player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        player.disableAllSkills();
        MagicSkillUser msk = new MagicSkillUser(player, 361, 1, unstuckTimer, 0);
        Broadcast.toSelfAndKnownPlayersInRadius(player, msk, 810000);
        SetupGauge sg = new SetupGauge(0, unstuckTimer);
        player.sendPacket(sg);

        Casino1 ef = new Casino1(player,ammount);
        player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(ef, unstuckTimer));
        //player.setSkillCastEndTime(10+GameTimeController.getGameTicks()+unstuckTimer/GameTimeController.MILLIS_IN_TICK);
    }

    public static class Casino1 implements Runnable
    {
        private L2PcInstance _player;
		private int _ammount;
        Casino1(L2PcInstance player,int ammount)
        {	
			_ammount= ammount;
            _player = player;
        }
        
        @Override
		public void run()
        {
            if(_player.isDead())
            {
                return;
            }

            _player.setIsIn7sDungeon(false);
            _player.enableAllSkills();
            int chance = Rnd.get(3);

            if( _player.getInventory().getInventoryItemCount(7570, 0) >= _ammount)
            {
                if(chance == 0)
                {
                    displayCongrats(_player);
                    //_player.getInventory().addItem("Adena", 7570, _ammount, _player, null);
					_player.addItem("Gift",7570,_ammount,_player,true);
					_player.broadcastUserInfo();
					
                }

                if(chance == 1)
                {
                    displayCongrats2(_player);
                    //_player.getInventory().destroyItemByItemId("Adena", 7570, _ammount, _player, null);
					_player.destroyItemByItemId("Consume", 7570, _ammount, _player, true);
					_player.broadcastUserInfo();
                }
                
                if(chance == 2)
                {
                    displayCongrats2(_player);
                    //_player.getInventory().destroyItemByItemId("Adena", 7570, _ammount, _player, null);
					_player.destroyItemByItemId("Consume", 7570, _ammount, _player, true);
					_player.broadcastUserInfo();
                }
            }
            else
            {
                _player.sendMessage("You do not have enough Vote Reward Item.");
            }
        }
    }
}
