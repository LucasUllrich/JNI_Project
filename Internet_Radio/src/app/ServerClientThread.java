package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <h1>File Listing Server Communication Thread</h1>
 * Handles communication between client and server.
 * 
 * @author Tobias Pistora
 * @version 1.0
 * @since 20.12.2019
 */
public class ServerClientThread extends Thread {
    private volatile boolean runFlag = true;
    private String threadName = "unnamed_server_client_Thread";
    private Socket comSocket = null;
    private MPlayerCommands cmdObject;

    public ServerClientThread(String threadName, Socket clientSocket, MPlayerCommands cmdObj) {
        this.threadName = threadName;
        this.comSocket = clientSocket;
        this.cmdObject = cmdObj;
    }

    /**
     * Method to stop the infinite thread loop.
     */
    public void stopIt() {
        if(!comSocket.isClosed()){
            try {
                comSocket.close();
            } catch (IOException e) {
                System.out.println("Can not close socket connection of "+this.threadName);
                e.printStackTrace();
            }
        }
        runFlag = false;
    }

    /**
     * This method returns the label of the thread (set in constructor)
     * 
     * @return thread label
     */
    public String getThreadLabel() {
        return this.threadName;
    }

    /**
     * Method that starts the infinite loop for server client communication.
     */
    @Override
    public void run() {
        String error = "No error occurred.";
        String previousPlayerInfo = "";
        String currentPlayerInfo = "";
        String clientRequest = "";
        PrintWriter comOut = null;
        Queue<String> playerInfo = null;
        Queue<String> clientReadQueue = new LinkedBlockingQueue<String>(20);
        ClientReaderThread readThread = new ClientReaderThread(this.threadName, this.comSocket, clientReadQueue);

        //start com IO
        try {
            comOut = new PrintWriter(comSocket.getOutputStream(), true);
            playerInfo = cmdObject.getPlayerInfoQueue();
            readThread.start();
        } catch (IOException e) {
            this.runFlag = false;
            error = "Cannot establish com connection.";
            e.printStackTrace();
        }
        System.out.println("Started thread " + threadName);

        while (runFlag) {

            if(readThread.isAlive()){
                if((clientRequest = clientReadQueue.poll()) != null){
                    comOut.println(cmdObject.sendCommand(clientRequest));
                }
            }
            else{
                this.stopIt();
                continue;
            }
            
            //get player info from queue without deleting queue content
            currentPlayerInfo = playerInfo.peek();
            if(currentPlayerInfo != null){
                //check if info changed
                if(!currentPlayerInfo.equals(previousPlayerInfo)){
                    //if info changed send it to client and copy it to old info
                    comOut.println(currentPlayerInfo);
                    previousPlayerInfo = String.valueOf(currentPlayerInfo);
                    //System.out.println("Received new player info: "+currentPlayerInfo);
                }
            }
            else{System.out.println("currentPlayerInfo is null");}
        }
        System.out.println("Stopping thread " + threadName + ", " + error);
    }

    /**
     * Private thread class that waits for client input and forwards it to parent for processing.
     * This thread will be terminated as soon as it cannot read from given socket. Furthermore
     * socket.close() will interrupt the blocking read method and stop the thread immediately.
     * Interrupting this thread has no effect, stopIt() will wait until something is sent by
     * client until it terminates the thread.
     */
    private class ClientReaderThread extends Thread{
        private volatile boolean runFlag = true;
        private Socket comSocket;
        private Queue<String> readQueue;
        private String threadName = "unnamed_server_client_Thread_reader";

        public ClientReaderThread(String threadName, Socket comSocket, Queue<String> readQueue){
            this.threadName = threadName + "_reader";
            this.comSocket = comSocket;
            this.readQueue = readQueue;
        }

        /**
         * Method to stop the infinite thread loop.
         */
        public void stopIt() {
            runFlag = false;
        }

        /**
         * Method that starts the infinite loop for client to server communication.
         */
        @Override
        public void run(){
            BufferedReader comIn = null;
            String error = "No error occurred.";
            String requestLine = "";

            try {
                comIn = new BufferedReader(new InputStreamReader(comSocket.getInputStream()));
            } catch (IOException e) {
                this.runFlag = false;
                error = "Cannot establish com connection at read.";
                e.printStackTrace();
            }

            System.out.println("Started thread " + threadName);
            while(runFlag){
                //waiting for client input
                try {
                    requestLine = comIn.readLine();
                    //client sent command -> putting request into queue to process it in parent thread
                    if(requestLine != null){
                        readQueue.add(requestLine);
                    }
                    else{
                        //client closed connection
                        this.stopIt();
                        error = "Connection closed by client.";
                    }
                } catch (IOException e) {
                    this.stopIt();
                    error = "Connection closed in read.";
                    //e.printStackTrace();
                    continue;
                }
            }
            System.out.println("Stopping thread " + threadName + ", " + error);
        }
    }
}