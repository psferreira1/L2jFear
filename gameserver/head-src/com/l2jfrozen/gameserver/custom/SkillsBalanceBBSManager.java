package com.l2jfrozen.gameserver.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.l2jfrozen.gameserver.communitybbs.Manager.BaseBBSManager;
import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.model.L2Skill;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.base.ClassId;

public class SkillsBalanceBBSManager
  extends BaseBBSManager
{
  public static String SKILLS_BALANCE_BBS_CMD = "_bbsskillsbalancer";
  
  @Override
public void parsecmd(String command, L2PcInstance activeChar)
  {
    if (!activeChar.isGM()) {
      return;
    }
    if (command.equals("admin_skillsbalancer")) {
      command = SKILLS_BALANCE_BBS_CMD + ";main";
    }
    String html = "<html><body><br><br>";
    command = command.substring(command.length() > SKILLS_BALANCE_BBS_CMD.length() ? SKILLS_BALANCE_BBS_CMD.length() + 1 : SKILLS_BALANCE_BBS_CMD.length());
    StringTokenizer st = new StringTokenizer(command, ";");
    String cmd = "main";
    if (st.hasMoreTokens()) {
      cmd = st.nextToken();
    }
    boolean forOlympiad = false;
    if (st.hasMoreTokens()) {
      forOlympiad = st.nextToken().equals("1");
    }
    int classId = -1;
    if ((st.hasMoreTokens()) && (!cmd.equalsIgnoreCase("add"))) {
      classId = Integer.parseInt(st.nextToken());
    }
    if (!cmd.startsWith("add")) {
      html = html + showHeading(forOlympiad, classId);
    }
    if (cmd.equalsIgnoreCase("main"))
    {
      html = html + showMain(forOlympiad, classId);
    }
    else if (cmd.equalsIgnoreCase("search"))
    {
      html = html + showSearchResults(st, activeChar, forOlympiad, classId);
    }
    else if (cmd.equalsIgnoreCase("delete"))
    {
      int key = Integer.parseInt(st.nextToken());
      int skillId = Integer.parseInt(st.nextToken());
      SkillsBalanceManager.getInstance().removeBalance(key, skillId, classId, forOlympiad);
      html = html + showSearchResults(st, activeChar, forOlympiad, classId);
    }
    else if (cmd.equalsIgnoreCase("increase"))
    {
      int key = Integer.parseInt(st.nextToken());
      int skillId = Integer.parseInt(st.nextToken());
      int type = Integer.parseInt(st.nextToken());
      double value = Double.parseDouble(st.nextToken());
      SkillsBalanceManager.getInstance().updateBalance(key, skillId, classId, type, value, forOlympiad);
      html = html + showSearchResults(st, activeChar, forOlympiad, classId);
    }
    else if (cmd.equalsIgnoreCase("addpage"))
    {
      double[] values = { 1.0D, 1.0D, -1.0D, -1.0D };
      





      int race = Integer.parseInt(st.nextToken());
      for (int i = 0; i < 7; i++)
      {
        if (!st.hasMoreTokens()) {
          break;
        }
        values[i] = Double.parseDouble(st.nextToken());
      }
      html = html + showAddPage(classId, race, values, forOlympiad);
    }
    else if (cmd.equalsIgnoreCase("add"))
    {
      String className = st.nextToken();
      if (className.startsWith(" ")) {
        className = className.substring(1);
      }
      if (className.endsWith(" ")) {
        className = className.substring(0, className.length() - 1);
      }
      className = className.replaceAll(" ", "");
      if (!className.equals("")) {
        for (ClassId cId : ClassId.values()) {
          if (cId.name().equalsIgnoreCase(className)) {
            classId = cId.getId();
          }
        }
      }
      String skill = st.nextToken().replaceAll(" ", "");
      boolean isNumber = true;
      int skillId = -1;
      try
      {
        skillId = Integer.parseInt(skill);
      }
      catch (NumberFormatException e)
      {
        isNumber = false;
      }
      double[] values = { 1.0D, 1.0D, -1.0D, -1.0D };
      





      int race = Integer.parseInt(st.nextToken());
      for (int i = 0; i < 7; i++)
      {
        if (!st.hasMoreTokens()) {
          break;
        }
        values[i] = Double.parseDouble(st.nextToken());
      }
      if (!isNumber)
      {
        activeChar.sendMessage("Implemented skill id is not number [" + skill + "]!");
        html = html + showAddPage(classId, race, values, forOlympiad);
      }
      else
      {
        values[2] = skillId;
        values[3] = classId;
        int key = skillId * (classId < 0 ? -1 : 1) + classId * 65536;
        SkillsBalanceManager.getInstance().updateBalance(key, skillId, classId, values, forOlympiad);
        StringTokenizer st1 = new StringTokenizer("" + skillId);
        html = html + showHeading(forOlympiad, classId);
        html = html + showSearchResults(st1, activeChar, forOlympiad, classId);
      }
    }
    html = html + "</body></html>";
    separateAndSend(html, activeChar);
  }
  
  public String showSearchResults(StringTokenizer st, L2PcInstance activeChar, boolean forOlympiad, int classId)
  {
    String html = "";
    String skill = "";
    if (st.hasMoreTokens()) {
      skill = st.nextToken();
    }
    skill = skill.replace(" ", "");
    int page = 1;
    if (st.hasMoreTokens()) {
      page = Integer.parseInt(st.nextToken());
    }
    boolean isId = true;
    int skillId = -1;
    try
    {
      skillId = Integer.parseInt(skill);
    }
    catch (NumberFormatException e)
    {
      isId = false;
    }
    if ((!isId) && (skill.length() < 4))
    {
      activeChar.sendMessage("You can not imput less than 4 characters for name search!");
      html = html + showMain(forOlympiad, classId);
    }
    else if ((!isId) && (skill.length() > 3))
    {
      ArrayList<Integer> skills = SkillsBalanceManager.getInstance().getSkillsByName(forOlympiad, skill, classId);
      if (skills.size() < 1)
      {
        String cl = "";
        if (classId >= 0)
        {
          String name = ClassId.getClassId(classId).name();
          name = name.substring(0, 1).toUpperCase() + name.substring(1);
          cl = " to target " + name;
        }
        activeChar.sendMessage("No used skills were found using " + skill + cl + "!");
        html = html + showMain(forOlympiad, classId);
      }
      else
      {
        html = html + showSkills(forOlympiad, classId, skill, skills, page);
      }
    }
    else
    {
      ArrayList<Integer> skills = SkillsBalanceManager.getInstance().getUsedSkillsById(forOlympiad, skillId, classId);
      if ((skills == null) || (skills.size() < 1))
      {
        String cl = "";
        if (classId >= 0)
        {
          String name = ClassId.getClassId(classId).name();
          name = name.substring(0, 1).toUpperCase() + name.substring(1);
          cl = " to target " + name;
        }
        activeChar.sendMessage("No used skills were found using ID[" + skillId + "]" + cl + "!");
        html = html + showMain(forOlympiad, classId);
      }
      else
      {
        html = html + showSkills(forOlympiad, classId, String.valueOf(skillId), skills, page);
      }
    }
    return html;
  }
  
  public String showHeading(boolean forOlympiad, int classId)
  {
    String html = "<center>";
    html = html + "<font name=\"ScreenMessageLarge\">Skills balancer</font><br>";
    html = html + "<table width=500><tr>";
    html = html + "<td width=20 align=center><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";main;" + (forOlympiad ? 0 : 1) + ";" + classId + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + (forOlympiad ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + (forOlympiad ? "_checked" : "") + "\"/></td>";
    html = html + "<td width=200 align=left><font color=BABABA>Show for Olympiad</font><img width=1 height=10 src=\"L2UI.SquareBlank\"/></td>";
    html = html + "<td width=140 align=left><edit var=\"skill\" width=140 height=15></td>";
    html = html + "<td width=140 align=left><button value=\"Search\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";search;" + (forOlympiad ? 1 : 0) + ";" + classId + "; $skill \" width=\"74\" height=\"22\" back=\"L2UI_CH3.Btn1_normal_Down\" fore=\"L2UI_CH3.Btn1_normal\"></td>";
    html = html + "</tr></table><br>";
    return html;
  }
  
  public String showMain(boolean forOlympiad, int classId)
  {
    String html = "";
    html = html + "<table width=700><tr>";
    int i = 0;
    for (ClassId cl : ClassId.values()) {
      if (cl.level() >= 3)
      {
        if (i % 12 == 0) {
          html = html + "<td width=200>";
        }
        html = html + "<table width=200 align=center " + (i % 2 == 0 ? "bgcolor=000000" : "") + ">";
        String name = cl.name();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        html = html + "<tr><td width=20 align=center><img width=1 height=3 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";main;" + (forOlympiad ? 1 : 0) + ";" + (cl.getId() == classId ? -1 : cl.getId()) + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + (cl.getId() == classId ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + (cl.getId() == classId ? "_checked" : "") + "\"/></td><td width=200><font name=ScreenMessageSmall color=BABABA>" + name + "</font></td></tr>";
        html = html + "</table>";
        if (i % 12 == 11) {
          html = html + "</td>";
        }
        i++;
      }
    }
    if (!html.endsWith("</td>")) {
      html = html + "</td>";
    }
    html = html + "</tr></table><br>";
    html = html + "<table bgcolor=000000><tr>";
    html = html + "<td width=400></td>";
    html = html + "<td width=200></td>";
    html = html + "<td><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";addpage;0;" + classId + ";0\" width=\"24\" height=\"24\" back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\" ></td>";
    html = html + "</tr></table>";
    return html;
  }
  
  public String showSkills(boolean forOlympiad, int classId, String search, ArrayList<Integer> skills, int page)
  {
    String html = "<center>";
    html = html + "<table width=602 align=center bgcolor=000000>";
    html = html + "<tr>";
    html = html + "<td width=30 align=center></td>";
    html = html + "<td width=400><img width=1 height=5 src=\"L2UI.SquareBlank\"/><font color=2E8424>Skill</font><font color=888888> -></font> <font color=ED792C>Target Class</font></td>";
    html = html + "<td width=85 align=center><font color=888888>Chance</td>";
    html = html + "<td width=85 align=center>Power</font></td>";
    html = html + "</tr>";
    html = html + "</table>";
    html = html + "<img src=\"L2UI.SquareGray\" width=600 height=1/>";
    int i = 0;
    int f = 0;
    int objectsInPage = 3;
    Iterator i$;
    if (skills != null) {
      for (i$ = skills.iterator(); i$.hasNext();)
      {
        int key = ((Integer)i$.next()).intValue();
        if ((i < (page - 1) * objectsInPage) || (i >= page * objectsInPage))
        {
          i++;
        }
        else
        {
          double[] values = SkillsBalanceManager.getInstance().getBalance(key, forOlympiad);
          String targetClassName = "All";
          if ((int)values[3] > -1)
          {
            targetClassName = ClassId.getClassId((int)values[3]).name();
            targetClassName = targetClassName.substring(0, 1).toUpperCase() + targetClassName.substring(1);
          }
          L2Skill sk = SkillTable.getInstance().getInfo((int)values[2], 1);
          html = html + "<table width=600 align=center " + (f % 2 == 0 ? "bgcolor=000000" : "") + ">";
          html = html + "</tr><tr><td><img width=1 height=3 src=\"L2UI.SquareBlank\"/></td></tr>";
          html = html + "<tr>";
          
          html = html + "<td width=30 align=center><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";delete;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + key + ";" + sk.getId() + "; " + search + ";" + page + "\" back=\"L2UI_CH3.FrameCloseOnBtn\" width=14 height=14 fore=\"L2UI_CH3.FrameCloseBtn\" ></td>";
          html = html + "<td width=400>";
          html = html + "<font color=2E8424>" + sk.getName() + "</font><font color=888888> -></font> <font color=ED792C>" + targetClassName + "</font>";
          html = html + "</font></td>";
          html = html + "<td>";
          html = html + "<font color=888888><table>";
          String h1 = "<tr>";
          String h2 = "<tr>";
          String h3 = "<tr>";
          for (int h = 0; h < 2; h++)
          {
            int val = (int)((values[h] - 1.0D) * 100.0D);
            h1 = h1 + "<td width=35 align=center><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";increase;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + key + ";" + sk.getId() + ";" + h + ";" + (values[h] + 0.1D) + "; " + search + "\" back=\"L2UI_CH3.upbutton_down\" width=14 height=14 fore=\"L2UI_CH3.UpButton\" ></td>";
            h2 = h2 + "<td width=85 align=center>" + (val >= 0 ? "+" + val : Integer.valueOf(val)) + "%</td>";
            h3 = h3 + "<td width=35 align=center><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";increase;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + key + ";" + sk.getId() + ";" + h + ";" + (values[h] - 0.1D) + "; " + search + "\" back=\"L2UI_CH3.downbutton_down\" width=14 height=14 fore=\"L2UI_ch3.DownButton\" ></td>";
          }
          html = html + h1 + "</tr>" + h2 + "</tr>" + h3 + "</tr>";
          html = html + "</table></font>";
          html = html + "</td>";
          html = html + "</tr><tr><td><img width=1 height=5 src=\"L2UI.SquareBlank\"/></td></tr>";
          html = html + "</table>";
          html = html + "<img src=\"L2UI.SquareGray\" width=600 height=1/>";
          i++;
          f++;
        }
      }
    }
    if ((i == 0) || (f == 0))
    {
      html = html + "<table width=501 align=center bgcolor=000000>";
      html = html + "<tr><td align=center width=602>No balances found!</td></tr>";
      html = html + "</table>";
      html = html + "<img src=\"L2UI.SquareGray\" width=600 height=1/>";
    }
    html = html + "<table bgcolor=000000><tr>";
    if (page > 1) {
      html = html + "<td><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";search;" + (forOlympiad ? 1 : 0) + ";" + classId + "; " + search + ";" + (page - 1) + "\" width=\"14\" height=\"14\" back=\"L2UI_CH3.prev1_down\" fore=\"L2UI_CH3.prev1\" ></td>";
    } else {
      html = html + "<td width=14></td>";
    }
    html = html + "<td width=20 align=center><font color=CBCBCB>" + page + "</font></td>";
    if ((skills != null) && (page * objectsInPage < skills.size())) {
      html = html + "<td><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";search;" + (forOlympiad ? 1 : 0) + ";" + classId + "; " + search + ";" + (page + 1) + "\" width=\"14\" height=\"14\" width=\"14\" height=\"14\" back=\"L2UI_CH3.next1_down\" fore=\"L2UI_CH3.next1\" ></td>";
    } else {
      html = html + "<td width=14></td>";
    }
    html = html + "</tr></table>";
    html = html + "<button value=\"Back\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";main\" width=\"74\" height=\"22\" back=\"L2UI_CH3.Btn1_normal_Down\" fore=\"L2UI_CH3.Btn1_normal\" >";
    html = html + "</center>";
    html = html + "Indicated search keywords [" + search + "]<br>";
    return html;
  }
  
  public String showAddPage(int classId, int race, double[] values, boolean forOlympiad)
  {
    String vals = ";" + values[0] + ";" + values[1];
    String classes = "";
    for (ClassId cl : ClassId.values()) {
      if ((cl.level() == 3) && (cl.getRace().ordinal() == race))
      {
        String className = cl.name();
        className = className.substring(0, 1).toUpperCase() + className.substring(1);
        classes = classes + className + ";";
      }
    }
    String content = "<br><font name=\"ScreenMessageSmall\" color=BBBBBB>";
    content = content + "<table width=\"640\">";
    content = content + "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=10></td></tr>";
    content = content + "<tr>";
    content = content + "<td><table>";
    content = content + "<tr><td width=100></td><td></td><td width=200>Target Class Id</td></tr>";
    content = content + "<tr><td width=100></td><td></td><td><img src=\"L2UI.SquareBlank\" width=40 height=10></td></tr>";
    content = content + "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";0;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 0 ? "_checked" : "") + "\">";
    content = content + "</td><td>Human</td></tr>";
    content = content + "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";1;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 1 ? "_checked" : "") + "\">";
    content = content + "</td><td>Elf</td></tr>";
    content = content + "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";2;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 2 ? "_checked" : "") + "\">";
    content = content + "</td><td>Dark Elf</td></tr>";
    content = content + "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";3;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 3 ? "_checked" : "") + "\">";
    content = content + "</td><td>Orc</td></tr>";
    content = content + "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";4;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 4 ? "_checked" : "") + "\">";
    content = content + "</td><td>Dwarf</td></tr>";
    content = content + "<tr><td width=100></td><td></td><td><combobox var=\"classId\" list=\"All;" + classes + "\" width=110></td></tr>";
    content = content + "</table></td>";
    content = content + "<td><table>";
    content = content + "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=100></td></tr>";
    content = content + "<tr><td width=100></td><td><edit var=\"skillId\" width=140 height=15></td><td width=200>Skill Id</td></tr>";
    content = content + "</table></td>";
    content = content + "</tr>";
    content = content + "</table><br><br>";
    content = content + "<table width=540 align=center>";
    content = content + "<tr>";
    content = content + "<td width=100 align=center></td>";
    content = content + "<td width=300 align=center>Chance</td>";
    content = content + "<td width=300 align=center>Power</td>";
    content = content + "</tr>";
    String h1 = "<tr><td width=100></td>";
    String h2 = "<tr><td width=100></td>";
    String h3 = "<tr><td width=100></td>";
    for (int h = 0; h < 2; h++)
    {
      int val = (int)((values[h] - 1.0D) * 100.0D);
      h1 = h1 + "<td width=170 align=center><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + race + ";" + (values[0] + (h == 0 ? 0.1D : 0.0D)) + ";" + (values[1] + (h == 1 ? 0.1D : 0.0D)) + "\" back=\"L2UI_CH3.upbutton_down\" width=14 height=14 fore=\"L2UI_CH3.UpButton\" ></td>";
      h2 = h2 + "<td width=170 align=center>" + (val >= 0 ? "+" + val : Integer.valueOf(val)) + "%</td>";
      h3 = h3 + "<td width=170 align=center><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + race + ";" + (values[0] - (h == 0 ? 0.1D : 0.0D)) + ";" + (values[1] - (h == 1 ? 0.1D : 0.0D)) + "\" back=\"L2UI_CH3.downbutton_down\" width=14 height=14 fore=\"L2UI_ch3.DownButton\" ></td>";
    }
    content = content + h1 + "</tr>" + h2 + "</tr>" + h3 + "</tr>";
    content = content + "</table>";
    content = content + "<center><br>";
    content = content + "<table width=170><tr><td width=20 align=center><img width=1 height=3 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 0 : 1) + ";" + classId + ";" + race + vals + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + (forOlympiad ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + (forOlympiad ? "_checked" : "") + "\"/></td><td width=150 align=left><font name=ScreenMessageSmall color=BABABA>For Olympiad</font></td></tr></table>";
    content = content + "<br>";
    content = content + "<button value=\"Add\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";add;" + (forOlympiad ? 1 : 0) + "; $classId ; $skillId ;" + race + vals + "\" width=\"74\" height=\"22\" back=\"L2UI_CH3.Btn1_normal_Down\" fore=\"L2UI_CH3.Btn1_normal\">";
    content = content + "</center><br>";
    content = content + "<table width=500><tr><td width=500 align=right>";
    content = content + "<button value=\"Back\" action=\"bypass " + SKILLS_BALANCE_BBS_CMD + ";main;0\" width=\"74\" height=\"22\" back=\"L2UI_CH3.Btn1_normal_Down\" fore=\"L2UI_CH3.Btn1_normal\">";
    content = content + "</td></tr></table><br>";
    content = content + "</font>";
    return content;
  }
  
  @Override
public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar) {}
  
  public static SkillsBalanceBBSManager getInstance()
  {
    return SingletonHolder._instance;
  }
  
  private static class SingletonHolder
  {
    protected static final SkillsBalanceBBSManager _instance = new SkillsBalanceBBSManager();
  }
}
