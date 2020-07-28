package com.l2jfrozen.gameserver.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.l2jfrozen.gameserver.communitybbs.Manager.BaseBBSManager;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.base.ClassId;


public class ClassBalanceBBS
  extends BaseBBSManager
{
  public static String CLASS_BALANCE_BBS_CMD = "_bbsbalancer";
  
  public void parseCmd(String command, L2PcInstance activeChar)
  {
    if (!activeChar.isGM()) {
      return;
    }
    BalanceManager.getInstance().loadSecondProffessions();
    if (command.equals("admin_classbalancer")) {
      command = CLASS_BALANCE_BBS_CMD + ";main";
    }
    String html = "<html><body><br><br>";
    command = command.substring(command.length() > CLASS_BALANCE_BBS_CMD.length() ? CLASS_BALANCE_BBS_CMD.length() + 1 : CLASS_BALANCE_BBS_CMD.length());
    if (command.startsWith("main"))
    {
      int classId = -1;
      int targetClassId = -1;
      boolean forOlympiad = false;
      if (command.length() > 4)
      {
        StringTokenizer st = new StringTokenizer(command.substring(5), ";");
        if (st.hasMoreTokens()) {
          forOlympiad = st.nextToken().equals("1");
        }
        if (st.hasMoreTokens()) {
          classId = Integer.parseInt(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          targetClassId = Integer.parseInt(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          classId = Integer.parseInt(st.nextToken());
        }
      }
      html = html + showMainPage(classId, targetClassId, forOlympiad);
    }
    else if (command.startsWith("show"))
    {
      StringTokenizer st = new StringTokenizer(command.substring(5), ";");
      boolean forOlympiad = st.nextToken().equals("1");
      int page = Integer.parseInt(st.nextToken());
      int classId = Integer.parseInt(st.nextToken());
      int targetClassId = Integer.parseInt(st.nextToken());
      html = html + showPage(page, classId, targetClassId, forOlympiad);
    }
    else if (command.startsWith("increase"))
    {
      StringTokenizer st = new StringTokenizer(command.substring(9), ";");
      boolean forOlympiad = st.nextToken().equals("1");
      int page = Integer.parseInt(st.nextToken());
      int classId = Integer.parseInt(st.nextToken());
      int targetClassId = Integer.parseInt(st.nextToken());
      int cId = Integer.parseInt(st.nextToken());
      int tcId = Integer.parseInt(st.nextToken());
      int key = Integer.parseInt(st.nextToken());
      int type = Integer.parseInt(st.nextToken());
      double value = Double.parseDouble(st.nextToken());
      BalanceManager.getInstance().updateBalance(key, cId, tcId, type, value, forOlympiad);
      html = html + showPage(page, classId, targetClassId, forOlympiad);
    }
    else if (command.startsWith("delete"))
    {
      StringTokenizer st = new StringTokenizer(command.substring(7), ";");
      boolean forOlympiad = st.nextToken().equals("1");
      int page = Integer.parseInt(st.nextToken());
      int classId = Integer.parseInt(st.nextToken());
      int targetClassId = Integer.parseInt(st.nextToken());
      int cId = Integer.parseInt(st.nextToken());
      int tcId = Integer.parseInt(st.nextToken());
      int key = Integer.parseInt(st.nextToken());
      BalanceManager.getInstance().removeBalance(key, cId, tcId, forOlympiad);
      html = html + showPage(page, classId, targetClassId, forOlympiad);
    }
    else if (command.startsWith("addpage"))
    {
      StringTokenizer st = new StringTokenizer(command.substring(8), ";");
      boolean forOlympiad = st.nextToken().equals("1");
      int classId = Integer.parseInt(st.nextToken());
      int targetClassId = Integer.parseInt(st.nextToken());
      int race = 0;int tRace = 0;
      if (st.hasMoreTokens()) {
        race = Integer.parseInt(st.nextToken());
      }
      if (st.hasMoreTokens()) {
        tRace = Integer.parseInt(st.nextToken());
      }
      double[] values = { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D };
      for (int i = 0; i < 7; i++)
      {
        if (!st.hasMoreTokens()) {
          break;
        }
        values[i] = Double.parseDouble(st.nextToken());
      }
      html = html + showAddPage(classId, targetClassId, race, tRace, values, forOlympiad);
    }
    else if (command.startsWith("add"))
    {
      StringTokenizer st = new StringTokenizer(command.substring(4), ";");
      boolean forOlympiad = st.nextToken().equals("1");
      String className = st.nextToken();
      if (className.startsWith(" ")) {
        className = className.substring(1);
      }
      if (className.endsWith(" ")) {
        className = className.substring(0, className.length() - 1);
      }
      className = className.replaceAll(" ", "");
      String targetClassName = st.nextToken();
      if (targetClassName.startsWith(" ")) {
        targetClassName = targetClassName.substring(1);
      }
      if (targetClassName.endsWith(" ")) {
        targetClassName = targetClassName.substring(0, targetClassName.length() - 1);
      }
      targetClassName = targetClassName.replaceAll(" ", "");
      int classId = -1;
      int targetClassId = -1;
      if (!className.equals("")) {
        for (ClassId cId : ClassId.values()) {
          if (cId.name().equalsIgnoreCase(className)) {
            classId = cId.getId();
          }
        }
      }
      if (!targetClassName.equals("")) {
        for (ClassId cId : ClassId.values()) {
          if (cId.name().equalsIgnoreCase(targetClassName)) {
            targetClassId = cId.getId();
          }
        }
      }
      double[] values = { 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, -1.0D, -1.0D };
      for (int i = 0; i < 7; i++)
      {
        if (!st.hasMoreTokens()) {
          break;
        }
        values[i] = Double.parseDouble(st.nextToken());
      }
      values[7] = classId;
      values[8] = targetClassId;
      int key = classId * 256 * (targetClassId == -1 ? -1 : 1) + (targetClassId == -1 ? 0 : targetClassId);
      BalanceManager.getInstance().updateBalance(key, classId, targetClassId, values, forOlympiad);
      html = html + showPage(1, classId, targetClassId, forOlympiad);
    }
    html = html + "</body></html>";
    separateAndSend(html, activeChar);
  }
  
  public String showMainPage(int classId, int targetClassId, boolean forOlympiad)
  {
    String html = "<center>";
    html = html + "Used classes in balancer<br>";
    html = html + "<table width=220><tr><td width=20 align=center><img width=1 height=3 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";main;" + (forOlympiad ? 0 : 1) + ";" + classId + ";" + targetClassId + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + (forOlympiad ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + (forOlympiad ? "_checked" : "") + "\"/></td><td width=200 align=left><font color=BABABA>Show for Olympiad</font></td></tr></table>";
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
        int cId = classId;
        int tcId = targetClassId;
        if ((cId == -1) && (tcId == -1))
        {
          cId = cl.getId();
        }
        else if ((cId == -1) && (tcId != -1))
        {
          if (tcId == cl.getId()) {
            tcId = -1;
          } else {
            cId = cl.getId();
          }
        }
        else if ((cId != -1) && (tcId == -1))
        {
          if (cId == cl.getId()) {
            cId = -1;
          } else {
            tcId = cl.getId();
          }
        }
        else if (tcId == cl.getId())
        {
          tcId = -1;
        }
        else if (cId == cl.getId())
        {
          cId = -1;
        }
        else
        {
          cId = tcId;
          tcId = cl.getId();
        }
        html = html + "<tr><td width=20 align=center><img width=1 height=3 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";main;" + (forOlympiad ? 1 : 0) + ";" + cId + ";" + tcId + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + ((cl.getId() == classId) || (cl.getId() == targetClassId) ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + ((cl.getId() == classId) || (cl.getId() == targetClassId) ? "_checked" : "") + "\"/></td><td width=200><font color=BABABA>" + name + "</font></td></tr>";
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
    int count = 0;
    if ((classId != -1) && (targetClassId != -1))
    {
      if (BalanceManager.getInstance().getAllBalances(forOlympiad).containsKey(Integer.valueOf(classId * 256 + targetClassId))) {
        count++;
      }
      if (BalanceManager.getInstance().getAllBalances(forOlympiad).containsKey(Integer.valueOf(targetClassId * 256 + classId))) {
        count++;
      }
    }
    else if ((classId == -1) && (targetClassId != -1))
    {
      if (BalanceManager.getInstance().getAllBalancesForIngame(forOlympiad).containsKey(Integer.valueOf(targetClassId))) {
        count += ((ArrayList)BalanceManager.getInstance().getAllBalancesForIngame(forOlympiad).get(Integer.valueOf(targetClassId))).size();
      }
    }
    else if ((classId != -1) && (targetClassId == -1))
    {
      if (BalanceManager.getInstance().getAllBalancesForIngame(forOlympiad).containsKey(Integer.valueOf(classId))) {
        count += ((ArrayList)BalanceManager.getInstance().getAllBalancesForIngame(forOlympiad).get(Integer.valueOf(classId))).size();
      }
    }
    html = html + "<table bgcolor=000000><tr>";
    html = html + "<td width=220><font color=CBCBCB>Available " + count + " balances:</font></td>";
    html = html + "<td><img src=\"L2UI.SquareBlank\" width=1 height=2/><button value=\"Show\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";show;" + (forOlympiad ? 1 : 0) + ";1;" + classId + ";" + targetClassId + "\" width=\"74\" height=\"22\" back=\"L2UI_CH3.Btn1_normal_Down\" fore=\"L2UI_CH3.Btn1_normal\" ></td>";
    html = html + "<td width=200></td>";
    html = html + "<td><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;0;" + classId + ";" + targetClassId + "\" width=\"24\" height=\"24\" back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\" ></td>";
    html = html + "</tr></table>";
    html = html + "</center>";
    return html;
  }
  
  public String showPage(int page, int classId, int targetClassId, boolean forOlympiad)
  {
    String html = "<center>";
    HashMap<Integer, double[]> _used = new HashMap();
    Iterator i$;
    if ((classId != -1) && (targetClassId != -1))
    {
      if (BalanceManager.getInstance().getAllBalances(forOlympiad).containsKey(Integer.valueOf(classId * 256 + targetClassId))) {
        _used.put(Integer.valueOf(classId * 256 + targetClassId), BalanceManager.getInstance().getBalance(classId * 256 + targetClassId, forOlympiad));
      }
      if (BalanceManager.getInstance().getAllBalances(forOlympiad).containsKey(Integer.valueOf(targetClassId * 256 + classId))) {
        _used.put(Integer.valueOf(targetClassId * 256 + classId), BalanceManager.getInstance().getBalance(targetClassId * 256 + classId, forOlympiad));
      }
    }
    else
    {
      Iterator i$1;
      if (classId != -1)
      {
        ArrayList<Integer> data = BalanceManager.getInstance().getBalanceForIngame(classId, forOlympiad);
        if ((data != null) && (data.size() > 0)) {
          for (i$1 = data.iterator(); i$1.hasNext();)
          {
            int cl = ((Integer)i$1.next()).intValue();
            
            double[] d = BalanceManager.getInstance().getBalance(cl, forOlympiad);
            if (d != null) {
              _used.put(Integer.valueOf(cl), BalanceManager.getInstance().getBalance(cl, forOlympiad));
            }
          }
        }
      }
      if (targetClassId != -1)
      {
        ArrayList<Integer> data = BalanceManager.getInstance().getBalanceForIngame(targetClassId, forOlympiad);
        if ((data != null) && (data.size() > 0)) {
          for (i$1 = data.iterator(); i$1.hasNext();)
          {
            int cl = ((Integer)i$1.next()).intValue();
            
            double[] d = BalanceManager.getInstance().getBalance(cl, forOlympiad);
            if (d != null) {
              _used.put(Integer.valueOf(cl), BalanceManager.getInstance().getBalance(cl, forOlympiad));
            }
          }
        }
      }
    }
    html = html + "Class balancer<br>";
    html = html + "<table width=600 align=center bgcolor=000000>";
    html = html + "<tr>";
    html = html + "<td width=30 align=center></td>";
    html = html + "<td width=250><img width=1 height=5 src=\"L2UI.SquareBlank\"/><font color=2E8424>Class</font><font color=888888> -></font> <font color=ED792C>Target Class</font></td>";
    html = html + "<td width=70 align=center><font color=888888>N</td>";
    html = html + "<td width=70 align=center>C</td>";
    html = html + "<td width=70 align=center>M</td>";
    html = html + "<td width=70 align=center>MC</td>";
    html = html + "<td width=70 align=center>B</td>";
    html = html + "<td width=70 align=center>PS</td>";
    html = html + "<td width=70 align=center>PSC</font></td>";
    html = html + "</tr>";
    html = html + "</table>";
    html = html + "<img src=\"L2UI.SquareGray\" width=602 height=1/>";
    int i = 0;
    int f = 0;
    int objectsInPage = 3;
    for (Entry<Integer, double[]> entry : _used.entrySet()) {
      if ((i < (page - 1) * objectsInPage) || (i >= page * objectsInPage))
      {
        i++;
      }
      else
      {
        String className = ClassId.getClassIdByOrdinal((int)((double[])entry.getValue())[7]).name();
        className = className.substring(0, 1).toUpperCase() + className.substring(1);
        String targetClassName = "All";
        if ((int)((double[])entry.getValue())[8] > -1)
        {
          targetClassName = ClassId.getClassIdByOrdinal((int)((double[])entry.getValue())[8]).name();
          targetClassName = targetClassName.substring(0, 1).toUpperCase() + targetClassName.substring(1);
        }
        html = html + "<table width=600 align=center " + (f % 2 == 0 ? "bgcolor=000000" : "") + ">";
        html = html + "</tr><tr><td><img width=1 height=3 src=\"L2UI.SquareBlank\"/></td></tr>";
        html = html + "<tr>";
        html = html + "<td width=30 align=center><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";delete;" + (forOlympiad ? 1 : 0) + ";" + page + ";" + classId + ";" + targetClassId + ";" + (int)((double[])entry.getValue())[7] + ";" + (int)((double[])entry.getValue())[8] + ";" + entry.getKey() + "\" back=\"L2UI_CH3.FrameCloseOnBtn\" width=14 height=14 fore=\"L2UI_CH3.FrameCloseBtn\" ></td>";
        html = html + "<td width=250>";
        html = html + "<table><tr><td align=center width=200><font color=2E8424>" + className + "</font></td></tr><tr><td width=100 align=center><font color=ED792C>" + targetClassName + "</font></td></tr></table>";
        html = html + "</td>";
        html = html + "<td>";
        html = html + "<table align=center width=380>";
        String h1 = "<tr>";
        String h2 = "<tr>";
        String h3 = "<tr>";
        for (int h = 0; h < 7; h++)
        {
          int val = (int)((((double[])entry.getValue())[h] - 1.0D) * 100.0D);
          h1 = h1 + "<td width=35 align=center><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";increase;" + (forOlympiad ? 1 : 0) + ";" + page + ";" + classId + ";" + targetClassId + ";" + (int)((double[])entry.getValue())[7] + ";" + (int)((double[])entry.getValue())[8] + ";" + entry.getKey() + ";" + h + ";" + (((double[])entry.getValue())[h] + 0.1D) + "\" back=\"L2UI_CH3.upbutton_down\" width=14 height=14 fore=\"L2UI_CH3.UpButton\" ></td>";
          h2 = h2 + "<td width=70 align=center>" + (val >= 0 ? "+" + val : Integer.valueOf(val)) + "%</td>";
          h3 = h3 + "<td width=35 align=center><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";increase;" + (forOlympiad ? 1 : 0) + ";" + page + ";" + classId + ";" + targetClassId + ";" + (int)((double[])entry.getValue())[7] + ";" + (int)((double[])entry.getValue())[8] + ";" + entry.getKey() + ";" + h + ";" + (((double[])entry.getValue())[h] - 0.1D) + "\" back=\"L2UI_CH3.downbutton_down\" width=14 height=14 fore=\"L2UI_ch3.DownButton\" ></td>";
        }
        html = html + h1 + "</tr>" + h2 + "</tr>" + h3 + "</tr>";
        html = html + "</table>";
        html = html + "</td>";
        html = html + "</tr><tr><td><img width=1 height=5 src=\"L2UI.SquareBlank\"/></td></tr>";
        html = html + "</table>";
        html = html + "<img src=\"L2UI.SquareGray\" width=602 height=1/>";
        i++;
        f++;
      }
    }
    if ((i == 0) || (f == 0))
    {
      html = html + "<table width=604 align=center bgcolor=000000>";
      html = html + "<tr><td align=center width=701><font color=CF1616>No balances found!</font></td></tr>";
      html = html + "</table>";
      html = html + "<img src=\"L2UI.SquareGray\" width=602 height=1/>";
    }
    html = html + "<table bgcolor=000000><tr>";
    if (page > 1) {
      html = html + "<td><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";show;" + (forOlympiad ? 1 : 0) + ";" + (page - 1) + ";" + classId + ";" + targetClassId + "\" width=\"14\" height=\"14\" back=\"L2UI_CH3.prev1_down\" fore=\"L2UI_CH3.prev1\" ></td>";
    } else {
      html = html + "<td width=14></td>";
    }
    html = html + "<td width=20 align=center><font color=CBCBCB>" + page + "</font></td>";
    if (page * objectsInPage < _used.size()) {
      html = html + "<td><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";show;" + (forOlympiad ? 1 : 0) + ";" + (page + 1) + ";" + classId + ";" + targetClassId + "\" width=\"14\" height=\"14\" back=\"L2UI_CH3.next1_down\" fore=\"L2UI_CH3.next1\" ></td>";
    } else {
      html = html + "<td width=14></td>";
    }
    html = html + "</tr></table>";
    html = html + "<button value=\"Back\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";main;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + "\" width=\"135\" height=\"21\" back=\"L2UI_CH3.bigbutton3_down\" fore=\"L2UI_CH3.bigbutton3\" >";
    html = html + "</center>";
    return html;
  }
  
  public String showAddPage(int classId, int targetClassId, int race, int tRace, double[] values, boolean forOlympiad)
  {
    String vals = "";
    for (int i = 0; i < 7; i++) {
      vals = vals + ";" + values[i];
    }
    String classes = "";
    for (ClassId cl : ClassId.values()) {
      if ((cl.level() == 3) && (cl.getRace().ordinal() == race))
      {
        String className = cl.name();
        className = className.substring(0, 1).toUpperCase() + className.substring(1);
        classes = classes + className + ";";
      }
    }
    String tClasses = "";
    for (ClassId cl : ClassId.values()) {
      if ((cl.level() == 3) && (cl.getRace().ordinal() == tRace))
      {
        String className = cl.name();
        className = className.substring(0, 1).toUpperCase() + className.substring(1);
        tClasses = tClasses + className + ";";
      }
    }
    String content = "<br><font color=BBBBBB>";
    content = content + "<table width=\"600\">";
    content = content + "<tr><td><img src=\"L2UI.SquareBlank\" width=20 height=10></td></tr>";
    content = content + "<tr><td></td><td align=\"center\">Class Id</td><td align=\"center\">Target Class Id</td><td width=\"20\"></td></tr>";
    content = content + "<tr><td><img src=\"L2UI.SquareBlank\" width=20 height=10></td></tr>";
    content = content + "<tr><td><table>";
    content = content + "<tr><td width=35></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";0;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 0 ? "_checked" : "") + "\">";
    content = content + "</td><td>Human</td></tr>";
    content = content + "<tr><td width=35></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";1;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 1 ? "_checked" : "") + "\">";
    content = content + "</td><td>Elf</td></tr>";
    content = content + "<tr><td width=35></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";2;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 2 ? "_checked" : "") + "\">";
    content = content + "</td><td>Dark Elf</td></tr>";
    content = content + "<tr><td width=35></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";3;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 3 ? "_checked" : "") + "\">";
    content = content + "</td><td>Orc</td></tr>";
    content = content + "<tr><td width=35></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";4;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (race == 4 ? "_checked" : "") + "\">";
    content = content + "</td><td>Dwarf</td></tr>";
    content = content + "</table></td>";
    content = content + "<td align=\"center\"><combobox var=\"classId\" list=\"" + classes + "\" width=110></td>";
    content = content + "<td align=\"center\"><combobox var=\"tClassId\" list=\"All;" + tClasses + "\" width=110></td>";
    content = content + "<td><table>";
    content = content + "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";0" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (tRace == 0 ? "_checked" : "") + "\">";
    content = content + "</td><td>Human</td></tr>";
    content = content + "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";1" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (tRace == 1 ? "_checked" : "") + "\">";
    content = content + "</td><td>Elf</td></tr>";
    content = content + "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";2" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (tRace == 2 ? "_checked" : "") + "\">";
    content = content + "</td><td>Dark Elf</td></tr>";
    content = content + "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";3" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (tRace == 3 ? "_checked" : "") + "\">";
    content = content + "</td><td>Orc</td></tr>";
    content = content + "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
    content = content + "<button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";4" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + (tRace == 4 ? "_checked" : "") + "\">";
    content = content + "</td><td>Dwarf</td></tr>";
    content = content + "</table></td>";
    content = content + "</tr>";
    content = content + "</table>";
    content = content + "<table>";
    content = content + "<tr><td width=40></td>";
    content = content + "<td width=70 align=center>N</td>";
    content = content + "<td width=70 align=center>C</td>";
    content = content + "<td width=70 align=center>M</td>";
    content = content + "<td width=70 align=center>MC</td>";
    content = content + "<td width=70 align=center>B</td>";
    content = content + "<td width=70 align=center>PS</td>";
    content = content + "<td width=70 align=center>PSC</td>";
    content = content + "</tr>";
    String h1 = "<tr><td width=40></td>";
    String h2 = "<tr><td width=40></td>";
    String h3 = "<tr><td width=40></td>";
    for (int h = 0; h < 7; h++)
    {
      String v = "";
      for (int x = 0; x < h; x++) {
        v = v + ";" + values[x];
      }
      String v2 = "";
      for (int x = h + 1; x < 7; x++) {
        v2 = v2 + ";" + values[x];
      }
      int val = (int)((values[h] - 1.0D) * 100.0D);
      h1 = h1 + "<td width=70 align=center><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";" + tRace + v + ";" + (values[h] + 0.1D) + v2 + "\" back=\"L2UI_CH3.upbutton_down\" width=14 height=14 fore=\"L2UI_CH3.UpButton\" ></td>";
      h2 = h2 + "<td width=70 align=center>" + (val >= 0 ? "+" + val : Integer.valueOf(val)) + "%</td>";
      h3 = h3 + "<td width=70 align=center><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";" + tRace + v + ";" + (values[h] - 0.1D) + v2 + "\" back=\"L2UI_CH3.downbutton_down\" width=14 height=14 fore=\"L2UI_ch3.DownButton\" ></td>";
    }
    content = content + h1 + "</tr>" + h2 + "</tr>" + h3 + "</tr>";
    content = content + "</table>";
    content = content + "<center><br>";
    content = content + "<table width=170><tr><td width=20 align=center><img width=1 height=3 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 0 : 1) + ";" + classId + ";" + targetClassId + ";" + race + ";" + tRace + vals + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + (forOlympiad ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + (forOlympiad ? "_checked" : "") + "\"/></td><td width=150 align=left><font name=ScreenMessageSmall color=BABABA>For Olympiad</font></td></tr></table>";
    content = content + "<br>";
    content = content + "<button value=\"Add\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";add;" + (forOlympiad ? 1 : 0) + "; $classId ; $tClassId " + vals + "\" width=75 height=24 back=\"L2UI_CH3.Btn1_normal_Down\" fore=\"L2UI_CH3.Btn1_normal\">";
    content = content + "</center><br>";
    content = content + "<table width=600><tr><td width=600 align=right>";
    content = content + "<button value=\"Back\" action=\"bypass " + CLASS_BALANCE_BBS_CMD + ";main;0;" + classId + ";" + targetClassId + "\" width=75 height=24 back=\"L2UI_CH3.Btn1_normal_Down\" fore=\"L2UI_CH3.Btn1_normal\">";
    content = content + "</td></tr></table><br>";
    content = content + "<center>";
    content = content + "N -> Normal, C -> Critical, M -> Magic, MC -> Magic Critical,</br1>B -> Blow, PS -> Physical Skill, PSC -> Physical Skill Critical.";
    content = content + "</center></font>";
    return content;
  }
  
  public void parseWrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar) {}
  
  public static ClassBalanceBBS getInstance()
  {
    return SingletonHolder._instance;
  }
  
  private static class SingletonHolder
  {
    protected static final ClassBalanceBBS _instance = new ClassBalanceBBS();
  }
}
