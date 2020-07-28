
/*
 *  Author: Qwerty, Scoria dev.
 *  v 1.0
 */
package com.l2jfrozen.gameserver.model.entity.siege.clanhalls;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;

import com.l2jfrozen.gameserver.datatables.csv.DoorTable;
import com.l2jfrozen.gameserver.datatables.sql.ClanTable;
import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.idfactory.IdFactory;
import com.l2jfrozen.gameserver.managers.ClanHallManager;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Clan;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2DecoInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.ClanHall;
import com.l2jfrozen.gameserver.model.entity.siege.ClanHallSiege;
import com.l2jfrozen.gameserver.model.zone.type.L2ClanHallZone;
import com.l2jfrozen.gameserver.network.serverpackets.CreatureSay;
import com.l2jfrozen.gameserver.taskmanager.ExclusiveTask;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;

public class WildBeastFarmSiege extends ClanHallSiege
{
	protected static Logger LOGGER = Logger.getLogger(WildBeastFarmSiege.class);
	private static WildBeastFarmSiege instance;
	private boolean registrationPeriod = false;
	private int clanCounter = 0;
	protected Map<Integer, ClanPlayersInfo> clansInfo = new HashMap<>();
	public ClanHall clanhall = ClanHallManager.getInstance().getClanHallById(63);
	protected ClanPlayersInfo ownerClanInfo = new ClanPlayersInfo();
	protected boolean finalStage = false;
	protected ScheduledFuture<?> midTimer;
	private L2ClanHallZone zone;
	
	public static final WildBeastFarmSiege getInstance()
	{
		if (instance == null)
		{
			instance = new WildBeastFarmSiege();
		}
		return instance;
	}
	
	private WildBeastFarmSiege()
	{
		LOGGER.info("SiegeManager of Wild Beasts Farm");
		final long siegeDate = restoreSiegeDate(63);
		final Calendar tmpDate = Calendar.getInstance();
		tmpDate.setTimeInMillis(siegeDate);
		setSiegeDate(tmpDate);
		setNewSiegeDate(siegeDate, 63, 22);
		// Schedule siege auto start
		startSiegeTask.schedule(1000);
	}
	
	public void startSiege()
	{
		setRegistrationPeriod(false);
		if (clansInfo.size() == 0)
		{
			endSiege(false);
			return;
		}
		if (clansInfo.size() == 1 && clanhall.getOwnerClan() == null)
		{
			endSiege(false);
			return;
		}
		if (clansInfo.size() == 1 && clanhall.getOwnerClan() != null)
		{
			L2Clan clan = null;
			for (final ClanPlayersInfo a : clansInfo.values())
			{
				clan = ClanTable.getInstance().getClanByName(a.clanName);
			}
			setIsInProgress(true);
			startSecondStep(clan);
			anonce("Take place at the siege of his headquarters.", 1);
			siegeEndDate = Calendar.getInstance();
			siegeEndDate.add(Calendar.MINUTE, 30);
			endSiegeTask.schedule(1000);
			return;
		}
		setIsInProgress(true);
		spawnFlags();
		gateControl(1);
		anonce("Take place at the siege of his headquarters.", 1);
		ThreadPoolManager.getInstance().scheduleGeneral(new startFirstStep(), 5 * 60000);
		midTimer = ThreadPoolManager.getInstance().scheduleGeneral(new midSiegeStep(), 25 * 60000);
		siegeEndDate = Calendar.getInstance();
		siegeEndDate.add(Calendar.MINUTE, 60);
		endSiegeTask.schedule(1000);
	}
	
	public void startSecondStep(final L2Clan winner)
	{
		List<String> winPlayers = WildBeastFarmSiege.getInstance().getRegisteredPlayers(winner);
		unSpawnAll();
		clansInfo.clear();
		final ClanPlayersInfo regPlayers = new ClanPlayersInfo();
		regPlayers.clanName = winner.getName();
		regPlayers.players = winPlayers;
		clansInfo.put(winner.getClanId(), regPlayers);
		clansInfo.put(clanhall.getOwnerClan().getClanId(), ownerClanInfo);
		spawnFlags();
		gateControl(1);
		finalStage = true;
		anonce("Take place at the siege of his headquarters.", 1);
		ThreadPoolManager.getInstance().scheduleGeneral(new startFirstStep(), 5 * 60000);
	}
	
	public void endSiege(final boolean par)
	{
		mobControlTask.cancel();
		finalStage = false;
		if (par)
		{
			final L2Clan winner = checkHaveWinner();
			if (winner != null)
			{
				ClanHallManager.getInstance().setOwner(clanhall.getId(), winner);
				anonce("Attention! Clan hall, farm beasts was conquered by the clan " + winner.getName(), 2);
			}
			else
			{
				anonce("Attention! Clan hall, farm wild animals did not get new owner", 2);
			}
		}
		setIsInProgress(false);
		unSpawnAll();
		clansInfo.clear();
		clanCounter = 0;
		teleportPlayers();
		setNewSiegeDate(getSiegeDate().getTimeInMillis(), 63, 22);
		startSiegeTask.schedule(1000);
	}
	
	public void unSpawnAll()
	{
		for (final String clanName : getRegisteredClans())
		{
			final L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
			final L2MonsterInstance mob = getQuestMob(clan);
			final L2DecoInstance flag = getSiegeFlag(clan);
			if (mob != null)
			{
				mob.deleteMe();
			}
			if (flag != null)
			{
				flag.deleteMe();
			}
		}
	}
	
	public void gateControl(final int val)
	{
		if (val == 1)
		{
			DoorTable.getInstance().getDoor(21150003).openMe();
			DoorTable.getInstance().getDoor(21150004).openMe();
			DoorTable.getInstance().getDoor(21150001).closeMe();
			DoorTable.getInstance().getDoor(21150002).closeMe();
		}
		else if (val == 2)
		{
			DoorTable.getInstance().getDoor(21150001).closeMe();
			DoorTable.getInstance().getDoor(21150002).closeMe();
			DoorTable.getInstance().getDoor(21150003).closeMe();
			DoorTable.getInstance().getDoor(21150004).closeMe();
		}
	}
	
	public void teleportPlayers()
	{
		zone = clanhall.getZone();
		for (final L2Character cha : zone.getCharactersInside().values())
		{
			if (cha instanceof L2PcInstance)
			{
				final L2Clan clan = ((L2PcInstance) cha).getClan();
				if (!isPlayerRegister(clan, cha.getName()))
				{
					cha.teleToLocation(53468, -94092, -1634);
				}
			}
		}
	}
	
	public L2Clan checkHaveWinner()
	{
		L2Clan res = null;
		int questMobCount = 0;
		for (final String clanName : getRegisteredClans())
		{
			final L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
			if (getQuestMob(clan) != null)
			{
				res = clan;
				questMobCount++;
			}
		}
		if (questMobCount > 1)
		{
			return null;
		}
		return res;
	}
	
	protected class midSiegeStep implements Runnable
	{
		@Override
		public void run()
		{
			mobControlTask.cancel();
			final L2Clan winner = checkHaveWinner();
			if (winner != null)
			{
				if (clanhall.getOwnerClan() == null)
				{
					ClanHallManager.getInstance().setOwner(clanhall.getId(), winner);
					anonce("Attention! Hall clan Fkrma wild animals was conquered by the clan " + winner.getName(), 2);
					endSiege(false);
				}
				else
				{
					startSecondStep(winner);
				}
			}
			else
			{
				endSiege(true);
			}
		}
	}
	
	protected class startFirstStep implements Runnable
	{
		@Override
		public void run()
		{
			teleportPlayers();
			gateControl(2);
			int mobCounter = 1;
			for (final String clanName : getRegisteredClans())
			{
				L2NpcTemplate template;
				final L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
				template = NpcTable.getInstance().getTemplate(35617 + mobCounter);
				/*
				 * template.setServerSideTitle(true); template.setTitle(clan.getName());
				 */
				final L2MonsterInstance questMob = new L2MonsterInstance(IdFactory.getInstance().getNextId(), template);
				questMob.setHeading(100);
				questMob.getStatus().setCurrentHpMp(questMob.getMaxHp(), questMob.getMaxMp());
				if (mobCounter == 1)
				{
					questMob.spawnMe(57069, -91797, -1360);
				}
				else if (mobCounter == 2)
				{
					questMob.spawnMe(58838, -92232, -1354);
				}
				else if (mobCounter == 3)
				{
					questMob.spawnMe(57327, -93373, -1365);
				}
				else if (mobCounter == 4)
				{
					questMob.spawnMe(57820, -91740, -1354);
				}
				else if (mobCounter == 5)
				{
					questMob.spawnMe(58728, -93487, -1360);
				}
				final ClanPlayersInfo regPlayers = clansInfo.get(clan.getClanId());
				regPlayers.mob = questMob;
				mobCounter++;
			}
			mobControlTask.schedule(3000);
			anonce("The battle began. Kill the enemy NPC", 1);
		}
	}
	
	public void spawnFlags()
	{
		int flagCounter = 1;
		for (final String clanName : getRegisteredClans())
		{
			L2NpcTemplate template;
			final L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
			if (clan == clanhall.getOwnerClan())
			{
				template = NpcTable.getInstance().getTemplate(35422);
			}
			else
			{
				template = NpcTable.getInstance().getTemplate(35422 + flagCounter);
			}
			final L2DecoInstance flag = new L2DecoInstance(IdFactory.getInstance().getNextId(), template);
			flag.setTitle(clan.getName());
			flag.setHeading(100);
			flag.getStatus().setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
			if (clan == clanhall.getOwnerClan())
			{
				flag.spawnMe(58782, -93180, -1354);
				final ClanPlayersInfo regPlayers = clansInfo.get(clan.getClanId());
				regPlayers.flag = flag;
				continue;
			}
			
			if (flagCounter == 1)
			{
				flag.spawnMe(56769, -92097, -1360);
			}
			else if (flagCounter == 2)
			{
				flag.spawnMe(59138, -92532, -1354);
			}
			else if (flagCounter == 3)
			{
				flag.spawnMe(57027, -93673, -1365);
			}
			else if (flagCounter == 4)
			{
				flag.spawnMe(58120, -91440, -1354);
			}
			else if (flagCounter == 5)
			{
				flag.spawnMe(58428, -93787, -1360);
			}
			final ClanPlayersInfo regPlayers = clansInfo.get(clan.getClanId());
			regPlayers.flag = flag;
			flagCounter++;
		}
	}
	
	public void setRegistrationPeriod(final boolean par)
	{
		registrationPeriod = par;
	}
	
	public boolean isRegistrationPeriod()
	{
		return registrationPeriod;
	}
	
	public boolean isPlayerRegister(final L2Clan playerClan, final String playerName)
	{
		if (playerClan == null)
		{
			return false;
		}
		final ClanPlayersInfo regPlayers = clansInfo.get(playerClan.getClanId());
		if (regPlayers != null)
		{
			if (regPlayers.players.contains(playerName))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isClanOnSiege(final L2Clan playerClan)
	{
		if (playerClan == clanhall.getOwnerClan())
		{
			return true;
		}
		final ClanPlayersInfo regPlayers = clansInfo.get(playerClan.getClanId());
		if (regPlayers == null)
		{
			return false;
		}
		return true;
	}
	
	public synchronized int registerClanOnSiege(final L2PcInstance player, final L2Clan playerClan)
	{
		if (clanCounter == 5)
		{
			return 2;
		}
		final L2ItemInstance item = player.getInventory().getItemByItemId(8293);
		if (item != null && player.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false))
		{
			clanCounter++;
			ClanPlayersInfo regPlayers = clansInfo.get(playerClan.getClanId());
			if (regPlayers == null)
			{
				regPlayers = new ClanPlayersInfo();
				regPlayers.clanName = playerClan.getName();
				clansInfo.put(playerClan.getClanId(), regPlayers);
			}
		}
		else
		{
			return 1;
		}
		return 0;
	}
	
	public boolean unRegisterClan(final L2Clan playerClan)
	{
		if (clansInfo.remove(playerClan.getClanId()) != null)
		{
			clanCounter--;
			return true;
		}
		return false;
	}
	
	public List<String> getRegisteredClans()
	{
		List<String> clans = new ArrayList<>();
		
		for (final ClanPlayersInfo a : clansInfo.values())
		{
			clans.add(a.clanName);
		}
		
		return clans;
	}
	
	public List<String> getRegisteredPlayers(final L2Clan playerClan)
	{
		if (playerClan == clanhall.getOwnerClan())
		{
			return ownerClanInfo.players;
		}
		final ClanPlayersInfo regPlayers = clansInfo.get(playerClan.getClanId());
		if (regPlayers != null)
		{
			return regPlayers.players;
		}
		return null;
	}
	
	public L2DecoInstance getSiegeFlag(final L2Clan playerClan)
	{
		final ClanPlayersInfo clanInfo = clansInfo.get(playerClan.getClanId());
		if (clanInfo != null)
		{
			return clanInfo.flag;
		}
		return null;
	}
	
	public L2MonsterInstance getQuestMob(final L2Clan clan)
	{
		final ClanPlayersInfo clanInfo = clansInfo.get(clan.getClanId());
		if (clanInfo != null)
		{
			return clanInfo.mob;
		}
		return null;
	}
	
	public int getPlayersCount(final String playerClan)
	{
		for (final ClanPlayersInfo a : clansInfo.values())
		{
			if (a.clanName.equalsIgnoreCase(playerClan))
			{
				return a.players.size();
			}
		}
		return 0;
	}
	
	public void addPlayer(final L2Clan playerClan, final String playerName)
	{
		if (playerClan == clanhall.getOwnerClan())
		{
			if (ownerClanInfo.players.size() < 18)
			{
				if (!ownerClanInfo.players.contains(playerName))
				{
					ownerClanInfo.players.add(playerName);
					return;
				}
			}
		}
		final ClanPlayersInfo regPlayers = clansInfo.get(playerClan.getClanId());
		if (regPlayers != null)
		{
			if (regPlayers.players.size() < 18)
			{
				if (!regPlayers.players.contains(playerName))
				{
					regPlayers.players.add(playerName);
				}
			}
		}
	}
	
	public void removePlayer(final L2Clan playerClan, final String playerName)
	{
		if (playerClan == clanhall.getOwnerClan())
		{
			if (ownerClanInfo.players.contains(playerName))
			{
				ownerClanInfo.players.remove(playerName);
				return;
			}
		}
		final ClanPlayersInfo regPlayers = clansInfo.get(playerClan.getClanId());
		if (regPlayers != null)
		{
			if (regPlayers.players.contains(playerName))
			{
				regPlayers.players.remove(playerName);
			}
		}
	}
	
	private final ExclusiveTask startSiegeTask = new ExclusiveTask()
	{
		@Override
		protected void onElapsed()
		{
			if (getIsInProgress())
			{
				cancel();
				return;
			}
			final Calendar siegeStart = Calendar.getInstance();
			siegeStart.setTimeInMillis(getSiegeDate().getTimeInMillis());
			final long registerTimeRemaining = siegeStart.getTimeInMillis() - System.currentTimeMillis();
			siegeStart.add(Calendar.HOUR, 1);
			final long siegeTimeRemaining = siegeStart.getTimeInMillis() - System.currentTimeMillis();
			long remaining = registerTimeRemaining;
			if (registerTimeRemaining <= 0)
			{
				if (!isRegistrationPeriod())
				{
					if (clanhall.getOwnerClan() != null)
					{
						ownerClanInfo.clanName = clanhall.getOwnerClan().getName();
					}
					else
					{
						ownerClanInfo.clanName = "";
					}
					setRegistrationPeriod(true);
					anonce("Attention! The period of registration at the siege clan hall, farm wild animals.", 2);
					remaining = siegeTimeRemaining;
				}
			}
			if (siegeTimeRemaining <= 0)
			{
				startSiege();
				cancel();
				return;
			}
			schedule(remaining);
		}
	};
	
	public void anonce(final String text, final int type)
	{
		if (type == 1)
		{
			final CreatureSay cs = new CreatureSay(0, 1, "Journal", text);
			for (final String clanName : getRegisteredClans())
			{
				final L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
				for (final String playerName : getRegisteredPlayers(clan))
				{
					final L2PcInstance cha = L2World.getInstance().getPlayer(playerName);
					if (cha != null)
					{
						cha.sendPacket(cs);
					}
				}
			}
		}
		else
		{
			final CreatureSay cs = new CreatureSay(0, 1, "Journal", text);
			/* L2MapRegion region = MapRegionManager.getInstance().getRegion(53508, -93776, -1584); */
			for (final L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if /*
					 * (region == MapRegionManager.getInstance().getRegion(player.getX(), player.getY(), player.getZ()) &&
					 */ (player.getInstanceId() == 0)/* ) */
				{
					player.sendPacket(cs);
				}
			}
		}
	}
	
	protected final ExclusiveTask endSiegeTask = new ExclusiveTask()
	{
		@Override
		protected void onElapsed()
		{
			if (!getIsInProgress())
			{
				cancel();
				return;
			}
			final long timeRemaining = siegeEndDate.getTimeInMillis() - System.currentTimeMillis();
			if (timeRemaining <= 0)
			{
				endSiege(true);
				cancel();
				return;
			}
			schedule(timeRemaining);
		}
	};
	protected final ExclusiveTask mobControlTask = new ExclusiveTask()
	{
		@Override
		protected void onElapsed()
		{
			int mobCount = 0;
			for (final ClanPlayersInfo cl : clansInfo.values())
			{
				if (cl.mob.isDead())
				{
					final L2Clan clan = ClanTable.getInstance().getClanByName(cl.clanName);
					unRegisterClan(clan);
				}
				else
				{
					mobCount++;
				}
			}
			teleportPlayers();
			if (mobCount < 2)
			{
				if (finalStage)
				{
					siegeEndDate = Calendar.getInstance();
					endSiegeTask.cancel();
					endSiegeTask.schedule(5000);
				}
				else
				{
					midTimer.cancel(false);
					ThreadPoolManager.getInstance().scheduleGeneral(new midSiegeStep(), 5000);
				}
			}
			else
			{
				schedule(3000);
			}
		}
	};
	
	protected class ClanPlayersInfo
	{
		public String clanName;
		public L2DecoInstance flag = null;
		public L2MonsterInstance mob = null;
		public List<String> players = new ArrayList<>();
	}
}
