package Display;

import FileManagement.JPEG_Handler;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.util.TimerTask;
/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 */
public class DisplayImg extends JFrame{
    private String filter;
    private Boolean isFilter;

    //constructor
    /**
     * @param img, an image to create the panel with
     * @param filter, a filter to apply to such image (optional).
     *
     * Using the dimensions of the image, we set the size of the window accordingly, and we call the
     * create panel function. We also set whether a filter will be used or not.
     */
    public DisplayImg(BufferedImage img, String filter){ //which will be our window, the panel will then go ON the window
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight() + 35; //to make up for some space

        setSize(imgWidth,imgHeight); //use the image size for the window
        setTitle("Project Window");
        setLocationRelativeTo(null); //set it to the center

        if(filter.equals("null")){
            isFilter = false;
        }else{
            this.filter = filter;
            isFilter = true;
        }
        //System.out.println("creating panel...");
        createPanel(img);
        //setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * @param img, the image we'll add to the panel
     *
     * Here we add the Panel to the window, and the components to it, which in this case is only the image.
     * Since the video will be played later in the program, we remove the initial image in the panel.
     */
    private void createPanel(BufferedImage img){
        JPanel panel = new JPanel();
        this.getContentPane().add(panel); //put panel on the window
        //add components

        JLabel image = new JLabel(new ImageIcon(img)); //make the image a swing component, so we can add it to the panel
        //changeColor(img);
        add(image);
        remove(image);
        //changeColor(img); //it doesn't matter if we change it before or after we add the image
    }

    /**
     * @param inPath, the directory that contains all of the images
     * @param fps, frame rate which will determine the video speed.
     *
     * This function is no longer in used since it is an older version. This version uses a Thread Sleep to handle
     * the frame rate, using the FrameRateControl class, which is not correct since is not in sync with the actual
     * computing time. The frame rate would also be limited to the speed of the for loop,
     * so it can only really slow down, not speed up.
     */
    public void playVideo(String inPath, int fps){
        //this function was used before, when we didn't have the Scheduler. Now its not in use
        JPEG_Handler jpeg_handler = new JPEG_Handler();
        File inputFile = new File(inPath);
        File[] file_allPaths = inputFile.listFiles();
        String progressBar = new String(new char[file_allPaths.length]).replace('\0', '_');
        int count = 0;

        if (file_allPaths.length == 0){
            throw new IllegalArgumentException("This file is empty " + inputFile.getAbsolutePath());
        }

        for (File frame : file_allPaths) {

            BufferedImage img = jpeg_handler.readImage(frame.getAbsolutePath());
            if(isFilter){
                applyFilter(img);
            }
            //System.out.println(frame.getAbsolutePath()); //print for debugging
            JLabel image = new JLabel(new ImageIcon(img));
            //add and remove the images, making the illusion of updating the panel
            add(image);
            setVisible(true);
            remove(image);

            progressBar = progressBar.substring(0, count) + "=" + progressBar.substring(count+1,progressBar.length());
            //to control the frame rate, anything with more than one 0 is probably too slow
            FrameRateControl frameRate = new FrameRateControl(1000/fps); //in milliseconds
            frameRate.start();
            try {
                frameRate.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print(progressBar + "\r");
            //imageUpdate(image);
            count++;

        }

    }

    /**
     * @param inPath, the directory that contains all of the images
     * @param count, used as an index to determine when to stop, and which image to display. This variable is given
     * in the ScheduledPlayVideo Timer Task run method.
     *
     * This has a similar function as the original playVideo method, but instead of using a for loop, the Scheduler is
     * responsible for calling the method, using a separate thread. Since the count is given by the ScheduledPlayVideo
     * class, we don't need to use a loop to iterate over the images in the directory, and the speed will be controlled
     * by the Scheduler, which uses the frame rate, allowing the video to play faster or slower.
     *
     * This method simply plays with the add and remove image method to change the Panel, creating the illusion of playing
     * a video. If a filter exists, it will be applied to the image.
     */
    public void playVideo2(String inPath, int count){
        //Now using the Scheduler, as we call the task ScheduledPlayVideo, count is initialized and updated,
        //and it is used to control up until when the Panel is updated

        JPEG_Handler jpeg_handler = new JPEG_Handler();
        File inputFile = new File(inPath);
        File[] file_allPaths = inputFile.listFiles();
        String progressBar = new String(new char[file_allPaths.length]).replace('\0', '_');

        if (file_allPaths.length == 0){
            throw new IllegalArgumentException("This file is empty " + inputFile.getAbsolutePath());
        }

        if (count < file_allPaths.length ){

            File frame = file_allPaths[count];
            BufferedImage img = jpeg_handler.readImage(frame.getAbsolutePath());

            if(isFilter){
                applyFilter(img);
            }
            //System.out.println(frame.getAbsolutePath()); //print for debugging
            JLabel image = new JLabel(new ImageIcon(img));
            //add and remove the images, making the illusion of updating the panel
            add(image);
            setVisible(true);
            remove(image);
            progressBar = progressBar.substring(0, count) + "=" + progressBar.substring(count+1,progressBar.length());

            System.out.print(progressBar + "\r");
            //imageUpdate(image);
        }

    }

    /**
     * @param img, the image which will have the filter applied
     *
     * This method determines which filter to use, depending on the user's input. Two methods were implemented.
     */
    public void applyFilter(BufferedImage img){
        if(filter.equals("color")){
            changeColor(img);
        }else if(filter.equals("average")){
            averageFilter(img);
        }else{
            System.out.print(filter+" does not exist, no filter was applied. \n");
        }

    }

    /**
     * @param img, the image which will have the filter applied
     *
     * This method uses a convolutional filter, by using 3x3 groups instead of individual pixels. Here we change
     * the image into grayscale, and sharpen the colors.
     */
    public void averageFilter(BufferedImage img){

        float[] SHARPEN3x3 = {
                0.f, -1.f, 0.f,
                -1.f, 5.0f, -1.f,
                0.f, -1.f, 0.f};

        Kernel kernel = new Kernel(3,3,SHARPEN3x3);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,null);

        //System.out.println("Changing color...");
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        BufferedImage greyscale = new BufferedImage(img.getWidth(), img.getHeight(), img.TYPE_BYTE_GRAY);
        op.filter(img, greyscale);
        cop.filter(greyscale,img);
    }

    /**
     * @param img, the image which will have the filter applied
     *
     * This method uses a punctual filter by changing the color of each pixel of the image to a green-ish tone.
     */
    public void changeColor(BufferedImage img){
        //System.out.println("Changing color...");
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int rgb = img.getRGB(i,j);
                Color color = new Color(rgb);
                //Color redIsh = new Color(255, color.getGreen(),color.getBlue()); //keeps green and blue values, sets red to max
                //Color redIsh = new Color(color.getRed(), 0,0);
                Color greenIsh = new Color(0, color.getGreen(),0);
                img.setRGB(i, j, greenIsh.getRGB());
            }
        }

    }


}
