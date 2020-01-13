package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * <h1>File Listing Client Reader Thread</h1>
 * Thread to read and show server to client communication.
 * 
 * @author Tobias Pistora
 * @version 1.1
 * @since 20.12.2019
 */
public class ReadThread extends Thread {
    private Socket comSocket = null;
    boolean runFlag = true;
    
    public ReadThread(Socket clientSocket) {
        this.comSocket = clientSocket;
    }

    /**
     * Method to stop the infinite thread loop ant to close given IO socket.
     */
    public void stopIt() {
        if(!comSocket.isClosed()){
            try {
                comSocket.close();
            } catch (IOException e) {
                System.out.println("Can not close socket connection");
                e.printStackTrace();
            }
        }
        runFlag = false;
    }

    /**
     * Listen to Server Output until thread gets closed.
     */
    @Override
    public void run(){
        String inputText = null;

        try(BufferedReader srvClient = new BufferedReader(new InputStreamReader(comSocket.getInputStream()));
        ) {
            while(runFlag && (inputText = srvClient.readLine()) != null){
                //converting info stream into better readable format
                if(inputText.startsWith("<PlayerInfo>|>")){
                    inputText = inputText.replace("|>", "\n");
                }
                System.out.println(inputText);   
            }
            System.out.println("Connection closed.");
        } catch (IOException e) {
            if(runFlag){
                System.err.println("No IO connection.");
                runFlag = false;
            }
        }
    }
}