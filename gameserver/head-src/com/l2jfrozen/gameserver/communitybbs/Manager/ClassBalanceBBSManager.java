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
package com.l2jfrozen.gameserver.communitybbs.Manager;

import java.util.Map;
import java.util.StringTokenizer;

import com.l2jfrozen.gameserver.custom.ClassBalanceManager;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.base.PlayerClass;

public class ClassBalanceBBSManager extends BaseBBSManager
{
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		if (!activeChar.isGM())
			return;
		String html = "<html><body><center><br><br>";
		html += "Whole online: " + (L2World.getAllPlayersCount() - (activeChar.isGM() ? 0 : L2World.getInstance().getAllGMs().size())) + "";
		html += "<br>";
		if (command.startsWith("_bbsloc"))
		{
			int pageId = 1;
			if (command.length() > 8)
			{
				pageId = Integer.parseInt(command.substring(8));
			}
			html += showMainHtml(activeChar, pageId);
		}
		else if (command.startsWith("_bbs_remove_classbalance"))
		{
			String info[] = command.substring(25).split(" ");
			int objectId = Integer.parseInt(info[0]);
			int pageId = 1;
			if (info.length > 1)
			{
				pageId = Integer.parseInt(info[1]);
			}
			ClassBalanceManager.getInstance().removeClassBalance(objectId);
			html += showMainHtml(activeChar, pageId);
		}
		else if (command.startsWith("_bbs_modify_classbalance"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			int objectId = Integer.parseInt(st.nextToken().substring(25));
			String className = st.nextToken();
			if (className.startsWith(" "))
			{
				className = className.substring(1);
			}
			if (className.endsWith(" "))
			{
				className = className.substring(0, className.length() - 1);
			}
			String attackTypeSt = st.nextToken().replaceAll(" ", "");
			String attackType = "0";
			if (attackTypeSt.equals("Magic"))
				attackType = "1";
			if (attackTypeSt.equals("Crit"))
				attackType = "2";
			if (attackTypeSt.equals("MCrit"))
				attackType = "3";
			if (attackTypeSt.equals("Blow"))
				attackType = "4";
			if (attackTypeSt.equals("accCombat"))
				attackType = "5";

			String val = st.nextToken().replaceAll(" ", "");
			String targetClassName = st.nextToken();
			if (targetClassName.startsWith(" "))
			{
				targetClassName = targetClassName.substring(1);
			}
			if (targetClassName.endsWith(" "))
			{
				targetClassName = targetClassName.substring(0, targetClassName.length() - 1);
			}
			int pageId = 0;
			if (st.hasMoreTokens())
			{
				pageId = Integer.parseInt(st.nextToken());
			}
			String classId = "-1";
			if (!className.equals(""))
				for (PlayerClass cId : PlayerClass.values())
					if (cId.name().equalsIgnoreCase(className))
						classId = cId.ordinal() + "";
						
			String targetClassId = "-1";
			if (!targetClassName.equals(""))
				for (PlayerClass cId : PlayerClass.values())
					if (cId.name().equalsIgnoreCase(targetClassName))
						targetClassId = cId.ordinal() + "";
			
			for (Map.Entry<String, Double> balance : ClassBalanceManager.getInstance().getAllBalances().entrySet())
			{
				int id = Integer.parseInt(balance.getKey().split("/")[3]);
				if (objectId != id)
				{
					continue;
				}
				if (classId.equals("-1"))
				{
					classId = balance.getKey().split("/")[0];
				}
				if (attackType.equals(""))
				{
					attackType = balance.getKey().split("/")[2];
				}
				if (targetClassId.equals("-1"))
				{
					targetClassId = balance.getKey().split("/")[1];
				}
				if (val.equals(""))
				{
					val = balance.getValue() + "";
				}
			}
			
			String key = classId + "/" + targetClassId + "/" + attackType + "/" + objectId;
			double value = Double.parseDouble(val);
			ClassBalanceManager.getInstance().modifyClassBalance(objectId, key, value);
			html += showMainHtml(activeChar, pageId);
		}
		else if (command.startsWith("_bbs_add_classbalance_menu"))
		{
			StringTokenizer st = new StringTokenizer(command.substring(27), " ");
			int pageId = Integer.parseInt(st.nextToken());
			int race = Integer.parseInt(st.nextToken());
			int tRace = Integer.parseInt(st.nextToken());
			html += showAddHtml(activeChar, pageId, race, tRace);
		}
		else if (command.startsWith("_bbs_add_classbalance"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			String className = st.nextToken();
			if (className.startsWith(" "))
			{
				className = className.substring(1);
			}
			if (className.endsWith(" "))
			{
				className = className.substring(0, className.length() - 1);
			}
			className = className.replaceAll(" ", "");
			String attackTypeSt = st.nextToken();
			String attackType = "0";
			if (attackTypeSt.equals("Magic"))
				attackType = "1";
			if (attackTypeSt.equals("Crit"))
				attackType = "2";
			if (attackTypeSt.equals("MCrit"))
				attackType = "3";
			if (attackTypeSt.equals("Blow"))
				attackType = "4";
			if (attackType.equals(""))
			{
				className += " " + attackTypeSt;
				attackTypeSt = st.nextToken();
				attackType = "0";
				if (attackTypeSt.equals("Magic"))
					attackType = "1";
				if (attackTypeSt.equals("Crit"))
					attackType = "2";
				if (attackTypeSt.equals("MCrit"))
					attackType = "3";
				if (attackTypeSt.equals("Blow"))
					attackType = "4";
			}
			
			String val = st.nextToken();
			String targetClassName = st.nextToken();
			if (targetClassName.startsWith(" "))
			{
				targetClassName = targetClassName.substring(1);
			}
			if (targetClassName.endsWith(" "))
			{
				targetClassName = targetClassName.substring(0, targetClassName.length() - 1);
			}
			targetClassName = targetClassName.replaceAll(" ", "");
			int objectId = 0;
			for (Map.Entry<String, Double> balance : ClassBalanceManager.getInstance().getAllBalances().entrySet())
			{
				int id = Integer.parseInt(balance.getKey().split("/")[3]);
				if (id >= objectId)
				{
					objectId = id + 1;
				}
			}
			String classId = "-1";
			if (!className.equals(""))
			{
				for (PlayerClass cId : PlayerClass.values())
				{
					if (cId.name().equalsIgnoreCase(className))
					{
						classId = cId.ordinal() + "";
					}
				}
			}
			String targetClassId = "-1";
			if (!targetClassName.equals(""))
			{
				for (PlayerClass cId : PlayerClass.values())
				{
					if (cId.name().equalsIgnoreCase(targetClassName))
					{
						targetClassId = cId.ordinal() + "";
					}
				}
			}
			String key = classId + "/" + targetClassId + "/" + attackType + "/" + objectId;
			double value = Double.parseDouble(val);
			ClassBalanceManager.getInstance().addClassBalance(objectId, key, value);
			html += showMainHtml(activeChar, 1);
		}
		html += "</center></body></html>";
		separateAndSend(html, activeChar);
	}
	
	public String showMainHtml(L2PcInstance activeChar, int pageId)
	{
		String content = "<br>";
		content += "<table width=\"640\">";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=20></td></tr>";
		content += "<tr><td width=\"40\"></td><td>Class Id</td><td>Attack Type</td><td></td><td align=center>Value</td><td></td><td>Target Class Id</td><td width=\"40\"></td></tr>";
		int count = 1, limitInPage = 7;
		for (Map.Entry<String, Double> balance : ClassBalanceManager.getInstance().getAllBalances().entrySet())
		{
			if ((count > ((limitInPage * pageId) - limitInPage)) && (count <= (limitInPage * pageId)))
			{
				int classId = Integer.parseInt(balance.getKey().split("/")[0]);
				int editedType = Integer.parseInt(balance.getKey().split("/")[2]);
				int targetClassId = Integer.parseInt(balance.getKey().split("/")[1]);
				int id = Integer.parseInt(balance.getKey().split("/")[3]);
				
				if (PlayerClass.getClassById(classId).name().equals("") || PlayerClass.getClassById(targetClassId).name().equals(""))
					continue;
				
				String type = "Normal";
				if (editedType == 1)
					type = "Magic";
				if (editedType == 2)
					type = "Crit";
				if (editedType == 3)
					type = "MCrit";
				if (editedType == 4)
					type = "Blow";
				String className = PlayerClass.getClassById(classId).name();
				double percents = (balance.getValue() * 100) - 100;
				String targetClassName = PlayerClass.getClassById(targetClassId).name();
				double addedValue = (double) Math.round((balance.getValue() + 0.1) * 10) / 10;
				double removedValue = (double) Math.round((balance.getValue() - 0.1) * 10) / 10;
				content += "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=20></td></tr>";
				content += "<tr><td></td>";
				content += "<td>" + classId + " (" + className + ")</td>";
				content += "<td align=center>" + type + "</td>";
				content += "<td><button action=\"bypass _bbs_modify_classbalance " + id + "; ; ; " + (removedValue) + "; ;1\" width=22 height=22 back=\"L2UI_CH3.Minimap.mapbutton_zoomout2\" fore=\"L2UI_CH3.Minimap.mapbutton_zoomout1\"></td>";
				content += "<td fixwidth=80 align=center>" + balance.getValue() + " (" + (percents > 0 ? "+" : "") + String.valueOf(percents).substring(0, String.valueOf(percents).indexOf(".")) + "%)</td>";
				content += "<td><button action=\"bypass _bbs_modify_classbalance " + id + "; ; ; " + (addedValue) + "; ;1\" width=22 height=22 back=\"L2UI_CH3.Minimap.mapbutton_zoomin2\" fore=\"L2UI_CH3.Minimap.mapbutton_zoomin1\"></td>";
				content += "<td>" + targetClassId + " (" + targetClassName + ")</td>";
				content += "<td width=\"75\" align=\"center\">";
				content += "<button value=\"Remove\" action=\"bypass _bbs_remove_classbalance " + id + " " + pageId + "\" width=65 height=21 back=\"L2UI_CH3.smallbutton2_down\" fore=\"L2UI_CH3.smallbutton2\">";
				content += "</td>";
				content += "<td width=\"40\"></td></tr>";
			}
			count++;
		}
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=20></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td width=\"45\" align=\"center\">";
		content += "<button value=\"Add\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " 0 0\" width=65 height=21 back=\"L2UI_CH3.smallbutton2_down\" fore=\"L2UI_CH3.smallbutton2\">";
		content += "</td><td width=\"40\"></td></tr><tr><td><img src=\"L2UI.SquareBlank\" width=40 height=20></td></tr></table>";
		content += "<table width=170><tr>";
		if ((pageId - 1) != 0)
		{
			content += "<td align=right width=190><button value=\"Prev\" action=\"bypass _bbsloc " + (pageId - 1) + "\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></td>";
		}
		else
		{
			content += "<td align=right width=190><button value=\"Prev\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\"></td>";
		}
		if ((pageId * limitInPage) >= (count - 1))
		{
			content += "<td width=190><button value=\"Next\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\"></td>";
		}
		else
		{
			content += "<td width=190><button value=\"Next\" action=\"bypass _bbsloc " + (pageId + 1) + "\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></td>";
		}
		content += "</tr></table>";
		return content;
	}
	
	public String showAddHtml(L2PcInstance activeChar, int pageId, int race, int tRace)
	{
		String classes = "";
		for (PlayerClass _classId : PlayerClass.values()) {
			if (_classId.getRace() == null) continue;
			if ((_classId.getLevel().ordinal() == 3) && (_classId.getRace().ordinal() == race))
				classes += _classId.name() + ";";
		}
		
		String tClasses = "";
		for (PlayerClass _classId : PlayerClass.values()) {
			if (_classId.getRace() == null) continue;
			if ((_classId.getLevel().ordinal() == 3) && (_classId.getRace().ordinal() == tRace))
				tClasses += _classId.name() + ";";
		}

		String content = "<br>";
		content += "<table width=\"640\">";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=10></td></tr>";
		content += "<tr><td></td><td align=\"center\">Class Id</td><td align=\"center\">Attack Type</td><td align=\"center\">Value</td><td align=\"center\">Target Class Id</td><td width=\"40\"></td></tr>";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=10></td></tr>";
		content += "<tr><td><table>";
		content += "<tr><td>";
		content += "<button value=\"\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " 0 " + tRace + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 0 ? "_checked" : "") + "\">";
		content += "</td><td>Human</td></tr>";
		content += "<tr><td>";
		content += "<button value=\"\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " 1 " + tRace + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 1 ? "_checked" : "") + "\">";
		content += "</td><td>Elf</td></tr>";
		content += "<tr><td>";
		content += "<button value=\"\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " 2 " + tRace + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 2 ? "_checked" : "") + "\">";
		content += "</td><td>Dark Elf</td></tr>";
		content += "<tr><td>";
		content += "<button value=\"\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " 3 " + tRace + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 3 ? "_checked" : "") + "\">";
		content += "</td><td>Orc</td></tr>";
		content += "<tr><td>";
		content += "<button value=\"\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " 4 " + tRace + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 4 ? "_checked" : "") + "\">";
		content += "</td><td>Dwarf</td></tr>";
		content += "</table></td>";
		content += "<td align=\"center\"><combobox var=\"classId\" list=\"" + classes + "\" width=110></td>";
		content += "<td align=\"center\"><combobox var=\"attackType\" list=\"Normal;Magic;Crit;MCrit;Blow\" width=70></td>";
		content += "<td align=\"center\"><Edit var=\"val\" width=30 height=12></td>";
		content += "<td align=\"center\"><combobox var=\"tClassId\" list=\"" + tClasses + "\" width=110></td>";
		content += "<td><table>";
		content += "<tr><td>";
		content += "<button value=\"\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " " + race + " 0\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (tRace == 0 ? "_checked" : "") + "\">";
		content += "</td><td>Human</td></tr>";
		content += "<tr><td>";
		content += "<button value=\"\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " " + race + " 1\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (tRace == 1 ? "_checked" : "") + "\">";
		content += "</td><td>Elf</td></tr>";
		content += "<tr><td>";
		content += "<button value=\"\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " " + race + " 2\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (tRace == 2 ? "_checked" : "") + "\">";
		content += "</td><td>Dark Elf</td></tr>";
		content += "<tr><td>";
		content += "<button value=\"\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " " + race + " 3\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (tRace == 3 ? "_checked" : "") + "\">";
		content += "</td><td>Orc</td></tr>";
		content += "<tr><td>";
		content += "<button value=\"\" action=\"bypass _bbs_add_classbalance_menu " + pageId + " " + race + " 4\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (tRace == 4 ? "_checked" : "") + "\">";
		content += "</td><td>Dwarf</td></tr>";
		content += "</table></td>";
		content += "</tr>";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=20></td></tr><tr><td></td><td></td><td></td><td align=\"right\">";
		content += "<button value=\"Add\" action=\"bypass _bbs_add_classbalance $classId $attackType $val $tClassId \" width=65 height=21 back=\"L2UI_CH3.smallbutton2_down\" fore=\"L2UI_CH3.smallbutton2\">";
		content += "</td><td></td><td></td></tr>";
		content += "<tr><td></td><td align=\"right\">";
		content += "<button value=\"Back\" action=\"bypass _bbsclan " + pageId + "\" width=65 height=21 back=\"L2UI_CH3.smallbutton2_down\" fore=\"L2UI_CH3.smallbutton2\">";
		content += "</td><td></td><td></td><td></td><td></td></tr>";
		content += "</table>";
		return content;
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
	}
	
	private static ClassBalanceBBSManager _instance = new ClassBalanceBBSManager();
	
	public static ClassBalanceBBSManager getInstance()
	{
		return _instance;
	}
	
}