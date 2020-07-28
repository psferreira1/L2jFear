/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.l2jfrozen.gameserver.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.util.CloseUtil;
import com.l2jfrozen.util.database.L2DatabaseFactory;

public class VoteHandler
{
	
	 protected static final Logger LOGGER = Logger.getLogger(VoteHandler.class);

	public VoteHandler()
	{
	}
protected static int getTopZoneVotes()
{
	int votes = -1;
	URL url = null;
	URLConnection con = null;
	InputStream is = null;
	InputStreamReader isr = null;
	BufferedReader in = null;
	try
	{
		url = new URL(Config.VOTES_SITE_TOPZONE_URL);
		con = url.openConnection();    
		con.addRequestProperty("User-Agent", "L2TopZone");
		is = con.getInputStream();
		isr = new InputStreamReader(is);		    
		in = new BufferedReader(isr);
		String inputLine;
		while ((inputLine = in.readLine()) != null)
		{
			if (inputLine.contains("Votes"))
			{
				String votesLine = inputLine;
				
				votes = Integer.valueOf(votesLine.split(">")[3].replace("</div", ""));
				break;
			}
		}
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	return votes;
}

public static void tzvote(final L2PcInstance player)
{
	long LastTZVote = 0L;
	long voteDelay = 43200000L;
	final int actualvotes;
	
	actualvotes = getTopZoneVotes();
	
	class tzvotetask implements Runnable
	{
		private final L2PcInstance p;
		
		public tzvotetask(L2PcInstance player)
		
		{
			p = player;
		}
		
		@Override
		public void run()
		{
			if (actualvotes < getTopZoneVotes())
			{
				p.setIsVoting(false);
				VoteHandler.updateLastTZVote(p);
				p.sendPacket(new ExShowScreenMessage("Thanks for Voting. You are rewarded with 10 Vote Stone.", 6000));
				p.sendMessage("Thanks for Voting. You are rewarded with 10 Vote Stone.");
				player.getInventory().addItem("TZreward", 7570, 10, player, null);
			}
			else
			{
				p.setIsVoting(false);
				p.sendPacket(new ExShowScreenMessage("You didn't vote. Try Again Later", 6000));
				p.sendMessage("You didn't vote. Try Again Later.");
			}
		}
		
	}
	
	PreparedStatement statement = null;
	Connection con = null;
	try
	{ 
		con = L2DatabaseFactory.getInstance().getConnection(false);
		statement = con.prepareStatement("SELECT LastTZVote FROM characters WHERE obj_Id=?");
		statement.setInt(1, player.getObjectId());
		
		ResultSet rset = statement.executeQuery();
		
		while (rset.next())
		{
			LastTZVote = rset.getLong("LastTZVote");
		}
	    rset.close();
	    rset = null;
		statement.execute();
		statement.close();
		statement = null;
	
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
	

 if ((LastTZVote + voteDelay) < System.currentTimeMillis())
	{
		for (L2PcInstance actualchar : L2World.getInstance().getAllPlayers())
		{
			if (actualchar.isVoting())
			{
				player.sendPacket(new ExShowScreenMessage(actualchar.getName()+", is voting now. Please wait for a while.", 6000));
				player.sendMessage(actualchar.getName()+", is voting now. Please wait for a while.");
				return;
			}
		}
		
		player.setIsVoting(true);
		player.sendPacket(new ExShowScreenMessage("You have 40 Secounds to vote on Topzone.", 6000));
		player.sendMessage("You have 40 Secounds to vote on Topzone.");
		ThreadPoolManager.getInstance().scheduleGeneral(new tzvotetask(player), 40 * 880);
	}
	else
	{
		player.sendPacket(new ExShowScreenMessage("You can vote only once every 12 hours.", 6000));
		player.sendMessage("You can vote only once every 12 hours.");
	}
}

public static void updateLastTZVote(L2PcInstance player)
{
	Connection con = null;
	try
	{ 
		con = L2DatabaseFactory.getInstance().getConnection(false);
		PreparedStatement statement = con.prepareStatement("UPDATE characters SET LastTZVote=? WHERE obj_Id=?");
		statement.setLong(1, System.currentTimeMillis());
		statement.setInt(2, player.getObjectId());
		statement.execute();
		statement.close();
		statement = null;
	
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
}
     protected static int getHopZoneVotes()
       {
    	   int votes = -1;
    	   
    	   try
  			{
  				final WebClient webClient = new WebClient(BrowserVersion.CHROME);
  				webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
  				webClient.getOptions().setThrowExceptionOnScriptError(false);
  				webClient.getOptions().setPrintContentOnFailingStatusCode(false);
  				webClient.setJavaScriptEngine(new JavaScriptEngine(webClient));
  				final HtmlPage page = webClient.getPage(Config.VOTES_SITE_HOPZONE_URL);
  				
  				String fullPage = page.asXml();
  				int constrainA = fullPage.indexOf("rank anonymous tooltip") + 24;
  				String voteSection = fullPage.substring(constrainA);
  				int constrainB = voteSection.indexOf("span") - 2;
  				voteSection = voteSection.substring(0, constrainB).trim();
  				votes = Integer.parseInt(voteSection);
  				
  				page.cleanUp();
  				webClient.getJavaScriptEngine().shutdown();
  				webClient.closeAllWindows();
  			}
  			catch (IOException e)
  			{
  				System.out.println("[VoteRewardManager]: Problem occured while getting Hopzone votes. Error Trace: " + e.getMessage());
 			}
   			return votes;
       }

public static void HZvote(final L2PcInstance player)
{
	long LastHZVote = 0L;
	long voteDelay = 43200000L;
	final int actualvotes;
	
	actualvotes = getHopZoneVotes();
	
	class hpvotetask implements Runnable
	{
		private final L2PcInstance p;
		
		public hpvotetask(L2PcInstance player)
		
		{
			p = player;
		}
		
		@Override
		public void run()
		{
			if (actualvotes < getHopZoneVotes())
			{
				p.setIsVoting(false);
				VoteHandler.updateLastHZVote(p);
				p.sendPacket(new ExShowScreenMessage("Thanks for Voting. You are rewarded with 10 Vote Stone.", 6000));
				p.sendMessage("Thanks for Voting. You are rewarded with 10 Vote Stone.");
				player.getInventory().addItem("TZreward", 7570, 10, player, null);
			}
			else
			{
				p.setIsVoting(false);
				p.sendPacket(new ExShowScreenMessage("You didn't vote. Try Again Later", 6000));
				p.sendMessage("You didn't vote. Try Again Later.");
			}
		}
		
	}
	
	PreparedStatement statement = null;
	Connection con = null;
	try
	{ 
		con = L2DatabaseFactory.getInstance().getConnection(false);
		statement = con.prepareStatement("SELECT LastHZVote FROM characters WHERE obj_Id=?");
		statement.setInt(1, player.getObjectId());
		
		ResultSet rset = statement.executeQuery();
		
		while (rset.next())
		{
			LastHZVote = rset.getLong("LastHZVote");
		}
	    rset.close();
	    rset = null;
		statement.execute();
		statement.close();
		statement = null;
	
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
	

 if ((LastHZVote + voteDelay) < System.currentTimeMillis())
	{
		for (L2PcInstance actualchar : L2World.getInstance().getAllPlayers())
		{
			if (actualchar.isVoting())
			{
				player.sendPacket(new ExShowScreenMessage(actualchar.getName()+", is voting now. Please wait for a while.", 6000));
				player.sendMessage(actualchar.getName()+", is voting now. Please wait for a while.");
				return;
			}
		}
		
		player.setIsVoting(true);
		player.sendPacket(new ExShowScreenMessage("You have 40 Secounds to vote on Hopzone.", 6000));
		player.sendMessage("You have 40 Secounds to vote on Hopzone.");
		ThreadPoolManager.getInstance().scheduleGeneral(new hpvotetask(player), 40 * 880);
	}
	else
	{
		player.sendPacket(new ExShowScreenMessage("You can vote only once every 12 hours.", 6000));
		player.sendMessage("You can vote only once every 12 hours");
	}
}

public static void updateLastHZVote(L2PcInstance player)
{
	Connection con = null;
	try
	{ 
		con = L2DatabaseFactory.getInstance().getConnection(false);
		PreparedStatement statement = con.prepareStatement("UPDATE characters SET LastHZVote=? WHERE obj_Id=?");
		statement.setLong(1, System.currentTimeMillis());
		statement.setInt(2, player.getObjectId());
		statement.execute();
		statement.close();
		statement = null;
	
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
}
protected static int getL2NetworkVotes()
{
    int votes = -1;
    URL url = null;
    URLConnection con = null;
    InputStream is = null;
    InputStreamReader isr = null;
    BufferedReader in = null;

    try
    {
        url = new URL(Config.VOTES_SITE_L2NETWORK_URL);
        con = url.openConnection();
        con.addRequestProperty("User-Agent", "L2Network");
        is = con.getInputStream();
        isr = new InputStreamReader(is);
        in = new BufferedReader(isr);
        String inputLine;
        while ((inputLine = in.readLine()) != null)
        {
            if (inputLine.contains("color:#e7ebf2"))
            {
                votes = Integer.valueOf(inputLine.split(">")[2].replace("</b", ""));
                break;
			}
		}
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	return votes;
}

public static void NZvote(final L2PcInstance player)
{
	long LastNZVote = 0L;
	long voteDelay = 43200000L;
	final int actualvotes;
	
	actualvotes = getL2NetworkVotes();
	
	class nzvotetask implements Runnable
	{
		private final L2PcInstance p;
		
		public nzvotetask(L2PcInstance player)
		
		{
			p = player;
		}
		
		@Override
		public void run()
		{
			if (actualvotes < getL2NetworkVotes())
			{
				p.setIsVoting(false);
				VoteHandler.updateLastNZVote(p);
				p.sendPacket(new ExShowScreenMessage("Thanks for Voting. You are rewarded with 10 Vote Stone.", 6000));
				p.sendMessage("Thanks for Voting. You are rewarded with 10 Vote Stone.");
				player.getInventory().addItem("NZreward", 7570, 10, player, null);
			}
			else
			{
				p.setIsVoting(false);
				p.sendPacket(new ExShowScreenMessage("You didn't vote. Try Again Later", 6000));
				p.sendMessage("You didn't vote. Try Again Later.");
			}
		}
		
	}
	
	PreparedStatement statement = null;
	Connection con = null;
	try
	{ 
		con = L2DatabaseFactory.getInstance().getConnection(false);
		statement = con.prepareStatement("SELECT LastNZVote FROM characters WHERE obj_Id=?");
		statement.setInt(1, player.getObjectId());
		
		ResultSet rset = statement.executeQuery();
		
		while (rset.next())
		{
			LastNZVote = rset.getLong("LastNZVote");
		}
	    rset.close();
	    rset = null;
		statement.execute();
		statement.close();
		statement = null;
	
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
	

 if ((LastNZVote + voteDelay) < System.currentTimeMillis())
	{
		for (L2PcInstance actualchar : L2World.getInstance().getAllPlayers())
		{
			if (actualchar.isVoting())
			{
				player.sendPacket(new ExShowScreenMessage(actualchar.getName()+", is voting now. Please wait for a while.", 6000));
				player.sendMessage(actualchar.getName()+", is voting now. Please wait for a while.");
				return;
			}
		}
		
		player.setIsVoting(true);
		player.setIsVoting(true);
		player.sendPacket(new ExShowScreenMessage("You have 40 Secounds to vote on Network.", 6000));
		player.sendMessage("You have 40 Secounds to vote on Network.");
		ThreadPoolManager.getInstance().scheduleGeneral(new nzvotetask(player), 40 * 880);
	}
	else
	{
		player.sendPacket(new ExShowScreenMessage("You can vote only once every 12 hours.", 6000));
		player.sendMessage("You can vote only once every 12 hours.");
	}
}

public static void updateLastNZVote(L2PcInstance player)
{
	Connection con = null;
	try
	{ 
		con = L2DatabaseFactory.getInstance().getConnection(false);
		PreparedStatement statement = con.prepareStatement("UPDATE characters SET LastNZVote=? WHERE obj_Id=?");
		statement.setLong(1, System.currentTimeMillis());
		statement.setInt(2, player.getObjectId());
		statement.execute();
		statement.close();
		statement = null;
	
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
}
}