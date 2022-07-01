package Display;

import java.util.Timer;

/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 */
public class ScheduledVideoCaller implements Runnable{

    private int my_fps;
    DisplayImg my_displayImg;
    private String my_inputPath;

    //constructor
    /**
     * @param displayImg the instance of the class, so we can call all of its method without having to pass an image.
     * @param fps the frame rate defined by the user, which will determine the call rate of the function to update the Panel
     * @param inPath the path of the directory that contains all the images to display.
     *
     * Here we set those parameters as global variables, so we can use them in the runnable
     * The runnable is just a caller, which calls the function that we want to execute; in this case, the
     * ScheduledPlayVideo class that implements the TimerTask.
     */
    public ScheduledVideoCaller(DisplayImg displayImg, int fps, String inPath) {
        my_fps = fps; //set fps to control the frame rate
        my_displayImg = displayImg;
        my_inputPath = inPath;
    }

    @Override
    public void run() {
        System.out.println("Scheduler Starts!");
        Timer timer = new Timer();
        ScheduledPlayVideo spv = new ScheduledPlayVideo(my_displayImg, my_inputPath);
        //use scheduler to update the pictures according to the fps
        timer.scheduleAtFixedRate(spv,0,(long)((double)1000/my_fps));
    }
}

