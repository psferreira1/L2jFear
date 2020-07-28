package com.l2jfrozen.gameserver.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.model.base.ClassId;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.util.database.L2DatabaseFactory;

public class BalanceManager
{
  public static final Logger _log = Logger.getLogger(BalanceManager.class.getName());
  HashMap<Integer, double[]> _balances;
  HashMap<Integer, double[]> _olympiadBalances;
  HashMap<Integer, Integer> _updates;
  HashMap<Integer, Integer> _olympiadUpdates;
  HashMap<Integer, Integer> _secondProffessions;
  HashMap<Boolean, HashMap<Integer, ArrayList<Integer>>> _dataForIngameBalancer;
  ScheduledFuture<?> _updateThread;
  
  public BalanceManager()
  {
    this._balances = new HashMap();
    this._olympiadBalances = new HashMap();
    this._updates = new HashMap();
    this._olympiadUpdates = new HashMap();
    this._dataForIngameBalancer = new HashMap();
    this._dataForIngameBalancer.put(Boolean.valueOf(true), new HashMap());
    this._dataForIngameBalancer.put(Boolean.valueOf(false), new HashMap());
    loadBalances();
    loadSecondProffessions();
    this._updateThread = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
    {
      public void run()
      {
        BalanceManager.this.updateBalances(_balances);
      }
    }, Config.CLASS_BALANCER_UPDATE_DELAY, Config.CLASS_BALANCER_UPDATE_DELAY);
  }
  
  public void loadBalances()
  {
    this._balances.clear();
    this._olympiadBalances.clear();
    ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(true))).clear();
    ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(false))).clear();
    this._updates.clear();
    this._olympiadUpdates.clear();
    try
    {
      Connection con = L2DatabaseFactory.getInstance().getConnection();Throwable localThrowable4 = null;
      try
      {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM class_balance");Throwable localThrowable5 = null;
        try
        {
          ResultSet rset = statement.executeQuery();Throwable localThrowable6 = null;
          try
          {
            while (rset.next())
            {
              int key = rset.getInt("key");
              int classId = rset.getInt("classId");
              int targetClassId = rset.getInt("targetClassId");
              boolean forOlympiad = rset.getInt("forOlympiad") == 1;
              double[] values = { rset.getDouble("normal"), rset.getDouble("normalCrit"), rset.getDouble("magic"), rset.getDouble("magicCrit"), rset.getDouble("blow"), rset.getDouble("physSkill"), rset.getDouble("physSkillCrit"), classId, targetClassId };
              if (!forOlympiad) {
                this._balances.put(Integer.valueOf(key), values);
              } else {
                this._olympiadBalances.put(Integer.valueOf(key), values);
              }
              if (!((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(classId))) {
                ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(classId), new ArrayList());
              }
              if (targetClassId >= 0)
              {
                if (!((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(targetClassId))) {
                  ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(targetClassId), new ArrayList());
                }
                if (!((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(targetClassId))).contains(Integer.valueOf(key))) {
                  ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(targetClassId))).add(Integer.valueOf(key));
                }
              }
              if (!((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(classId))).contains(Integer.valueOf(key))) {
                ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(classId))).add(Integer.valueOf(key));
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
      _log.log(Level.SEVERE, "Failed loading class balances.", e);
    }
    _log.log(Level.INFO, "Successfully loaded " + (this._balances.size() + this._olympiadBalances.size()) + " balances.");
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
    if (!Config.CLASS_BALANCER_AFFECTS_SECOND_PROFFESION) {
      return cId;
    }
    if (this._secondProffessions.containsKey(Integer.valueOf(cId))) {
      return ((Integer)this._secondProffessions.get(Integer.valueOf(cId))).intValue();
    }
    return cId;
  }
  
  public double getBalance(int classId, int targetClassId, int type, boolean forOlympiad)
  {
    classId = getClassId(classId);
    targetClassId = getClassId(targetClassId);
    if (!forOlympiad)
    {
      if (this._balances.containsKey(Integer.valueOf(classId * 256 + targetClassId))) {
        return ((double[])this._balances.get(Integer.valueOf(classId * 256 + targetClassId)))[type];
      }
    }
    else if (this._olympiadBalances.containsKey(Integer.valueOf(classId * 256 + targetClassId))) {
      return ((double[])this._olympiadBalances.get(Integer.valueOf(classId * 256 + targetClassId)))[type];
    }
    return 1.0D;
  }
  
  public double[] getBalance(int key, boolean forOlympiad)
  {
    if (!forOlympiad)
    {
      if (this._balances.containsKey(Integer.valueOf(key))) {
        return (double[])this._balances.get(Integer.valueOf(key));
      }
    }
    else if (this._olympiadBalances.containsKey(Integer.valueOf(key))) {
      return (double[])this._olympiadBalances.get(Integer.valueOf(key));
    }
    return null;
  }
  
  public double getBalanceToAll(int classId, int type, boolean forOlympiad)
  {
    classId = getClassId(classId);
    if (!forOlympiad)
    {
      if (this._balances.containsKey(Integer.valueOf(classId * -256))) {
        return ((double[])this._balances.get(Integer.valueOf(classId * -256)))[type];
      }
    }
    else if (this._olympiadBalances.containsKey(Integer.valueOf(classId * -256))) {
      return ((double[])this._olympiadBalances.get(Integer.valueOf(classId * -256)))[type];
    }
    return 1.0D;
  }
  
  public HashMap<Integer, double[]> getAllBalances(boolean forOlympiad)
  {
    return forOlympiad ? this._olympiadBalances : this._balances;
  }
  
  public HashMap<Integer, ArrayList<Integer>> getAllBalancesForIngame(boolean forOlympiad)
  {
    return (HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad));
  }
  
  public ArrayList<Integer> getBalanceForIngame(int classId, boolean forOlympiad)
  {
    classId = getClassId(classId);
    if (((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(classId))) {
      return (ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(classId));
    }
    return null;
  }
  
  public void updateBalance(int key, int classId, int targetClassId, int type, double value, boolean forOlympiad)
  {
    HashMap<Integer, double[]> balances = forOlympiad ? this._olympiadBalances : this._balances;
    if (!balances.containsKey(Integer.valueOf(key)))
    {
      double[] data = { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, classId < 0 ? -classId : classId, targetClassId };
      










      data[type] = value;
      balances.put(Integer.valueOf(key), data);
      if (!((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(classId))) {
        ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(classId), new ArrayList());
      }
      if (targetClassId >= 0)
      {
        if (!((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(targetClassId))) {
          ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(targetClassId), new ArrayList());
        }
        if (!((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(targetClassId))).contains(Integer.valueOf(key))) {
          ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(targetClassId))).add(Integer.valueOf(key));
        }
      }
      if (!((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(classId))).contains(Integer.valueOf(key))) {
        ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(classId))).add(Integer.valueOf(key));
      }
    }
    else
    {
      ((double[])balances.get(Integer.valueOf(key)))[type] = value;
    }
    if (forOlympiad) {
      this._olympiadUpdates.put(Integer.valueOf(key), Integer.valueOf(0));
    } else {
      this._updates.put(Integer.valueOf(key), Integer.valueOf(0));
    }
  }
  
  public void updateBalance(int key, int classId, int targetClassId, double[] values, boolean forOlympiad)
  {
    HashMap<Integer, double[]> balances = forOlympiad ? this._olympiadBalances : this._balances;
    balances.put(Integer.valueOf(key), values);
    if (!((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(classId))) {
      ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(classId), new ArrayList());
    }
    if (targetClassId >= 0)
    {
      if (!((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(targetClassId))) {
        ((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).put(Integer.valueOf(targetClassId), new ArrayList());
      }
      if (!((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(targetClassId))).contains(Integer.valueOf(key))) {
        ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(targetClassId))).add(Integer.valueOf(key));
      }
    }
    if (!((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(classId))).contains(Integer.valueOf(key))) {
      ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(classId))).add(Integer.valueOf(key));
    }
    if (forOlympiad) {
      this._olympiadUpdates.put(Integer.valueOf(key), Integer.valueOf(0));
    } else {
      this._updates.put(Integer.valueOf(key), Integer.valueOf(0));
    }
  }
  
  public void removeBalance(int key, int classId, int targetClassId, boolean forOlympiad)
  {
    int rClassId = classId < 0 ? -classId : classId;
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
    if ((((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(rClassId))) && (((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(rClassId))).contains(Integer.valueOf(key))))
    {
      int i = ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(rClassId))).indexOf(Integer.valueOf(key));
      ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(rClassId))).remove(i);
    }
    if ((((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).containsKey(Integer.valueOf(targetClassId))) && (((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(targetClassId))).contains(Integer.valueOf(key))))
    {
      int i = ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(targetClassId))).indexOf(Integer.valueOf(key));
      ((ArrayList)((HashMap)this._dataForIngameBalancer.get(Boolean.valueOf(forOlympiad))).get(Integer.valueOf(targetClassId))).remove(i);
    }
  }
  
  public void updateBalances(Object localThrowable9)
  {
    _log.info("Class balances updating to database!");
    for (Entry<Integer, Integer> entry : this._updates.entrySet()) {
      if (((Integer)entry.getValue()).intValue() == 0) {
        try
        {
          Connection con = L2DatabaseFactory.getInstance().getConnection();localThrowable9 = null;
          try
          {
            PreparedStatement statement = con.prepareStatement("REPLACE INTO class_balance (class_balance.key, forOlympiad, normal, normalCrit, magic, magicCrit, blow, physSkill, physSkillCrit, classId, targetClassId) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");localThrowable9 = null;
            try
            {
              double[] data = (double[])this._balances.get(entry.getKey());
              statement.setInt(1, ((Integer)entry.getKey()).intValue());
              statement.setInt(2, 0);
              statement.setDouble(3, data[0]);
              statement.setDouble(4, data[1]);
              statement.setDouble(5, data[2]);
              statement.setDouble(6, data[3]);
              statement.setDouble(7, data[4]);
              statement.setDouble(8, data[5]);
              statement.setDouble(9, data[6]);
              statement.setInt(10, (int)data[7]);
              statement.setInt(11, (int)data[8]);
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
          _log.log(Level.SEVERE, "Could not update class balances[" + entry.getKey() + "]: " + e.getMessage(), e);
        }
      } else {
        try
        {
          Connection con = L2DatabaseFactory.getInstance().getConnection();localThrowable9 = null;
          try
          {
            PreparedStatement statement = con.prepareStatement("DELETE FROM class_balance WHERE class_balance.key=?");localThrowable9 = null;
            try
            {
              statement.setInt(1, ((Integer)entry.getKey()).intValue());
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
          _log.log(Level.SEVERE, "Could not delete class balances[" + entry.getKey() + "]: " + e.getMessage(), e);
        }
      }
    }
    Throwable localThrowable91;
    Throwable localThrowable10;
    for (Map.Entry<Integer, Integer> entry : this._olympiadUpdates.entrySet()) {
      if (((Integer)entry.getValue()).intValue() == 0) {
        try
        {
          Connection con = L2DatabaseFactory.getInstance().getConnection();localThrowable91 = null;
          try
          {
            PreparedStatement statement = con.prepareStatement("REPLACE INTO class_balance (class_balance.key, forOlympiad, normal, normalCrit, magic, magicCrit, blow, physSkill, physSkillCrit, classId, targetClassId) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");localThrowable10 = null;
            try
            {
              double[] data = (double[])this._olympiadBalances.get(entry.getKey());
              statement.setInt(1, ((Integer)entry.getKey()).intValue());
              statement.setInt(2, 1);
              statement.setDouble(3, data[0]);
              statement.setDouble(4, data[1]);
              statement.setDouble(5, data[2]);
              statement.setDouble(6, data[3]);
              statement.setDouble(7, data[4]);
              statement.setDouble(8, data[5]);
              statement.setDouble(9, data[6]);
              statement.setInt(10, (int)data[7]);
              statement.setInt(11, (int)data[8]);
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
            localThrowable91 = localThrowable6;throw localThrowable6;
          }
          finally
          {
            if (con != null) {
              if (localThrowable91 != null) {
                try
                {
                  con.close();
                }
                catch (Throwable x2)
                {
                  localThrowable91.addSuppressed(x2);
                }
              } else {
                con.close();
              }
            }
          }
        }
        catch (Exception e)
        {
          _log.log(Level.SEVERE, "Could not update class balances[" + entry.getKey() + "]: " + e.getMessage(), e);
        }
      } else {
        try
        {
          Connection con = L2DatabaseFactory.getInstance().getConnection();localThrowable91 = null;
          try
          {
            PreparedStatement statement = con.prepareStatement("DELETE FROM class_balance WHERE class_balance.key=?");localThrowable10 = null;
            try
            {
              statement.setInt(1, ((Integer)entry.getKey()).intValue());
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
            localThrowable91 = localThrowable8;throw localThrowable8;
          }
          finally
          {
            if (con != null) {
              if (localThrowable91 != null) {
                try
                {
                  con.close();
                }
                catch (Throwable x2)
                {
                  localThrowable91.addSuppressed(x2);
                }
              } else {
                con.close();
              }
            }
          }
        }
        catch (Exception e)
        {
          _log.log(Level.SEVERE, "Could not delete class balances[" + entry.getKey() + "]: " + e.getMessage(), e);
        }
      }
    }
    this._updates.clear();
    this._olympiadUpdates.clear();
  }
  
  public static BalanceManager getInstance()
  {
    return SingletonHolder._instance;
  }
  
  private static class SingletonHolder
  {
    protected static final BalanceManager _instance = new BalanceManager();
  }
}
