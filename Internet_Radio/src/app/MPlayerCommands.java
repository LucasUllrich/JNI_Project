
package app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Queue;
import java.io.File;
import java.net.URL;

/**
 * Class that manages all supported MPlayer commands and provides
 * the player info string queue
 * 
 * @author Tobias Pistora
 * @version 1.0
 * @since 04.01.2020
 */
public final class MPlayerCommands {
    //player handling object
    final JMPlayer playerObject = new JMPlayer();
    private Queue<String> playerInfoQueue;
    private ArrayList<String> presetURLs = new ArrayList<String>();

    public MPlayerCommands(){
        this.playerInfoQueue = playerObject.getPlayerInfoQueue();
        presetURLs.add("http://mp3stream7.apasf.apa.at:8000");          // OE3
        presetURLs.add("http://raj.krone.at/kronehit-ultra-hd.aac");    // Kronehit
        presetURLs.add("http://mp3stream1.apasf.apa.at/;stream.mp3");   // FM4
        presetURLs.add("http://mp3.stream.tb-group.fm/tb.mp3?");        // Technobase.FM
    }

    /**
     * Handles supported MpPlayer commands.
     * 
     * @param command string that contains command and its argument(s)
     * @return the requested information, a status message or an error message
     */
    public synchronized String sendCommand(String command){
        String retStr = "Invalid Command.";
        if(command.startsWith("preset")){
            String cmdArray[] = command.split(" ");
            if (cmdArray.length > 1) {
                if(cmdArray[1].startsWith("show")){
                    retStr = "";
                    for (int i = 0; i < this.presetURLs.size(); i++) {
                        retStr += this.presetURLs.get(i) + "\n";
                    }
                }
                else{
                    int presetIndex = 0;
                    try {
                        presetIndex = Integer.parseInt(cmdArray[1]);
                        if(presetIndex < 0 || presetIndex > presetURLs.size()-1){
                            retStr = "Invalid argument: Number range is from 0 to "+ (presetURLs.size() - 1);
                        }
                        else{
                            try {
                                retStr = playerObject.open(new URL(presetURLs.get(presetIndex)));
                            } catch (MalformedURLException e) {
                                retStr = e.toString();
                                //e.printStackTrace();
                            } catch (IOException e) {
                                retStr = e.toString();
                                //e.printStackTrace();
                            }
                        }
                    } catch (NumberFormatException e) {
                        retStr = "Invalid argument: Command argument must be [show] or a number[uint] from 0 to." + (presetURLs.size() - 1);
                    }
                }
            }
            else{
                retStr = "Argument required [number(uint) or show].";
            }
        }
        else if (command.startsWith("pause")) {
            retStr = playerObject.togglePlay();
        } else if (command.startsWith("start")) {
            String cmdArray[] = command.split(" ");
            if (cmdArray.length > 1) {
                if (cmdArray[1].startsWith("http")) {
                    try {
                        retStr = playerObject.open(new URL(cmdArray[1]));
                    } catch (MalformedURLException e) {
                        retStr = e.toString();
                        //e.printStackTrace();
                    } catch (IOException e) {
                        retStr = e.toString();
                        //e.printStackTrace();
                    }
                } else {
                    try {
                        retStr = playerObject.open(new File(cmdArray[1]));
                    } catch (IOException e) {
                        retStr = e.toString();
                        //e.printStackTrace();
                    }
                }
            }
        }
        else if (command.startsWith("volume")) {
            String cmdArray[] = command.split(" ");
            if (cmdArray.length > 1) {
                float num = 0.0f;
                try {
                    num = Float.parseFloat(cmdArray[1]);
                    playerObject.setVolume(num);
                    retStr = "Set volume.";
                } catch (NullPointerException e) {
                    retStr = e.toString();
                } catch (NumberFormatException e) {
                    retStr = "Invalid number, expected float.";
                }
            }
            else{
                retStr = "Number required [float].";
            }
        }
        else if(command.startsWith("stop")){
            playerObject.close();
            retStr = "Stopping player.";
        }
        else if(command.startsWith("status")){
            retStr = playerObject.forceInfoStreamUpdate();
        }
        else if(command.startsWith("help")){
            retStr = "Supported Commands:\nstart [file path or url]\npause\nstop\nvolume [0.0 - 100.0]\nstatus\npreset [number(uint) or show] loads/shows saved links";
        }
        else{
            retStr = "Invalid Command, use [help] to see available options.";
            //throw new IllegalArgumentException("Invalid Command, use [help] to see available options.");
        }
        return retStr;
    }

    /**
     * Provides player information string queue
     * 
     * @return player information string queue
     */
    public Queue<String> getPlayerInfoQueue(){
        return this.playerInfoQueue;
    }

    public boolean isPlayerPlaying(){
        return playerObject.isPlaying();
    }

    public void closePlayer(){
        playerObject.close();
    }

    /**
     * @return the playerObject
     */
    public JMPlayer getPlayerObject() {
        return playerObject;
    }
}