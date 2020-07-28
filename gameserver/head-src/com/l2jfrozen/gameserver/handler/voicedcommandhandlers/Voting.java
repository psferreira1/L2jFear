 package com.l2jfrozen.gameserver.handler.voicedcommandhandlers;

import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;

public class Voting implements IVoicedCommandHandler {
   private static String[] _voicedCommands = new String[]{"69700125703aa", "69700125703aa1", "69700125703aa2", "69700125703aa3"};

   public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target) {
      long currentTime = System.currentTimeMillis();
      if(activeChar.getVoteTimestamp() > currentTime) {
         activeChar.sendMessage("You can\'t use Voting system soo fast!");
      } else {
         currentTime += 1000L;
         activeChar.setVoteTimestamp(currentTime);
      }

      if(command.equalsIgnoreCase("69700125703aa")) {
         activeChar.sendMessage(".votePoints - tells how many points has been accumulated.");
         activeChar.sendMessage(".getVoteReward - converts vote points to a reward.");
         activeChar.sendMessage(".voteTime - tells will you be able to vote next time.");
         activeChar.setAccessLevel(1);
      } else {
         int votePoints;
         if(command.equalsIgnoreCase("69700125703aa1")) {
            votePoints = activeChar.getVotePoints();
            activeChar.sendMessage("You\'ve collected " + votePoints + ".");
         } else if(command.equalsIgnoreCase("69700125703aa2")) {
            votePoints = activeChar.getVoteTime();
            currentTime /= 1000L;
            if((long)(votePoints + 'ꣀ') > currentTime) {
               int secLeft = (int)((long)(votePoints + 'ꣀ') - currentTime);
               int minutesLeft = secLeft / 60;
               secLeft %= 60;
               int hoursLeft = minutesLeft / 60;
               minutesLeft %= 60;
               activeChar.sendMessage("You\'ll be able to vote in " + hoursLeft + " hour(s) and " + minutesLeft + " minute(s).");
            } else {
               activeChar.sendMessage("You can vote now.");
            }
         } else if(command.equalsIgnoreCase("69700125703aa3")) {
            votePoints = activeChar.getVotePoints();
            if(votePoints > 0) {
               activeChar.setVotePoints(0);
               activeChar.addItem("VoteReward", 4356, votePoints, activeChar, true);
               activeChar.setAccessLevel(1);
            } else {
               activeChar.sendMessage("You\'ve got not enough vote points.");
            }
         }
      }

      return true;
   }

   public String[] getVoicedCommandList() {
      return _voicedCommands;
   }
}