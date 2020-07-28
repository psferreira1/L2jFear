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
package Extensions.balancer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.l2jfrozen.util.database.L2DatabaseFactory;

/**
 * @author Elfocrash
 */
public class BalanceLoad
{
	public static int[] Evasion = new int[31], Accuracy = new int[31], Speed = new int[31], PAtk = new int[31],
			MAtk = new int[31], PDef = new int[31], MDef = new int[31], HP = new int[31], CP = new int[31],
			MP = new int[31], MAtkSpd = new int[31], PAtkSpd = new int[31], Critical = new int[31],
			MagicCritical = new int[31], WalkSpeed = new int[31], MAtkRange = new int[31], MReuseRate = new int[31], PReuseRate = new int[31],
			INT = new int[31], WIT = new int[31], MEN=new int[31], CON = new int[31], STR = new int[31], DEX= new int[31];

	public static void LoadEm()
	{
		int z;

		for (z = 0; z < 31; z++)
		{
			Evasion[z] = loadEvasion(88 + z);
			Speed[z] = loadSpeed(z + 88);
			MAtk[z] = loadMAtk(z + 88);
			PAtk[z] = loadPAtk(z + 88);
			PDef[z] = loadPDef(z + 88);
			MDef[z] = loadMDef(z + 88);
			HP[z] = loadHP(z + 88);
			CP[z] = loadCP(z + 88);
			MP[z] = loadMP(z + 88);
			MAtkSpd[z] = loadMAtkSpd(z + 88);
			PAtkSpd[z] = loadPAtkSpd(z + 88);
			Critical[z] = loadCritical(z + 88);
			MagicCritical[z] = loadMagicCritical(z + 88);
			WalkSpeed[z] = loadWalkSpeed(z + 88);
			MAtkRange[z] = loadMAtkRange(z + 88);
			MReuseRate[z] = loadMReuseRate(z + 88);
			PReuseRate[z] = loadPReuseRate(z + 88);
			INT[z] = loadINT(z + 88);
			WIT[z] = loadWIT(z + 88);
			MEN[z] = loadMEN(z + 88);
			CON[z] = loadCON(z + 88);
			STR[z] = loadSTR(z + 88);
			DEX[z] = loadDEX(z + 88);
		}

	}

	public static int loadEvasion(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT ev FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("ev");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}
		return i;
	}

	public static int loadAccuracy(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT acc FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("acc");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}
		return i;
	}

	public static int loadSpeed(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT walk FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("walk");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}
		return i;
	}

	public static int loadPAtk(int classId)
	{
		int i = 0;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT patk FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("patk");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}
		return i;
	}

	public static int loadMAtk(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT matk FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("matk");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}
		return i;
	}

	public static int loadPDef(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT pdef FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("pdef");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}

	public static int loadMDef(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT mdef FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("mdef");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}

	public static int loadHP(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT hp FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("hp");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}

	public static int loadCP(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT cp FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("cp");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}

	public static int loadMP(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT mp FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("mp");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}

	public static int loadMAtkSpd(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT matksp FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("matksp");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}

	public static int loadPAtkSpd(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT patksp FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("patksp");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadCritical(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT critical FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("critical");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadMagicCritical(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT magiccritical FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("magiccritical");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadWalkSpeed(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT walkspeed FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("walkspeed");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadMAtkRange(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT matkrange FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("matkrange");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadMReuseRate(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT mreuserate FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("mreuserate");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadPReuseRate(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT preuserate FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("preuserate");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadINT(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT INT_ FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("INT_");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadMEN(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT MEN FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("MEN");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadWIT(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT WIT FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("WIT");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	
	public static int loadCON(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT CON FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("CON");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadSTR(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT STR FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("STR");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
	
	public static int loadDEX(int classId)
	{
		int i = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT DEX FROM balance WHERE class_id=" + classId);
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				i = rset.getInt("DEX");
			}

			stm.close();
		}
		catch (Exception e)
		{
			System.err.println("Error while loading balance stats from database.");
			e.printStackTrace();
		}

		return i;
	}
}