package app;

public class ButtonManager extends Thread {
    static {
        System.loadLibrary("radio");
    }
    private native int getButtonStates ();


    @Override
    public void run () {

    }
}