package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * <h1>File Listing Server Socket Handling</h1>
 * Handles client connection requests.
 * 
 * @author Tobias Pistora
 * @version 1.0
 * @since 20.12.2019
 */
public class ServerSocketManager extends Thread {
    private ServerSocket daSocket = null;
    private int port = 1111;
    private int maxAllowedConnections = 10;
    private ArrayList<ServerClientThread> connectedClients = new ArrayList<ServerClientThread>();
    private volatile boolean runFlag = true;
    private MPlayerCommands cmdObject;

    public ServerSocketManager(int serverPort, int maxConnections, MPlayerCommands cmdObj) {
        if (serverPort < 0 || serverPort > 65535) {
            throw new IllegalArgumentException("Out of port range. Allowed ports are 0 - 65535.");
        }
        if (maxConnections < 1) {
            throw new IllegalArgumentException("maxConnections must be greater than 0.");
        }
        this.port = serverPort;
        this.maxAllowedConnections = maxConnections;
        this.cmdObject = cmdObj;
    }

    public ServerSocketManager(int serverPort) {
        if (serverPort < 0 || serverPort > 65535) {
            throw new IllegalArgumentException("Out of port range. Allowed ports are 0 - 65535.");
        }
        this.port = serverPort;
    }

    /**
     * Method to stop the infinite thread loop if it is still running.
     */
    public void stopServer() {
        if(connectedClients != null){
            for (ServerClientThread clientThread : this.connectedClients) {
                if(clientThread.isAlive()){
                    clientThread.stopIt();
                    clientThread.interrupt();
                    try {
                        clientThread.join(5000);
                    } catch (InterruptedException e) {
                        System.out.println("Was not able to terminate "+clientThread.getThreadLabel()+" within 5sec.");
                        e.printStackTrace();
                    }
                }
            }
        }
        if(daSocket != null){
            try {
                daSocket.close();
            } catch (IOException e) {
                System.out.println("Can not close server socket.");
                e.printStackTrace();
            }
        }
        runFlag = false;
    }

    @Override
    public void run(){
        //create server socket
        try {
            this.daSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            System.out.println("Can not create server socket.");
            runFlag = false;
        }

        while(runFlag){
            Socket newConnection = null;

            //waiting for new connection
            try {
                newConnection = this.daSocket.accept();
            } catch (IOException e) {
                if(daSocket != null && !daSocket.isClosed()){
                    System.out.println("Connection accept error.");
                    e.printStackTrace();
                    stopServer();
                }
                continue;
            }

            //if there were no connected clients before create first one and add it to list
            if(this.connectedClients.size() == 0){
                ServerClientThread newClientThread = new ServerClientThread("thread_0",  newConnection, cmdObject);
                this.connectedClients.add(newClientThread);
                this.connectedClients.get(0).start();
            }
            else{
                boolean usedClosedClientThread = false;
                //going through list of connected clients to see if a connection got closed
                for (ServerClientThread clientThread : this.connectedClients) {
                    //if closed connection found (thread should terminate when connection gets closed) use this list place for new one
                    if(!clientThread.isAlive()){
                        ServerClientThread newClientThread = new ServerClientThread("thread_"+this.connectedClients.indexOf(clientThread),  newConnection, cmdObject);
                        connectedClients.remove(clientThread);
                        this.connectedClients.add(newClientThread);
                        this.connectedClients.get(connectedClients.size()-1).start();
                        usedClosedClientThread = true;
                        break;
                    }
                }
                //if all current connections are still active check if a new one is allowed (< connection limit)
                if(!usedClosedClientThread){
                    //if a new connection is allowed create it and add to connection list
                    if(this.connectedClients.size() < this.maxAllowedConnections){
                        ServerClientThread newClientThread = new ServerClientThread("thread_"+this.connectedClients.size(),  newConnection, cmdObject);
                        this.connectedClients.add(newClientThread);
                        this.connectedClients.get(connectedClients.size()-1).start();
                    }
                    else{
                        //if connection limit is reached close the new one
                        try {
                            newConnection.close();
                        } catch (IOException e) {
                            System.out.println("Can not close new connection.");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
}