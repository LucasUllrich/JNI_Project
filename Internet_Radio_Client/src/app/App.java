package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * <h1>Main Class of File Listing Client</h1>
 * Starts client socket and handles user IO.
 * 
 * @author Tobias Pistora
 * @version 1.1
 * @since 20.12.2019
 */
public class App {
    public static void main(String[] args) throws Exception {
        String ipAddress = "localhost";

        if(args.length > 0){
            ipAddress = args[0];
        }
        System.out.println("trying to connect with "+ipAddress+"...");

        try(
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket(ipAddress, 1111);
        PrintWriter clientSvr = new PrintWriter(clientSocket.getOutputStream(), true);
        ){
            ReadThread reader = new ReadThread(clientSocket);
            String serialInput = "";
            boolean runApp = true;

            //starting listen thread
            reader.start();

            //user input loop
            while(runApp){
                try{
                    serialInput = bufferRead.readLine();
                    //System.out.println("Command: "+serialInput);
                }
                catch(IOException e)
                {
                    reader.stopIt();
                    reader.join();
                    runApp = false;
                }
                if(serialInput.startsWith("quit")){
                    //stop app by user
                    reader.stopIt();
                    reader.join();
                    System.out.println("Quit Application.");
                    runApp = false;
                }
                else if(!reader.isAlive()){
                    System.out.println("Connection is lost -> quitting application.");
                    runApp = false;
                }
                else{
                    //send user input to server
                    clientSvr.println(serialInput);
                }
            }
            
        } catch (UnknownHostException e) {
            System.err.println("Unknown host.");
            System.exit(1);
        } catch (IOException e){
            System.err.println("No IO connection.");
            System.exit(1);
        }
    }
}