package Display;
import FileManagement.JPEG_Handler;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 */
public class ScheduledPlayVideo extends TimerTask {

    int count = 0;
    String my_inPath;
    DisplayImg displayImg;

    //constructor
    /**
     * @param displayImg the instance of the class, so we can call all of its method without having to pass an image.
     * @param inPath the path of the directory that contains all the images to display.
     *
     * Since a method was already implemented in DisplayImg to update the Panel, we just call that method.
     * We use a "count" variable to keep track of which image we want to display since count works as an
     * index that corresponds to an image contained in the given directory.
     */
    public ScheduledPlayVideo(DisplayImg displayImg, String inPath) {
        this.displayImg = displayImg;
        this.my_inPath = inPath;
    }

    @Override
    public void run() {
        //Since this is called by the scheduler, we can call the playVideo2 functions, which updates the
        //video image by adding and removing images to the Panel in DisplayImg
        displayImg.playVideo2(this.my_inPath, this.count);
        count++;
    }
}
