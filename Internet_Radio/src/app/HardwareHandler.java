package app;

public class HardwareHandler extends Thread {

    @Override
    public void run () {
        ButtonManager buttonManager = new ButtonManager();
        DisplayManager displayManager = new DisplayManager();
        buttonManager.start();
        displayManager.start();

        while(true) {
            System.out.println("Button state: " + buttonManager.getButtonPressed());
            
            try {
                Thread.sleep(100);
                
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }
}
