package Display;

/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 * This class is from the previous submission, and is no longer in use.
 */
public class FrameRateControl extends Thread{
    /*
    This is supposed to be used as a frame rate control, to control the speed.
     */
    private long milli_seconds;

    /**
     * @param millis, the fps given by the user in but in milliseconds
     *
     * Using the fps, we sleep the thread to slow down the execution.
     */
    public FrameRateControl(long millis){
        this.milli_seconds = millis;
    }

    @Override
    public void run() {
        try {
            sleep(this.milli_seconds); //wait for this.waitInMs miliseconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
