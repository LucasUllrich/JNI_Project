package app;

public class HardwareHandler extends Thread {

    @Override
    public void run () {
        ButtonManager buttonManager = new ButtonManager();
        DisplayManager displayManager = new DisplayManager();
    }
}