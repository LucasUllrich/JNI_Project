package app;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * A player which is actually an interface to the famous MPlayer.
 * Tested with MPlayer 1.4-7.
 * If MPlayer is not found check if it is installed and change mplayerPath variable.
 * 
 * @author Adrian BER, adaptions and changes by Tobias Pistora
 * 
 * source of original: https://beradrian.wordpress.com/2008/01/30/jmplayer/
 */
public class JMPlayer {
    private static Logger logger = Logger.getLogger(JMPlayer.class.getName());

    /**
     * A thread that reads from an input stream and outputs to another line by line.
     */
    private static class LineRedirecter extends Thread {
        /** The input stream to read from. */
        private InputStream in;
        /** The output stream to write to. */
        private OutputStream out;
        /** The prefix used to prefix the lines when outputting to the logger. */
        private String prefix;

        /**
         * @param in     the input stream to read from.
         * @param out    the output stream to write to.
         * @param prefix the prefix used to prefix the lines when outputting to the
         *               logger.
         */
        LineRedirecter(InputStream in, OutputStream out, String prefix) {
            this.in = in;
            this.out = out;
            this.prefix = prefix;
        }

        public void run() {
            try {
                // creates the decorating reader and writer
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                PrintStream printStream = new PrintStream(out);
                String line;

                // logger.info("Created Line Redirector with prefix [" + prefix + "]");
                // read line by line
                while ((line = reader.readLine()) != null) {
                    //logger.info((prefix != null ? prefix : "") + line);
                    printStream.println(line);
                    //System.out.println("Bytes in Pipe: "+in.available());
                }
                // logger.log(Level.INFO, "Stopping Line Redirecter Thread.");
            } catch (IOException exc) {
                logger.log(Level.WARNING,
                        "An error has occurred while grabbing lines at Redirector with prefix [" + prefix + "]", exc);
            }
        }

    }

    /** The path to the MPlayer executable. */
    private String mplayerPath = "/usr/local/bin/mplayer";
    /** Options passed to MPlayer. */
    private String mplayerOptions = " -slave -idle";

    /** The process corresponding to MPlayer. */
    private Process mplayerProcess;
    /** The standard input for MPlayer where you can send commands. */
    private PrintStream mplayerIn;
    /**
     * A combined reader for the the standard output and error of MPlayer. Used to
     * read MPlayer responses.
     */
    private BufferedReader mplayerOutErr;
    //thread for clearing pipe while information is not needed
    private PipeDumper pipeDumper = null;

    private Map<String, String> playerInfo = new TreeMap<String, String>();
    private ArrayList<String> fileInfo = new ArrayList<String>();

    private int queueCapacity = 1;
    private Queue<String> playerInfoQueue;

    public JMPlayer() {
        this.playerInfoQueue = new LinkedBlockingQueue<String>(this.queueCapacity);
        this.initResetPlayerInfo();
    }

    public Queue<String> getPlayerInfoQueue() {
        return this.playerInfoQueue;
    }

    public String forceInfoStreamUpdate() {
        return this.writePlayerInfoToStream();
    }

    /** @return the path to the MPlayer executable. */
    public String getMPlayerPath() {
        return mplayerPath;
    }

    /**
     * Sets the path to the MPlayer executable.
     * 
     * @implNote not used so far
     * @param mplayerPath the new MPlayer path; this will be actually effective after
     *                    {@link #close() closing} the currently running player.
     */
    public void setMPlayerPath(String mplayerPath) {
        this.mplayerPath = mplayerPath;
    }

    /**
     * Starts MPlayer with given file path.
     * 
     * @param file audio file to play.
     * @return Status message.
     * @throws IOException
     */
    public String open(File file) throws IOException {
        String path = file.getAbsolutePath().replace('\\', '/');
        String retStr = "Unknown Error";

        if (!file.exists()) {
            // logger.info("Cannot find file (" + path + "), ignoring this request.");
            return "Cannot find file (" + path + "), ignoring this request.";
        }

        if (this.mplayerProcess != null) {
            this.close();
        }
        retStr = this.startMplayer(path);
        if(!retStr.isEmpty()){
            return retStr;
        }
        // wait to start playing
        // logger.info("Trying to play file " + path);
        String response = waitForAnswer("Starting playback...", 5000, "File");
        if (response == null || response.startsWith("Error")) {
            this.close();
            // logger.info("Cannot execute file (" + path + "), stopping player.
            // "+response);
            return "Cannot execute file (" + path + "), stopping player. " + response;
        }
        // System.out.println("File Info:\n"+this.fileInfo.toString());
        this.updatePlayerInfoOnOpen(path);
        return "Starting new audio stream from " + path + ".";
    }

    /**
     * Start MPlayer with given URL.
     * 
     * @param urlObj URL to web radio stream.
     * @return Status message.
     * @throws IOException
     */
    public String open(URL urlObj) throws IOException {
        String path = urlObj.toString();
        System.out.println("Requested path = " + path);
        InputStream connectionTest = null;
        String retStr = "Unknown Error";
        try {
            connectionTest = urlObj.openStream();
        } catch (IOException e) {
            // logger.info("Cannot connect to target URL (" + path + "), ignoring this
            // request.");
            return "Cannot connect to target URL (" + path + "), ignoring this request.";
        }
        connectionTest.close();

        if (this.mplayerProcess != null) {
            this.close();
        }
        retStr = this.startMplayer(path);
        if(!retStr.isEmpty()){
            return retStr;
        }

        // wait to start playing
        // logger.info("Trying to play file fom url" + path);
        String response = waitForAnswer("Starting playback...", 5000, "URL");
        if (response == null || response.startsWith("Error")) {
            this.close();
            // logger.info("Cannot execute URL (" + path + "), stopping player. "+response);
            return "Cannot execute URL (" + path + "), stopping player. " + response;
        }
        // System.out.println("File Info:\n"+this.fileInfo.toString());
        this.updatePlayerInfoOnOpen(path);
        return "Starting new audio stream from " + path + ".";
    }

    /**
     * Handles MPlayer start and communication re-direction.
     * 
     * @param path file path / URL to execute.
     * @return  Status message.
     */
    private String startMplayer(String path) {
        String retStr = "";

        // start MPlayer as an external process
        String command = mplayerPath + mplayerOptions + " " + path;
        // logger.info("Starting MPlayer process: " + command);
        try {
            this.mplayerProcess = Runtime.getRuntime().exec(command);
        } catch (IOException e1) {
            retStr = "Cannot start MPlayer process!";
            return retStr;
            //e1.printStackTrace();
        }

        // create the piped streams where to redirect the standard output and error of
        // MPlayer
        // specify a bigger pipesize
        PipedOutputStream writeTo;

        try {
            PipedInputStream readFrom = new PipedInputStream(2048 * 2048);
            writeTo = new PipedOutputStream(readFrom);
            this.mplayerOutErr = new BufferedReader(new InputStreamReader(readFrom));
            this.pipeDumper = new PipeDumper(readFrom);

            // create the threads to redirect the standard output and error of MPlayer
            new LineRedirecter(mplayerProcess.getInputStream(), writeTo, "MPlayer says: ").start();
            new LineRedirecter(mplayerProcess.getErrorStream(), writeTo, "MPlayer encountered an error: ").start();
            // the standard input of MPlayer
            this.mplayerIn = new PrintStream(mplayerProcess.getOutputStream());
            this.pipeDumper.start();

        } catch (IOException e) {
            this.close();
            retStr = "Cannot establish communication with MPlayer!";
            return retStr;
            //e.printStackTrace();
        }

        return retStr;
    }

    /**
     * When opening a new audio stream the local player data shall be updated
     * 
     * @param path path to file or URL of started audio stream
     */
    private void updatePlayerInfoOnOpen(String path){
        this.setPlayerInfoParam("path", path);
        this.setPlayerInfoParam("isPlaying", "true");
        this.getVolume();
        this.writePlayerInfoToStream();
    }

    /**
     * Closes currently running MPlayer and stops its data re-direction thread
     */
    public void close() {
        if (mplayerProcess != null) {
            execute("quit");
            this.setPlayerInfoParam("isPlaying", "false");
            try {
                mplayerProcess.waitFor(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.info("Timeout while waiting for mplayer process.");
            }
            mplayerProcess = null;
            try {
                this.mplayerOutErr.close();
            } catch (IOException e) {
                // e.printStackTrace();
                logger.info("Cannot close mplayer com.");
            }
        }
        if(pipeDumper.isAlive()){
            pipeDumper.stopIt();
        }
        this.writePlayerInfoToStream();
    }

    /**
     * Get path / URL
     * @return path / URL of currently played audio stream
     */
    public File getPlayingFile() {
        String path = getProperty("path");
        return path == null ? null : new File(path);
    }

    /**
     * Pausing / Continuing current audio Stream.
     * If current stream is an URL player will be closed.
     * 
     * @return Status message.
     */
    public String togglePlay() {
        String retStr = "Unknown Error";
        if (this.playerInfo.get("path").startsWith("http")) {
            if (this.isPlaying()) {
                this.close();
                retStr = "Stopped URL stream.";
            } else {
                try {
                    retStr = this.open(new URL(this.playerInfo.get("path")));
                } catch (MalformedURLException e) {
                    retStr = e.toString();
                } catch (IOException e) {
                    retStr = e.toString();
                }
            }
        }
        else if(this.playerInfo.get("path").isEmpty()){
            retStr = "Cannot continue without file path / URL.";
        }
        else{
            execute("pause");
            if(this.playerInfo.get("isPlaying").startsWith("true")){
                this.setPlayerInfoParam("isPlaying", "false");
                retStr = "Paused playing file.";
            }
            else{
                this.setPlayerInfoParam("isPlaying", "true");
                retStr = "Continuing playing.";
            }
        }
        this.writePlayerInfoToStream();
        return retStr;
    }

    /**
     * Get status of MPlayer.
     * @return player is playing or not...
     */
    public boolean isPlaying() {
        if(mplayerProcess == null){this.setPlayerInfoParam("isPlaying", "false");}
        this.writePlayerInfoToStream();
        return mplayerProcess != null;
    }

    public long getTimePosition() {
        return getPropertyAsLong("time_pos");
    }

    public void setTimePosition(long seconds) {
        setProperty("time_pos", seconds);
    }

    public long getTotalTime() {
        return getPropertyAsLong("length");
    }

    /**
     * Get player volume, updates local player info data.
     * 
     * @return Current volume value.
     */
    public float getVolume() {
        float vol = 0.0f;
        vol = getPropertyAsFloat("volume");
        this.setPlayerInfoParam("volume", String.valueOf(vol));
        this.writePlayerInfoToStream();
        return vol;
    }

    /**
     * Set new volume.
     * 
     * @param volume new volume value (0 - 100).
     */
    public void setVolume(float volume) {
        if(volume < 0.0f){volume = 0.0f;}
        if(volume > 100.0f){volume = 100.0f;}
        setProperty("volume", volume);
        this.getVolume();
    }

    protected String getProperty(String name) {
        if (name == null || mplayerProcess == null) {
            return null;
        }
        String s = "ANS_" + name + "=";
        String x = execute("get_property " + name, s);
        if (x == null)
            return null;
        if (!x.startsWith(s))
            return null;
        return x.substring(s.length());
    }

    protected long getPropertyAsLong(String name) {
        try {
            return Long.parseLong(getProperty(name));
        }
        catch (NumberFormatException exc) {}
        catch (NullPointerException exc) {}
        return 0;
    }

    protected float getPropertyAsFloat(String name) {
        try {
            return Float.parseFloat(getProperty(name));
        }
        catch (NumberFormatException exc) {}
        catch (NullPointerException exc) {}
        return 0f;
    }

    protected void setProperty(String name, String value) {
        execute("set_property " + name + " " + value);
    }

    protected void setProperty(String name, long value) {
        execute("set_property " + name + " " + value);
    }

    protected void setProperty(String name, float value) {
        execute("set_property " + name + " " + value);
    }

    /**
     * Initialize local player info variable.
     */
    private void initResetPlayerInfo(){
        this.playerInfo.put("path", "");
        this.playerInfo.put("volume", "0.0");
        this.playerInfo.put("isPlaying", "false");
        this.writePlayerInfoToStream();
    }

    /**
     * Sets a parameter of local player info variable.
     * 
     * @param param parameter
     * @param value parameters value
     */
    private void setPlayerInfoParam(String param, String value){
        if(this.playerInfo.containsKey(param)){
            this.playerInfo.put(param, value);
        }
    }

    /**
     * When this Method gets called the local player and file info variables
     * get converted into a info string and sent to player inf queue.
     * This queue is provided by parent process.
     * 
     * @return player info string
     */
    private String writePlayerInfoToStream(){
        String streamString = "";
        
        if(this.playerInfo != null){
            Set<Map.Entry<String,String>> localSet = this.playerInfo.entrySet();
            streamString += "<PlayerInfo>|>";
            for(Map.Entry<String,String> entry:localSet){
                streamString += entry.getKey() + ": " + entry.getValue() + "|>";
            }
        }
        if(this.fileInfo != null){
            for (int i = 0; i<this.fileInfo.size(); i++) {
                streamString += this.fileInfo.get(i) + "|>";
            }
        }
        //if old data string is remaining in queue, remove it
        if(this.playerInfoQueue.size() > 0){
            this.playerInfoQueue.remove();
        }
        //add new data string to queue
        this.playerInfoQueue.add(streamString);
        //logger.info(playerInfoQueue.peek());
        //return player data
        return streamString;
    }

    /** Sends a command to MPlayer..
     * @param command the command to be sent
     */
    private void execute(String command) {
        execute(command, null);
    }

    /** Sends a command to MPlayer and waits for an answer.
     * @param command the command to be sent
     * @param expected the string with which has to start the line; if null don't wait for an answer
     * @return the MPlayer answer
     */
    private String execute(String command, String expected) {
        if (mplayerProcess != null) {
            //logger.info("Send to MPlayer the command \"" + command + "\" and expecting "
            //        + (expected != null ? "\"" + expected + "\"" : "no answer"));
            mplayerIn.print(command);
            mplayerIn.print("\n");
            mplayerIn.flush();
            //logger.info("Command sent");
            if (expected != null) {
                String response = waitForAnswer(expected, 5000, null);
                //logger.info("MPlayer command response: " + response);
                return response;
            }
        }
        return null;
    }

    /** Read from the MPlayer standard output and error a line that starts with the given parameter and return it.
     * @param expected the expected starting string for the line
     * @return the entire line from the standard output or error of MPlayer
     */
    private String waitForAnswer(String expected, long timeout, String fileInfoType) {
        // todo add the possibility to specify more options to be specified
        // todo use regexp matching instead of the beginning of a string
        String line = "No answer from player.";
        String retStr = "Error: Timeout while waiting for answer!";
        long endTime = System.currentTimeMillis() + timeout;
        boolean answerFound = false;
        ArrayList<String> locFileInfo = new ArrayList<String>();
        if (expected != null) {
            this.pipeDumper.switchDumpAllowed(false);
            try {
                while (System.currentTimeMillis() < endTime) {
                    if(mplayerOutErr.ready()){
                        line = mplayerOutErr.readLine();
                        if(line == null){
                            retStr = "Error: Answer not found.";
                            break;
                        }
                        //logger.info("Reading line: " + line);
                        //adding file info provided by mplayer to local list
                        if(fileInfoType != null){
                            if(line.contains("Name") || line.contains("Genre") || line.contains("Website") || line.contains("Title")
                            || line.contains("Artist") || line.contains("Year") || line.contains("Album") || line.contains("Comment") ){
                                locFileInfo.add(line);
                            }
                        }
                        if (line.startsWith(expected)) {
                            retStr = line;
                            answerFound = true;
                            break;
                        }
                    }
                }
                this.pipeDumper.switchDumpAllowed(true);
            }
            catch (IOException e) {
                System.out.println("Error at wait for answer.");
                retStr = "Error: Communication with player failed!";
                return retStr;
            }
            catch (Exception e){
                System.out.println(" Unknown error at wait for answer.");
                retStr = "Error: Communication with player failed, unknown error!";
                return retStr;
            }
        }
        //if file/url got started successfully, renew file info of this session
        if(answerFound && fileInfoType != null){
            this.fileInfo.clear();
            this.fileInfo.add("Type   : "+fileInfoType);
            //copying local list to session list
            locFileInfo.forEach(elm->this.fileInfo.add(elm));
        }
        return retStr;
    }

    private class PipeDumper extends Thread{
        private volatile boolean runFlag = true;
        private volatile boolean dumpingIsAllowed = false;
        private PipedInputStream dumpPipe;

        public PipeDumper(PipedInputStream pipeToDump){
            this.dumpPipe = pipeToDump;
        }

        public void stopIt(){
            this.runFlag = false;
        }

        public void switchDumpAllowed(boolean newState){
            this.dumpingIsAllowed = newState;
        }

        //public boolean isDumpAllowed(){
        //    return this.dumpingIsAllowed;
        //}

        @Override
        public void run(){
            BufferedReader pipeReader = new BufferedReader(new InputStreamReader(dumpPipe));
            String lineToDump = "";
            //this.dumpingIsAllowed = true;

            System.out.println("Starting pipe dumper thread.");
            while(runFlag){
                try {
                    if(pipeReader.ready() && this.dumpingIsAllowed){
                        if((lineToDump = pipeReader.readLine()) != null){
                            if(!dumpingIsAllowed){
                                System.out.println("Dumping line: " + lineToDump);
                            }
                        }
                        else{
                            this.stopIt();
                        }
                    }
                } catch (IOException e) {
                    this.stopIt();
                }
            }
            System.out.println("Stopping pipe dumper thread.");
        }
    }
}