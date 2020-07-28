/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jfrozen.gameserver.custom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;

/**
 *
 * @author gevorakoC
 */
public class ClassBalanceManager
{
	private static Logger _log = Logger.getLogger(ClassBalanceManager.class.getName());
	HashMap<String, Double> classes;
	HashMap<String, HashMap<String, Double>> changes;

	public ClassBalanceManager()
	{
		classes = new HashMap<>();
		changes = new HashMap<>();
		changes.put("add", new HashMap<String, Double>());
		changes.put("modify", new HashMap<String, Double>());
		changes.put("remove", new HashMap<String, Double>());
		load();
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new UpdateClassBalance(), 30000, 600000);
	}

	public void load()
	{
		_log.info(getClass().getSimpleName()+": Initializing");
		classes.clear();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File file = new File("data/ClassBalance.xml");
		Document doc = null;

		if (file.exists())
		{
			try
			{
				doc = factory.newDocumentBuilder().parse(file);
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Could not parse ClassBalance.xml file: " + e.getMessage(), e);
			}

			if (doc == null) {
				_log.log(Level.WARNING, "Can't load ClassBalance.xml file!");
				return;
			}
			
			Node n = doc.getFirstChild();
			int id = 0;
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equals("balance"))
				{
					int classId = Integer.parseInt(d.getAttributes().getNamedItem("classId").getNodeValue());
					String notEditedType = d.getAttributes().getNamedItem("type").getNodeValue().toLowerCase();
					int type = 0;
					if (notEditedType.equalsIgnoreCase("Magic"))
						type = 1;
					else if (notEditedType.equalsIgnoreCase("Crit"))
						type = 2;
					else if (notEditedType.equalsIgnoreCase("MCrit"))
						type = 3;
					else if (notEditedType.equalsIgnoreCase("Blow"))
						type = 4;
					int targetClassId = Integer.parseInt(d.getAttributes().getNamedItem("targetClassId").getNodeValue());
					
					double val = Double.parseDouble(d.getAttributes().getNamedItem("val").getNodeValue());

					String key = classId + "/" + targetClassId + "/" + type + "/" + id;
					classes.put(key, val);
					id++;
				}
			}
		}
		_log.info(getClass().getSimpleName()+": Successfully loaded " + classes.size() + " class balance edits.");
	}

	public HashMap<String, Double> getAllBalances() {
		return classes;
	}

	public double getBalancedClass(int type, L2PcInstance player, L2PcInstance victim)
	{
		if (player.isInOlympiadMode() || victim.isInOlympiadMode())
			return 1;
		int classId = player.getClassId().getId();
		int targetClassId = victim.getClassId().getId();
		String key = classId + "/" + targetClassId + "/" + type;
		for (Map.Entry<String, Double> m : classes.entrySet()) {
			String data[] = m.getKey().split("/");
			String thisKey = data[0] + "/" + data[1] + "/" + data[2];
			if (thisKey.equalsIgnoreCase(key))
				return m.getValue();
		}
		return 1;
	}

	public void removeClassBalance(int objectId) {
		if (!changes.containsKey("remove"))
			changes.put("remove", new HashMap<String, Double>());
		for (Map.Entry<String, Double> balance : getAllBalances().entrySet()) {
			int id = Integer.parseInt(balance.getKey().split("/")[3]);
			if (id == objectId) {
				if (classes.containsKey(balance.getKey()))
					classes.remove(balance.getKey());
				if (changes.get("remove").containsKey(balance.getKey()))
					changes.get("remove").remove(balance.getKey());
				changes.get("remove").put(balance.getKey(), balance.getValue());
				break;
			}
		}
	}

	public void modifyClassBalance(int objectId, String key, double value) {
		if (!changes.containsKey("modify"))
			changes.put("modify", new HashMap<String, Double>());
		for (Map.Entry<String, Double> balance : getAllBalances().entrySet()) {
			int id = Integer.parseInt(balance.getKey().split("/")[3]);
			if (id == objectId) {
				if (classes.containsKey(balance.getKey()))
					classes.remove(balance.getKey());
				if (classes.containsKey(key))
					classes.remove(key);
				classes.put(key, value);
				if (changes.get("modify").containsKey(balance.getKey()))
					changes.get("modify").remove(balance.getKey());
				if (changes.get("modify").containsKey(key))
					changes.get("modify").remove(key);
				changes.get("modify").put(key, value);
				break;
			}
		}
	}

	public void addClassBalance(int objectId, String key, double value) {
		if (!changes.containsKey("add"))
			changes.put("add", new HashMap<String, Double>());
		for (Map.Entry<String, Double> balance : getAllBalances().entrySet()) {
			int id = Integer.parseInt(balance.getKey().split("/")[3]);
			if (id == objectId) {
				if (classes.containsKey(key))
					classes.remove(key);
				break;
			}
		}
		classes.put(key, value);
		if (changes.get("add").containsKey(key))
			changes.get("add").remove(key);
		changes.get("add").put(key, value);
	}

	class UpdateClassBalance implements Runnable {
		@Override
		public void run() {
			if (!changes.isEmpty())
				rewriteToXml();
		}
	}

	public void rewriteToXml() {
    	try {
    		int objId = 0;
    		HashMap<Integer, HashMap<String, String>> values = new HashMap<>();
    		File file = new File("data/ClassBalance.xml");
    		FileInputStream fistream = new FileInputStream(file);
    		DataInputStream in = new DataInputStream(fistream);
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		String line;

    		while ((line = br.readLine()) != null) {
    			if (!line.contains("<balance"))
    				continue;
    			HashMap<String, String> info = new HashMap<>();
    			String value[] = line.split("=\"");
    			for (int i = 0; i < value.length-1; i++) {
    				String name = value[i].split(" ")[1];
    				String val = value[i+1].split("\"")[0];
    				info.put(name, val);
    			}
    			values.put(objId, info);
    			objId++;
    		}
    		in.close();

			for (Map.Entry<String, HashMap<String, Double>> change : changes.entrySet()) {
				for (Map.Entry<String, Double> data : change.getValue().entrySet()) {
					HashMap<String, String> info = new HashMap<>();
					String strType = "Normal";
					int type = Integer.parseInt(data.getKey().split("/")[2]);
					if (type == 1)
						strType = "Magic";
					else if (type == 2)
						strType = "Crit";
					else if (type == 3)
						strType = "MCrit";
					else if (type == 4)
						strType = "Blow";

					String writeType = change.getKey();
					
					int objectId = Integer.parseInt(data.getKey().split("/")[3]);
					String classId = data.getKey().split("/")[0];
					String val = String.valueOf(data.getValue());
					String targetClassId = data.getKey().split("/")[1];
					if (writeType.equalsIgnoreCase("add")) {
						info.put("classId", classId);
						info.put("type", strType);
						info.put("val", val);
						info.put("targetClassId", targetClassId);
						boolean write = true;
						for (HashMap<String, String> data2 : values.values()) {
							if (data2.get("classId").equalsIgnoreCase(classId) && data2.get("type").equalsIgnoreCase(strType) && 
								data2.get("val").equalsIgnoreCase(val) && data2.get("targetClassId").equalsIgnoreCase(targetClassId))
								write = false;
						}
						if (write) {
							if (values.containsKey(objectId))
								values.remove(objectId);
							values.put(objectId, info);
						}
					}
					else if (writeType.equalsIgnoreCase("modify")) {
						info.put("classId", classId);
						info.put("type", strType);
						info.put("val", val);
						info.put("targetClassId", targetClassId);
						if (values.containsKey(objectId)) {
							values.remove(objectId);
							values.put(objectId, info);
						}
					}
					else if (writeType.equalsIgnoreCase("remove")) {
						if (values.containsKey(objectId))
							values.remove(objectId);
					}
				}
			}
			
	    	// Create file 
	    	FileWriter fstream = new FileWriter(file);
	    	BufferedWriter out = new BufferedWriter(fstream);
	    	out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	    	out.write("<!-- Saved by gevorakoC (" + Calendar.getInstance().getTime().toString() + ") -->\n");
	    	out.write("<list>\n");
			for (HashMap<String, String> value : values.values()) {
				String xml = "	<balance ";
				xml += "classId=\"" + value.get("classId") + "\" ";
				xml += "type=\"" + value.get("type") + "\" ";
				xml += "val=\"" + value.get("val") + "\" ";
				xml += "targetClassId=\"" + value.get("targetClassId") + "\" ";
				xml += "/>\n";
				out.write(xml);
			}
			out.write("</list>");
		    out.close();
		    changes.clear();
    	}
    	catch (Exception e) {//Catch exception if any
    		System.out.println("Error: " + e.getMessage());
    	}
	}

	public static final ClassBalanceManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final ClassBalanceManager _instance = new ClassBalanceManager();
	}
}