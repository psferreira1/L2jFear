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
package com.l2jfrozen.gameserver.model.actor.instance;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.sql.ItemTable;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.templates.L2Item;
import com.l2jfrozen.util.database.L2DatabaseFactory;


/**
 * @author by gevorakoC
 * 
 *
 */
public class PvpRewardSystem {
	
	private static Logger _log = Logger.getLogger(PvpRewardSystem.class.getName());
	
	/** 
	 * Executed when kill player (from victim side)
	 * @param killer
	 * @param victim
	 */
	public static void doCustomPvpReward(L2PcInstance killer, L2PcInstance victim)
	{
		//int error_code = 0;
		int kills = 0;
		int kills_today = 0;
		long kill_time = 0;
		
		long sys_time = System.currentTimeMillis();
		long prot_time = (1000 * 60 * Config.CUSTOM_PVP_REWARD_PROTECTION_RESET);
		
		Connection con = null;
		try{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement statement = con.prepareStatement("CALL CPRS_add(?,?,?,?)"); //query returns updated killer data
			statement.setInt(1, killer.getObjectId());
			statement.setInt(2, victim.getObjectId());
			statement.setLong(3, sys_time);
			statement.setLong(4, prot_time);
			
			ResultSet rset = statement.executeQuery();
			
			while(rset.next()){
				//error_code = rset.getInt("error_code");
				kills = rset.getInt("kills");
				kills_today = rset.getInt("kills_today");
				kill_time = rset.getLong("kill_time");
				break;
			}
			rset.close();
			statement.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			L2DatabaseFactory.close(con);
		}
		
		
		if(kills_today <= Config.CUSTOM_PVP_REWARD_PROTECTION){
			if(killer !=null){
				addItemRewardForKiller(killer, victim);
			}
		}else{
			if((Config.CUSTOM_PVP_PK_REWARD)&&(killer.getKarma() == 0)){
				if(Config.CUSTOM_PVP_REWARD_PROTECTION > 0){
					if((Config.CUSTOM_PVP_REWARD_ENABLED)){
						if(Config.CUSTOM_PVP_REWARD_PROTECTION_RESET == 0){
							killer.sendMessage("Reward has been awarded for kill this player!");
						}else{
							killer.sendMessage("Reward has been awarded for kill this player today!");
							killer.sendMessage("Next for "+calculateTimeToString(sys_time, kill_time));
						}
					}
				}
			}
		}

		
		
		if(kills > 1){
			String timeStr1 = "times";
			if(kills_today == 1){ timeStr1 = "time"; }
			String msgVictim1 = killer.getName() + " killed you " + kills + " times.";
			String msgVictim2 = killer.getName() + " killed you " + kills + " times ( "+ kills_today +" "+timeStr1+" today ).";
			String msgKiller1 = "You have killed " + victim.getName() + " " + kills + " times.";
			String msgKiller2 = "You have killed " + killer.getName() + " " + kills + " times ( "+ kills_today +" "+timeStr1+" today ).";
			
			if(Config.CUSTOM_PVP_REWARD_PROTECTION_RESET == 0){
				victim.sendMessage(msgVictim1);
				killer.sendMessage(msgKiller1);
			}else{
				victim.sendMessage(msgVictim2);
				killer.sendMessage(msgKiller2);
			}
		}else{
			victim.sendMessage("This is the first time you have been killed by " + killer.getName() + ".");
			killer.sendMessage("You have killed " + victim.getName() + " for the first time."); 
		}

		killer = null;
		victim = null;
		
	}
	
	/**
	 * Adds the item reward.
	 *
	 * @param killer Player who killed.
	 * @param victim victim Player.
	 */
	public static void addItemRewardForKiller(L2PcInstance killer, L2PcInstance victim){	
		//Anti FARM Reward, level player >= reward_min_lvl
		if((Config.CUSTOM_PVP_REWARD_MIN_LVL > victim.getLevel())||(Config.CUSTOM_PVP_REWARD_MIN_LVL > killer.getLevel()))
		{
			killer.sendMessage("Rewards are awarded on "+ Config.CUSTOM_PVP_REWARD_MIN_LVL + "+ level.");
		}else{
	        //IP check
			if(killer.getClient()!=null)
			if(killer.getClient().getConnection().getInetAddress() != victim.getClient().getConnection().getInetAddress())
			{
	
				if((killer.getKarma() == 0)&&(killer.getPvpFlag() > 0)||((Config.CUSTOM_PVP_PK_REWARD)&&(killer.getKarma() == 0)&&(victim.getKarma() > 0))){
					if(Config.CUSTOM_PVP_REWARD_ENABLED)
					{
						L2Item reward = ItemTable.getInstance().getTemplate(Config.CUSTOM_PVP_REWARD_ID);
						
						//killer.getInventory().addItem("Winning PvP", Config.CUSTOM_PVP_REWARD_ID, Config.PVP_REWARD_A-beep-T, killer, null);
						killer.addItem("PvP Reward", Config.CUSTOM_PVP_REWARD_ID, Config.PVP_REWARD, killer, false);
						killer.sendMessage("You have earned " + Config.PVP_REWARD + " x " + reward.getName() + ".");
					}
				}
			}else{
				 victim.sendMessage("Farm is punishable with Ban! Don't kill your Box!");
		        _log.warning("PVP POINT FARM ATTEMPT: " + victim.getName() + " AND " + killer.getName() +" HAVE SAME IP!");
			}
		}
	}
	
	private static String calculateTimeToString(long sys_time, long kill_time){
		long TimeToRewardInMilli = ((kill_time + (1000 * 60 * Config.CUSTOM_PVP_REWARD_PROTECTION_RESET)) - sys_time);
        long TimeToRewardHours = TimeToRewardInMilli / (60 * 60 * 1000);
        long TimeToRewardMinutes = (TimeToRewardInMilli % (60 * 60 * 1000)) / (60 *1000);
        long TimeToRewardSeconds = (TimeToRewardInMilli % (60 * 1000)) / (1000);
        
        String H = Long.toString(TimeToRewardHours);
        String M = Long.toString(TimeToRewardMinutes);
        String S = Long.toString(TimeToRewardSeconds);
        if(TimeToRewardMinutes <= 9){ M = "0" + M; }
        if(TimeToRewardSeconds <= 9){ S = "0" + S; }
        
        return H+":"+M+":"+S;
	}
	
}