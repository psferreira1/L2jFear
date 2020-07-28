package com.l2jfrozen.gameserver.custom;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.model.L2Skill;
import com.l2jfrozen.gameserver.model.base.ClassId;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.util.database.L2DatabaseFactory;


public class SkillsBalanceManager
{
  public static final Logger _log = Logger.getLogger(SkillsBalanceManager.class.getName());
  ScheduledFuture<?> _updateThread;
  HashMap<Integer, double[]> _olympiadBalances;
  HashMap<Integer, double[]> _balances;
  HashMap<Integer, Integer> _secondProffessions;
  HashMap<Boolean, HashMap<Integer, ArrayList<Integer>>> _dataForIngameBalancer;
  HashMap<Integer, Integer> _updates;
  HashMap<Integer, Integer> _olympiadUpdates;
  HashMap<Boolean, HashMap<Integer, String>> _usedSkillNames;
  
  public SkillsBalanceManager()
  {
    this._balances = new HashMap();
    this._olympiadBalances = new HashMap();
    this._updates = new HashMap();
    this._olympiadUpdates = new HashMap();
    this._dataForIngameBalancer = new HashMap();
    this._dataForIngameBalancer.put(Boolean.valueOf(true), new HashMap());
    this._dataForIngameBalancer.put(Boolean.valueOf(false), new HashMap());
    this._usedSkillNames = new HashMap();
    this._usedSkillNames.put(Boolean.valueOf(true), new HashMap());
    this._usedSkillNames.put(Boolean.valueOf(false), new HashMap());
    loadSecondProffessions();
    loadBalances();
    this._updateThread = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
    {
      public void run()
      {
        SkillsBalanceManager.this.updateBalances();
      }
    }, Config.SKILLS_BALANCER_UPDATE_DELAY, Config.SKILLS_BALANCER_UPDATE_DELAY);
  }
  
  public void loadBalances()
  {
    try
    {
      Connection con = L2DatabaseFactory.getInstance().getConnection();Throwable localThrowable4 = null;
      try
      {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM skills_balance");Throwable localThrowable5 = null;
        try
        {
          ResultSet rset = statement.executeQuery();Throwable localThrowable6 = null;
          try
          {
            while (rset.next())
            {
              int key = rset.getInt("key");
              int skillId = rset.getInt("skillId");
              int targetClassId = rset.getInt("targetClassId");
              boolean forOlympiad = rset.getInt("forOlympiad") == 1;
              double[] values = { rset.getDouble("chance"), rset.getDouble("power"), skillId, targetClassId };
              if (forOlympiad) {
                this._olympiadBalances.put(Integer.valueOf(key), values);
              } else {
                this._balances.put(Integer.valueOf(key), values);
              }
              if (!((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(skillId))) {
                ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(skillId), new ArrayList());
              }
              if (!((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId))).contains(Integer.valueOf(key))) {
                ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId))).add(Integer.valueOf(key));
              }
              L2Skill sk = SkillTable.getInstance().getInfo(skillId, 1);
              if (sk != null) {
                ((HashMap)this._usedSkillNames.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(skillId), sk.getName());
              }
            }
          }
          catch (Throwable localThrowable1)
          {
            localThrowable6 = localThrowable1;throw localThrowable1;
          }
          finally {}
        }
        catch (Throwable localThrowable2)
        {
          localThrowable5 = localThrowable2;throw localThrowable2;
        }
        finally {}
      }
      catch (Throwable localThrowable3)
      {
        localThrowable4 = localThrowable3;throw localThrowable3;
      }
      finally
      {
        if (con != null) {
          if (localThrowable4 != null) {
            try
            {
              con.close();
            }
            catch (Throwable x2)
            {
              localThrowable4.addSuppressed(x2);
            }
          } else {
            con.close();
          }
        }
      }
    }
    catch (Exception e)
    {
      _log.log(Level.SEVERE, "Failed loading skills balances.", e);
    }
    _log.log(Level.INFO, "Successfully loaded " + (this._balances.size() + this._olympiadBalances.size()) + " skills balances.");
  }
  
  public void loadSecondProffessions()
  {
    this._secondProffessions = new HashMap();
    for (ClassId cId : ClassId.values()) {
      if (cId.level() >= 3) {
        this._secondProffessions.put(Integer.valueOf(cId.getParent().getId()), Integer.valueOf(cId.getId()));
      }
    }
  }
  
  public int getClassId(int cId)
  {
    if (!Config.SKILLS_BALANCER_AFFECTS_SECOND_PROFFESION) {
      return cId;
    }
    if (this._secondProffessions.containsKey(Integer.valueOf(cId))) {
      return this._secondProffessions.get(Integer.valueOf(cId)).intValue();
    }
    return cId;
  }
  
  public double getBalance(int skillId, int classId, int type, boolean forOlympiad)
  {
    classId = getClassId(classId);
    if (!forOlympiad)
    {
      if (this._balances.containsKey(Integer.valueOf(skillId * (classId < 0 ? -1 : 1) + classId * 65536))) {
        return this._balances.get(Integer.valueOf(skillId * 1 + classId * 65536))[type];
      }
    }
    else if (this._olympiadBalances.containsKey(Integer.valueOf(skillId * (classId < 0 ? -1 : 1) + classId * 65536))) {
      return this._olympiadBalances.get(Integer.valueOf(skillId * 1 + classId * 65536))[type];
    }
    return 1.0D;
  }
  
  public double[] getBalance(int key, boolean forOlympiad)
  {
    if (!forOlympiad)
    {
      if (this._balances.containsKey(Integer.valueOf(key))) {
        return this._balances.get(Integer.valueOf(key));
      }
    }
    else if (this._olympiadBalances.containsKey(Integer.valueOf(key))) {
      return this._olympiadBalances.get(Integer.valueOf(key));
    }
    return null;
  }
  
  public double getBalanceToAll(int classId, int type, boolean forOlympiad)
  {
    classId = getClassId(classId);
    if (!forOlympiad)
    {
      if (this._balances.containsKey(Integer.valueOf(classId * -1))) {
        return this._balances.get(Integer.valueOf(classId * -1))[type];
      }
    }
    else if (this._olympiadBalances.containsKey(Integer.valueOf(classId * -1))) {
      return this._olympiadBalances.get(Integer.valueOf(classId * -1))[type];
    }
    return 1.0D;
  }
  
  public HashMap<Integer, double[]> getAllBalances(boolean forOlympiad)
  {
    return forOlympiad ? this._olympiadBalances : this._balances;
  }
  
  public HashMap<Integer, ArrayList<Integer>> getAllBalancesForIngame(boolean forOlympiad)
  {
    return this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad));
  }
  
  public ArrayList<Integer> getBalanceForIngame(int skillId, boolean forOlympiad)
  {
    if (((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(skillId))) {
      return (ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId));
    }
    return null;
  }
  
  public void updateBalance(int key, int skillId, int targetClassId, int type, double value, boolean forOlympiad)
  {
    HashMap<Integer, double[]> balances = forOlympiad ? this._olympiadBalances : this._balances;
    if (!balances.containsKey(Integer.valueOf(key)))
    {
      double[] data = { 1.0D, 1.0D, skillId < 0 ? -skillId : skillId, targetClassId };
      





      data[type] = value;
      balances.put(Integer.valueOf(key), data);
      if (!((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(skillId))) {
        ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(skillId), new ArrayList());
      }
      if (!((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId))).contains(Integer.valueOf(key))) {
        ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId))).add(Integer.valueOf(key));
      }
      if (!this._usedSkillNames.containsKey(Integer.valueOf(skillId)))
      {
        L2Skill sk = SkillTable.getInstance().getInfo(skillId, 1);
        if (sk != null) {
          ((HashMap)this._usedSkillNames.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(skillId), sk.getName());
        }
      }
    }
    else
    {
      balances.get(Integer.valueOf(key))[type] = value;
    }
    if (forOlympiad) {
      this._olympiadUpdates.put(Integer.valueOf(key), Integer.valueOf(0));
    } else {
      this._updates.put(Integer.valueOf(key), Integer.valueOf(0));
    }
  }
  
  public void updateBalance(int key, int skillId, int targetClassId, double[] values, boolean forOlympiad)
  {
    HashMap<Integer, double[]> balances = forOlympiad ? this._olympiadBalances : this._balances;
    balances.put(Integer.valueOf(key), values);
    if (!((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(skillId))) {
      ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(skillId), new ArrayList());
    }
    if (!((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId))).contains(Integer.valueOf(key))) {
      ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId))).add(Integer.valueOf(key));
    }
    if (!this._usedSkillNames.containsKey(Integer.valueOf(skillId)))
    {
      L2Skill sk = SkillTable.getInstance().getInfo(skillId, 1);
      if (sk != null) {
        ((HashMap)this._usedSkillNames.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(skillId), sk.getName());
      }
    }
    if (forOlympiad) {
      this._olympiadUpdates.put(Integer.valueOf(key), Integer.valueOf(0));
    } else {
      this._updates.put(Integer.valueOf(key), Integer.valueOf(0));
    }
  }
  
  public void removeBalance(int key, int skillId, int targetClassId, boolean forOlympiad)
  {
    int rSkillId = skillId < 0 ? -skillId : skillId;
    if (!forOlympiad)
    {
      if (this._balances.containsKey(Integer.valueOf(key)))
      {
        this._balances.remove(Integer.valueOf(key));
        this._updates.put(Integer.valueOf(key), Integer.valueOf(1));
      }
    }
    else if (this._olympiadBalances.containsKey(Integer.valueOf(key)))
    {
      this._olympiadBalances.remove(Integer.valueOf(key));
      this._olympiadUpdates.put(Integer.valueOf(key), Integer.valueOf(1));
    }
    if ((((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(rSkillId))) && (((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(rSkillId))).contains(Integer.valueOf(key))))
    {
      int i = ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(rSkillId))).indexOf(Integer.valueOf(key));
      ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(rSkillId))).remove(i);
    }
    if ((((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(rSkillId))) && (((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(rSkillId))).size() < 1)) {
      ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).remove(Integer.valueOf(rSkillId));
    }
    if (((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(rSkillId))) {
      this._usedSkillNames.remove(Integer.valueOf(skillId));
    }
  }
  
  @SuppressWarnings("unchecked")
public HashMap<Integer, String> getSkillNames(boolean forOlympiad)
  {
    return this._usedSkillNames.get(Boolean.valueOf(forOlympiad));
  }
  
  @SuppressWarnings("rawtypes")
public ArrayList<Integer> getSkillsByName(boolean forOlympiad, String name, int classId)
  {
    ArrayList<Integer> skills = new ArrayList<>();
    name = name.toLowerCase();
    for (Map.Entry<Integer, Integer> entry : this._olympiadUpdates.entrySet()) {
        if (entry.getValue().intValue() == 0) {
        skills.add(entry.getKey());
      }
    }
    @SuppressWarnings("unchecked")
	ArrayList<Integer> usedSkills = new ArrayList();
    for (Iterator i$ = skills.iterator(); i$.hasNext();)
    {
      int skillId = ((Integer)i$.next()).intValue();
      if (classId >= 0)
      {
        int key = skillId * (classId < 0 ? -1 : 1) + classId * 65536;
        ArrayList<Integer> keys = (ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId));
        if (keys.contains(Integer.valueOf(key))) {
          usedSkills.add(Integer.valueOf(key));
        }
      }
      else if (((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(skillId)))
      {
        usedSkills.addAll((Collection)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId)));
      }
    }
    return usedSkills;
  }
  
  public ArrayList<Integer> getUsedSkillsById(boolean forOlympiad, int skillId, int classId)
  {
    if (!((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(skillId))) {
      return null;
    }
    if (classId == -1) {
      return (ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId));
    }
    int key = skillId * (classId < 0 ? -1 : 1) + classId * 65536;
    if (((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(skillId))).contains(Integer.valueOf(key)))
    {
      ArrayList<Integer> r = new ArrayList();
      r.add(Integer.valueOf(key));
      return r;
    }
    System.out.println("key nera " + key);
    return null;
  }
  
  public void updateBalances()
  {
    _log.info("Skills balances updating to database!");
    for (Map.Entry<Integer, Integer> entry : this._updates.entrySet()) {
      Object localThrowable9;
	if (entry.getValue().intValue() == 0) {
        try
        {
          Connection con = L2DatabaseFactory.getInstance().getConnection();localThrowable9 = null;
          try
          {
            PreparedStatement statement = con.prepareStatement("REPLACE INTO skills_balance (skills_balance.key, forOlympiad, power, chance, skillId, targetClassId) values (?, ?, ?, ?, ?, ?)");localThrowable9 = null;
            try
            {
              double[] data = this._balances.get(entry.getKey());
              statement.setInt(1, entry.getKey().intValue());
              statement.setInt(2, 0);
              statement.setDouble(3, data[0]);
              statement.setDouble(4, data[1]);
              statement.setInt(5, (int)data[2]);
              statement.setInt(6, (int)data[3]);
              statement.executeUpdate();
            }
            catch (Throwable localThrowable1)
            {
              localThrowable9 = localThrowable1;throw localThrowable1;
            }
            finally {}
          }
          catch (Throwable localThrowable2)
          {
            localThrowable9 = localThrowable2;throw localThrowable2;
          }
          finally
          {
            if (con != null) {
              if (localThrowable9 != null) {
                try
                {
                  con.close();
                }
                catch (Throwable x2)
                {
                  ((Throwable) localThrowable9).addSuppressed(x2);
                }
              } else {
                con.close();
              }
            }
          }
        }
        catch (Exception e)
        {
          _log.log(Level.SEVERE, "Could not update skill balances[" + entry.getKey() + "]: " + e.getMessage(), e);
        }
      } else {
        try
        {
          Connection con = L2DatabaseFactory.getInstance().getConnection();localThrowable9 = null;
          try
          {
            PreparedStatement statement = con.prepareStatement("DELETE FROM skills_balance WHERE skills_balance.key=?");localThrowable9 = null;
            try
            {
              statement.setInt(1, entry.getKey().intValue());
              statement.executeUpdate();
            }
            catch (Throwable localThrowable3)
            {
              localThrowable9 = localThrowable3;throw localThrowable3;
            }
            finally {}
          }
          catch (Throwable localThrowable4)
          {
            localThrowable9 = localThrowable4;throw localThrowable4;
          }
          finally
          {
            if (con != null) {
              if (localThrowable9 != null) {
                try
                {
                  con.close();
                }
                catch (Throwable x2)
                {
                  ((Throwable) localThrowable9).addSuppressed(x2);
                }
              } else {
                con.close();
              }
            }
          }
        }
        catch (Exception e)
        {
          _log.log(Level.SEVERE, "Could not delete skill balances[" + entry.getKey() + "]: " + e.getMessage(), e);
        }
      }
    }
    Throwable localThrowable9;
    Throwable localThrowable10;
    for (Map.Entry<Integer, Integer> entry : this._olympiadUpdates.entrySet()) {
      if (entry.getValue().intValue() == 0) {
        try
        {
          Connection con = L2DatabaseFactory.getInstance().getConnection();localThrowable9 = null;
          try
          {
            PreparedStatement statement = con.prepareStatement("REPLACE INTO skills_balance (skills_balance.key, forOlympiad, power, chance, skillId, targetClassId) values (?, ?, ?, ?, ?, ?)");localThrowable10 = null;
            try
            {
              double[] data = this._olympiadBalances.get(entry.getKey());
              statement.setInt(1, entry.getKey().intValue());
              statement.setInt(2, 1);
              statement.setDouble(3, data[0]);
              statement.setDouble(4, data[1]);
              statement.setInt(5, (int)data[2]);
              statement.setInt(6, (int)data[3]);
              statement.executeUpdate();
            }
            catch (Throwable localThrowable5)
            {
              localThrowable10 = localThrowable5;throw localThrowable5;
            }
            finally {}
          }
          catch (Throwable localThrowable6)
          {
            localThrowable9 = localThrowable6;throw localThrowable6;
          }
          finally
          {
            if (con != null) {
              if (localThrowable9 != null) {
                try
                {
                  con.close();
                }
                catch (Throwable x2)
                {
                  localThrowable9.addSuppressed(x2);
                }
              } else {
                con.close();
              }
            }
          }
        }
        catch (Exception e)
        {
          _log.log(Level.SEVERE, "Could not update skill balances[" + entry.getKey() + "]: " + e.getMessage(), e);
        }
      } else {
        try
        {
          Connection con = L2DatabaseFactory.getInstance().getConnection();localThrowable9 = null;
          try
          {
            PreparedStatement statement = con.prepareStatement("DELETE FROM skills_balance WHERE skills_balance.key=?");localThrowable10 = null;
            try
            {
              statement.setInt(1, entry.getKey().intValue());
              statement.executeUpdate();
            }
            catch (Throwable localThrowable7)
            {
              localThrowable10 = localThrowable7;throw localThrowable7;
            }
            finally {}
          }
          catch (Throwable localThrowable8)
          {
            localThrowable9 = localThrowable8;throw localThrowable8;
          }
          finally
          {
            if (con != null) {
              if (localThrowable9 != null) {
                try
                {
                  con.close();
                }
                catch (Throwable x2)
                {
                  localThrowable9.addSuppressed(x2);
                }
              } else {
                con.close();
              }
            }
          }
        }
        catch (Exception e)
        {
          _log.log(Level.SEVERE, "Could not delete skill balances[" + entry.getKey() + "]: " + e.getMessage(), e);
        }
      }
    }
    this._updates.clear();
    this._olympiadUpdates.clear();
  }
  
  public static SkillsBalanceManager getInstance()
  {
    return SingletonHolder._instance;
  }
  
  private static class SingletonHolder
  {
    protected static final SkillsBalanceManager _instance = new SkillsBalanceManager();
  }
}
