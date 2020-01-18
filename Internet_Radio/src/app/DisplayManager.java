package app;

public class DisplayManager extends Thread {
    static {
        System.loadLibrary("radio");
    }
    private boolean autoscroll;
    private String displayText1 = "";
    private String displayText2 = "";

    private native void sendText (String text);
    private native void setBacklightState (boolean state);      // LCD Backlight on/off
    private native void setCursorVisibility (boolean state);    // Cursor visibility on/off
    private native void clearScreen ();
    private native void setLcdState (boolean state);            // LCD on/off
    private native byte initLcd ();
    private native void autoscrollLcd (boolean state);
    private native void setCursourPosition (byte col, byte row);

    /**
     * @param autoscroll the autoscroll to set
     */
    public void setAutoscroll(boolean autoscroll) {
        this.autoscroll = autoscroll;
    }
    /**
     * Display text of the first line
     * @param displayText1 the displayText1 to set
     */
    public void setDisplayText1(String displayText1) {
        this.displayText1 = displayText1;
    }

    /**
     * Display text of the second line
     * @param displayText2 the displayText2 to set
     */
    public void setDisplayText2(String displayText2) {
        this.displayText2 = displayText2;
    }

    @Override
    public void run () {
        DisplayManager displayManager = new DisplayManager();
        displayManager.initLcd();
        displayManager.sendText("Long Testtexttextext");
        while(true) {
            try {
                
                displayManager.sendText(displayText1);
                displayManager.sendText(displayText2);


                Thread.sleep(100);
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }

}
