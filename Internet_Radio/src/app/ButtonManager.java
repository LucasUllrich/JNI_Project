package app;

public class ButtonManager extends Thread {
    static {
        System.loadLibrary("radio");
    }
    private native byte getButtonStates ();


    @Override
    public void run () {

    }
}