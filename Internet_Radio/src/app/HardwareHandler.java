package app;

public class HardwareHandler extends Thread {

    MPlayerCommands cmdInterface = null;

    HardwareHandler (MPlayerCommands cmdInterface) {
        this.cmdInterface = cmdInterface;
    }

    @Override
    public void run () {
        ButtonManager buttonManager = new ButtonManager();
        DisplayManager displayManager = new DisplayManager();
        JMPlayer player = cmdInterface.getPlayerObject();
        int buttonState = 0;

        buttonManager.start();
        displayManager.start();

        while(true) {
            buttonState = buttonManager.getButtonPressed();

            System.out.println("Info: " cmdInterface.getPlayerInfoQueue());
            
            switch (buttonState) {
                case 1:
                    
                    break;

                case 2:
                    
                    break;

                case 3:
                    
                    break;

                case 4:
                    
                    break;

                case 5:
                    
                    break;
                    
                case 6:
                    player.setVolume(0);
                    break;

                case 7:
                    player.setVolume(player.getVolume() - 5);
                    break;

                case 8:
                    player.setVolume(player.getVolume() + 5);
                    break;

                default:
                    break;
            }

            try {
                Thread.sleep(500);
                
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }
}
