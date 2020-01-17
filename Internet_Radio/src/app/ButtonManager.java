package app;

public class ButtonManager extends Thread {
    static {
        System.loadLibrary("radio");
    }
    private native byte getButtonStates ();


    @Override
    public void run () {
        while(true) {
            try {
                
                Thread.sleep(100);
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }
}