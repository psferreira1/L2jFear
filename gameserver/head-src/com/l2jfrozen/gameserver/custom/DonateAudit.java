package com.l2jfrozen.gameserver.custom;
 
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import com.l2jfrozen.gameserver.util.Util;
 
public class DonateAudit
{
    static
    {
        new File("log/Donates").mkdirs();
    }
   
    private static final Logger LOGGER = Logger.getLogger(DonateAudit.class.getName());
   
    public static void auditGMAction(String activeChar, String action, String target, String params)
    {
        final File file = new File("log/Donates/" + activeChar + ".txt");
            if (!file.exists())
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
            }
       
        try (FileWriter save = new FileWriter(file, true))
        {
            save.write(Util.formatDate(new Date(), "dd/MM/yyyy H:mm:ss") + ">" + activeChar + ">" + action + ">" + target + ">" + params + "\r\n");
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "GMAudit for GM " + activeChar + " could not be saved: ", e);
        }
    }
   
    public static void auditGMAction(String activeChar, String action, String target)
    {
        auditGMAction(activeChar, action, target, "");
    }
}