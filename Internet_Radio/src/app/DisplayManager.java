package app;

/**
 * Handler thread to display the information on the screen
 */
public class DisplayManager extends Thread {
    static {
        System.loadLibrary("radio");
    }
    private String displayText1 = "";
    private String displayText2 = "";
    private String displayText1Buffer = "";
    private String displayText2Buffer = "";

    private native void sendText (String text);
    private native void setBacklightState (boolean state);      // LCD Backlight on/off
    private native void setCursorVisibility (boolean state);    // Cursor visibility on/off
    private native void clearScreen ();
    private native void setLcdState (boolean state);            // LCD on/off
    private native byte initLcd ();
    private native void autoscrollLcd (boolean state);
    private native void setCursourPosition (int col, int row);

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

    /**
     * Thread to periodically update the display and make all the information readable by scrolling
     */
    @Override
    public void run () {
        DisplayManager displayManager = new DisplayManager();
        int line1TextPos = 0;
        int line2TextPos = 0;
        displayManager.initLcd();
        // displayManager.sendText("Long Testtexttextext");
        while(true) {
            // Reset moving line routine if the string changed to counteract an index out of range
            if (displayText1Buffer.compareTo(displayText1) != 0) {
                displayText1Buffer = displayText1;
                line1TextPos = 0;
            }
            if (displayText2Buffer.compareTo(displayText2) != 0) {
                displayText2Buffer = displayText2;
                line1TextPos = 0;
                line2TextPos = 0;
            }

            /**
             * Rotate the Message on the screen to make more than 16 characters vissible
             */
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


            /**
             * At least 8 characters stay visible on the screen, as soon as both lines
             * are scrolled to the we jump back to the start
             */
            if (line1TextPos < (displayText1.length() - 8)) {
                line1TextPos++;
            }
            if (line2TextPos < (displayText2.length() - 8)) {
                line2TextPos++;
            }
            if ((line1TextPos > (displayText1.length() - 9)) &&
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
