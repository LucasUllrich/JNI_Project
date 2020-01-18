package app;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class HardwareHandler extends Thread {

    MPlayerCommands cmdInterface = null;
   

    HardwareHandler (MPlayerCommands cmdInterface) {
        this.cmdInterface = cmdInterface;
    }

    @Override
    public void run () {
        ButtonManager buttonManager = new ButtonManager();
        DisplayManager displayManager = new DisplayManager();
        JMPlayer player = cmdInterface.getPlayerObject();
        // Map<String, String> playerInfo = new TreeMap<String, String>();
        String playerInfo = "";
        String[] playerInfoElements;
        String[] elementParser;
        int buttonState = 0;
        int volume = 0;
        String senderName = "";
        String title = "";
        int subStringPos = 0;

        buttonManager.start();
        displayManager.start();

        while(true) {
            buttonState = buttonManager.getButtonPressed();
            playerInfo = cmdInterface.getPlayerInfoQueue().peek().toString();
            
            switch (buttonState) {
                    case 1:
                        cmdInterface.sendCommand("preset 0");
                        break;
                    
                    case 2:
                        cmdInterface.sendCommand("preset 1");
                        break;
                    
                    case 3:
                        cmdInterface.sendCommand("preset 2");
                        break;
                    
                    case 4:
                        cmdInterface.sendCommand("preset 3");
                        break;
                    
                        
                    case 6:
                        player.setVolume(0);
                        break;

                    case 7:
                        player.setVolume(player.getVolume() - 5);
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            System.out.println("HardwareHandler could not enter sleep state");
                        }
                        break;
                    
                    case 8:
                        player.setVolume(player.getVolume() + 5);
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            System.out.println("HardwareHandler could not enter sleep state");
                        }
                        break;
                    
                    default:
                        break;
                }

                playerInfoElements = playerInfo.split(Pattern.quote("|"));
    
                for (String element : playerInfoElements) {
                    if (element.startsWith(">volume")) {
                        elementParser = element.split(" ");
                        volume = (int) Float.parseFloat(elementParser[1]);
                    } else if (element.startsWith(">Name")) {
                        elementParser = element.split(":");
                        senderName = elementParser[1].trim();
                    } else if (element.contains("StreamTitle")) {
                        subStringPos = element.indexOf("StreamTitle");
                        title = element.substring(subStringPos, element.length());
                        elementParser = title.split("'");
                        title = elementParser[1].trim();
                    }
                }

                displayManager.setDisplayText1(senderName + " " + "Vol: " + volume);
                displayManager.setDisplayText2(title);

            try {
                Thread.sleep(100);
                
            } catch (Exception e) {
                System.out.println("HardwareHandler could not enter sleep state");
            }
        }
    }
}
