package app;

public class DisplayManager extends Thread {
    static {
        System.loadLibrary("radio");
    }

    private native void sendText (String text);
    private native void setBacklightState (boolean state);      // LCD Backlight on/off
    private native void setCursorVisibility (boolean state);    // Cursor visibility on/off
    private native void clearScreen ();
    private native void setLcdState (boolean state);            // LCD on/off
    private native byte initLcd ();

    @Override
    public void run () {
        DisplayManager displayManager = new DisplayManager();
        displayManager.initLcd();
        displayManager.sendText("Test");
        while(true) {
            try {
                
                Thread.sleep(100);
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }

}
