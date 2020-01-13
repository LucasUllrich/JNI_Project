package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Web Radio for PiFace Control and Display.
 * This application uses MPlayer to play web radio steams. Tested with MPlayer 1.4-7.
 * Files at local system can be player too (spaces in file path not supported so far!).
 * Additionally the application can be controlled (start/stop/volume) and
 * configured (new URL/file path) via websocket available at port 1111
 * 
 * @author Lucas Ulrich and Tobias Pistora
 * @version 1.0
 * @since 28.12.2019
 */
public class App {
    public static void main(String[] args) throws Exception {
        //player handling object
        final JMPlayer jmPlayer = new JMPlayer();
        //player command object, using this one for controlling player
        final MPlayerCommands cmdInterface = new MPlayerCommands(jmPlayer);
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        String serialInput = "";
        boolean runFlag = true;
        final ServerSocketManager serverThread = new ServerSocketManager(1111, 5, cmdInterface);

        //starting server thread -> handles server client relationships
        try {
            serverThread.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            runFlag = false;
        }

        /************************************************************************
         * PiFace Control and Display Handling
         ************************************************************************/


         /************************************************************************
         * Main Loop
         ************************************************************************/
        //main loop, will forward commands to player command handler, "quit" will stop the server
        while(runFlag && (serialInput = bufferRead.readLine()) != null){
            if(serialInput.startsWith("quit")){
                runFlag = false;
                continue;
            }
            else{
                System.out.println(cmdInterface.sendCommand(serialInput));
            }
        }

        /************************************************************************
         * Application Termination
         ************************************************************************/
        //before terminating application stop server threads
        serverThread.stopServer();
        //closing mplayer if active
        if(jmPlayer.isPlaying()){
            jmPlayer.close();
        }
        System.out.println("Quit Application.");
    }
}