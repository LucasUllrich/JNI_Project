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
    private native void setCursourPosition (int col, int row);

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
        int line1TextPos = 0;
        int line2TextPos = 0;
        displayManager.initLcd();
        // displayManager.sendText("Long Testtexttextext");
        while(true) {
            displayManager.clearScreen();
            displayManager.setCursourPosition(0, 0);
            displayManager.sendText(displayText1.substring(line1TextPos));
            displayManager.setCursourPosition(0, 1);
            displayManager.sendText(displayText2.substring(line2TextPos));
            
            // Special case if the line is back at the start, make a longer delay
            if (line1TextPos == 0) {
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    System.out.println("DisplayManager could not enter sleep state");
                }
            }


            if (line1TextPos < (displayText1.length() - 8)) {
                line1TextPos++;
            }
            if (line2TextPos < (displayText2.length() - 8)) {
                line2TextPos++;
            }
            else if ((line1TextPos > (displayText1.length() - 9)) &&
                    (line2TextPos > (displayText2.length() - 9))) {
                line1TextPos = 0;
                line2TextPos = 0;
            }


            try {
                Thread.sleep(200);
            } catch (Exception e) {
                System.out.println("DisplayManager could not enter sleep state");
            }
        }
    }

}
