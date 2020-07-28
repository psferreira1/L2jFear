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
 
 import java.io.*;
 import java.util.StringTokenizer;

import javolution.text.TextBuilder;

import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.L2GameClient;
import com.l2jfrozen.gameserver.network.clientpackets.Say2;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.CreatureSay;
import com.l2jfrozen.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;

/**
 * @author squallcs
 *
 */
public class L2BugReportInstance extends L2FolkInstance
{
private static String _type;

public L2BugReportInstance(int objectId, L2NpcTemplate template)
{
super(objectId, template);
}

@Override
public void onBypassFeedback(L2PcInstance player, String command)
{
if (command.startsWith("send_report"))
{
StringTokenizer st = new StringTokenizer(command);
st.nextToken();
String msg = null;
String type = null;
type = st.nextToken();
msg = st.nextToken();
try
{
while (st.hasMoreTokens())
{
msg = msg + " " + st.nextToken();
}

sendReport(player, type, msg);
}
catch (StringIndexOutOfBoundsException e)
{
}
}
}

static
{
new File("log/BugReports/").mkdirs();
}

private void sendReport(L2PcInstance player, String command, String msg)
{
String type = command;
L2GameClient info = player.getClient().getConnection().getClient();

if (type.equals("General"))
_type = "General";
if (type.equals("Fatal"))
_type = "Fatal";
if (type.equals("Misuse"))
_type = "Misuse";
if (type.equals("Balance"))
_type = "Balance";
if (type.equals("Other"))
_type = "Other";
if (type.equals("Donate"))
_type = "Donate";

try
{
String fname = "log/BugReports/" + player.getName() + ".txt";
File file = new File(fname);
boolean exist = file.createNewFile();
if (!exist)
{
player.sendMessage("You have already sent a bug report, GMs must check it first.");
return;
}
FileWriter fstream = new FileWriter(fname);
BufferedWriter out = new BufferedWriter(fstream);
out.write("Character Info: " + info + "\r\nBug Type: " + _type + "\r\nMessage: " + msg);
player.sendMessage("Report sent. GMs will check it soon. Thanks...");

for (L2PcInstance allgms : L2World.getInstance().getAllGMs())
allgms.sendPacket(new CreatureSay(0, Say2.SHOUT, "Report Manager", player.getName() + " sent a report."));

System.out.println("Character: " + player.getName() + " sent a bug report.");
out.close();
}
catch (Exception e)
{
player.sendMessage("Something went wrong try again.");
}
}

@Override
public void onAction(L2PcInstance player)
{
if (!canTarget(player))
{
return;
}

if (this != player.getTarget())
{
player.setTarget(this);

player.sendPacket(new MyTargetSelected(getObjectId(), 0));

player.sendPacket(new ValidateLocation(this));
}
else if (!canInteract(player))
{
player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
}
else
{
showHtmlWindow(player);
}

player.sendPacket(new ActionFailed());
}

private void showHtmlWindow(L2PcInstance activeChar)
{
NpcHtmlMessage nhm = new NpcHtmlMessage(5);
TextBuilder replyMSG = new TextBuilder("");


replyMSG.append("<html><body><center>");
replyMSG.append("<img src=\"l2ceriel.1\"width=300 height=100>");
replyMSG.append("<br>");
replyMSG.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
replyMSG.append("<table bgcolor=000000 width=319>");
replyMSG.append("<tr>");
replyMSG.append("<td><center><font color=\"ae9988\">Hey, you want to report something?</font><center></td>");
replyMSG.append("</tr>");
replyMSG.append("</table>");
replyMSG.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
replyMSG.append("<br>");
replyMSG.append("<table width=250><tr>");
replyMSG.append("<td><font color=\"LEVEL\">Select Report Type:</font></td>");
replyMSG.append("<td><combobox width=105 var=type list=General;Fatal;Misuse;Balance;Other;Donate></td>");
replyMSG.append("</tr></table><br><br>");
replyMSG.append("<multiedit var=\"msg\" width=250 height=50><br>");
replyMSG.append("<br><br>");
replyMSG.append("<button value=\"Send Message\" action=\"bypass -h npc_" + getObjectId() + "_send_report $type $msg\" width=132 height=21 back=\"l2ceriel.bat\" fore=\"l2ceriel.bat\"></center>");
replyMSG.append("<br><br>");
replyMSG.append("<br><br>");
replyMSG.append("<br>"); 
replyMSG.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
replyMSG.append("<table bgcolor=000000 width=319>");	
replyMSG.append("<tr>");
replyMSG.append("<td><center><font color=\"E80000\">l2xs.eu</font></center></td>");
replyMSG.append("</tr>");
replyMSG.append("</table>");
replyMSG.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
replyMSG.append("</body></html>");

nhm.setHtml(replyMSG.toString());
activeChar.sendPacket(nhm);

activeChar.sendPacket(new ActionFailed());
}

}