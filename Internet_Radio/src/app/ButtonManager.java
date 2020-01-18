package app;

public class ButtonManager extends Thread {
    static {
        System.loadLibrary("radio");
    }
    private byte buttonPressed;
    private byte buttonStates;

    private native byte getButtonStates ();

    /**
     * @return the buttonPressed
     */
    public byte getButtonPressed() {
        return buttonPressed;
    }

    @Override
    public void run () {
        ButtonManager buttonManager = new ButtonManager();
        while(true) {
            // Debounce
            if (buttonStates != buttonManager.getButtonStates()) {  // Change detected
                buttonStates = buttonManager.getButtonStates();     // Store change
            } else {
                // If there are no changes between cycles the last stored state indicates pressed buttons
                buttonPressed = buttonStates;
            }

            try {    
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("ButtonManager could not enter sleep state");
            }
        }
    }
}