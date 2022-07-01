import Display.DisplayImg;
import Display.ScheduledVideoCaller;
import FileManagement.JPEG_Handler;
import FileManagement.ZipHandler;
import Parser.Args;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import Codec.Encoder;
import Codec.Decoder;

/**
 * @author Adrià Valls, Sebastian Andrade 2022
 */
public class Codec {
    //private static Args main_args;

    /**
     * @param args the command line arguments which are defined in the Parser package
     */
    public static void main(String[] args) {
        System.out.print("TM codec"+"\n");

        Args argParser = new Args();
        //main_args = argParser; //copy the arguments so we can use them in the runnable.

        JCommander jCommander = new JCommander(argParser);
        jCommander.setProgramName("TMCodec");

        try{
            jCommander.parse(args);
            testParser(argParser);
        }catch (ParameterException exception){
            System.out.print(exception.getMessage());
        }

        if (argParser.isHelp()){
            jCommander.usage();
            System.exit(0);
        }
        //Decode images of a file
        if(argParser.isDecode()){
            decode(argParser);
            if(!(argParser.isBatch())){
                callRunnable(argParser);
            }
            //test_Unzip_file(argParser); //for testing
        }
        //Encode images of a file
        if(argParser.isEncode()){
            encode(argParser);
            //test_Zip_file(argParser); //for testing
        }

        //try video
        //callRunnable(argParser);

    }

    /**
     * @param arguments the command line arguments which are defined in the Parser package
     * this function is just for testing. Here we make sure we can successfully unzip a file, and
     * save its content as jpeg
     */
    public static void test_Unzip_file(Args arguments){

        //read ZIP files
        ZipHandler zipHandler = new ZipHandler();
        //pass input path and output path
        zipHandler.readZip(arguments.getZipPath(), arguments.getOutputName());
        //TODO-Maybe create an if arg for this
        zipHandler.copy_file_as_jpeg(arguments.getOutputName(), arguments.getOutputName() +"JPEG_Copy");

    }

    /**
     * @param arguments the command line arguments which are defined in the Parser package
     * this function is just for testing. Here we make sure we can successfully zip a file, with
     * all of its content.
     */
    public static void test_Zip_file(Args arguments){

        //write ZIP files
        ZipHandler zipHandler = new ZipHandler();
        //pass input path and output path
        zipHandler.writeZip(arguments.getZipPath(), arguments.getOutputName());

    }

  
    public static void decode(Args arguments){

        long startTime = System.currentTimeMillis();

        ZipHandler zipHandler = new ZipHandler();
        //pass the input path and output path
        zipHandler.readZip(arguments.getZipPath(), arguments.getOutputName());

        Decoder decoder = new Decoder();
        decoder.decode(arguments.getOutputName(),arguments.getOutputName()+"_Finished",
                arguments.getnTiles(), arguments.getGOP());

        long encodingtime = System.currentTimeMillis() - startTime;

        System.out.println("Files decoded!");
        System.out.println("Encoding time: " + (double) encodingtime /1000 + "s");


    }

    /**
     * @param arguments the command line arguments which are defined in the Parser package
     * This is where the encoding process takes place. Here we call all the necessary functions to make this happen.
     * Unzip the file, read the unzipped images and apply the encoding process by calling de "encode" method.
     * Then we save the encoded images in a zip along with the serialized information document (for decoding).
     * Finally, we print the total processing time, along with the file size gain after compressing.
     */
    public static void encode(Args arguments){

        long startTime = System.currentTimeMillis(); //to keep track of the execution time
        //read ZIP files
        ZipHandler zipHandler = new ZipHandler();
        //pass the input path and output path
        zipHandler.readZip(arguments.getZipPath(), arguments.getOutputName());
        //create a copy in JPEG so we can make a fair comparison of the file gain ratio
        zipHandler.copy_file_as_jpeg(arguments.getOutputName(), arguments.getOutputName() +"JPEG_Copy");
        zipHandler.writeZip(arguments.getOutputName() +"JPEG_Copy", arguments.getOutputName() +"JPEG_Finished.zip");

        //once the Zip has been unzipped, we will now apply the encoding process
        Encoder encoder = new Encoder();
        encoder.encode(arguments.getOutputName(),arguments.getOutputName()+"_Encoded",
                arguments.getnTiles(), arguments.getSeekRange(), arguments.getGOP(), arguments.getQuality());

        //now that we have saved our encoded images as jpeg in another file, we can now zip it.
        zipHandler.writeZip(arguments.getOutputName()+"_Encoded", arguments.getOutputName()+"_Finished.zip");

        long encodingtime = System.currentTimeMillis() - startTime;

        System.out.println("Files encoded!");
        System.out.println("Encoding time: " + (double) encodingtime /1000 + "s");

        //now lets print the gain in data size
        Path old_path = Paths.get(arguments.getOutputName() +"JPEG_Finished.zip");
        Path encoded_path = Paths.get(arguments.getOutputName()+"_Finished.zip");
        try {

            //size of files (in bytes)
            long old = Files.size(old_path);
            long encoded = Files.size(encoded_path);

            System.out.println(String.format("File size improvement: "+ "%,d kilobytes", (old - encoded)/1024));
            System.out.println("File-size gain ratio: " + (1 - ((float)(encoded/1024)/(float)(old/1024))));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param arguments the command line arguments which are defined in the Parser package
     * this function is used to start the Scheduler, which is responsible for playing the video.
     * Here we read the first image, to start the display Panel in the DisplayImg Class. After the panel has
     * been started, all we need to do is update the feed, which will be done by the scheduler according to the
     * frame rate defined by the user.
     */
    public static void callRunnable(Args arguments){ //start Display, and start Thread for Scheduler

        //System.out.println("fps: " + arguments.getFps());
        BufferedImage img = null;
        File inputFile = null;
        if(arguments.isDecode()){
            inputFile = new File(arguments.getOutputName()+"_Finished"); //use output path

        }else if(arguments.isEncode()){ //try to zip folder

            inputFile = new File(arguments.getZipPath()); //use input path
        }else{
            inputFile = new File(arguments.getZipPath()); //else use input path
        }

        File[] file_allPaths = inputFile.listFiles();

        //read and write JPGE files
        //try to read an image
        JPEG_Handler jpeg_handler = new JPEG_Handler();
        img = jpeg_handler.readImage(file_allPaths[0].getAbsolutePath());

        //display one image, to start the window
        DisplayImg displayImg = new DisplayImg(img, arguments.getFilter());
        displayImg.setVisible(true);

        //make first call to start the Scheduler, to control the frame rate on a separate Thread
        ScheduledVideoCaller svc = new ScheduledVideoCaller(displayImg, arguments.getFps(), inputFile.getAbsolutePath());
        Thread thread1 = new Thread(svc);
        thread1.start();
    }

    /**
     * @param arguments the command line arguments which are defined in the Parser package
     * this function is just for testing. Here we let the user know all of the information introduced is correct,
     * and help ourselves see what parameters are being applied.
     */
    public static void testParser(Args arguments){

        if(arguments.isEncode()){
            System.out.print("Encode active"+"\n");
        }
        if(arguments.isDecode()){
            System.out.print("Decode active"+"\n");
        }
        String zipPath = arguments.getZipPath();
        System.out.print("Zip file path: " + zipPath +"\n");

        System.out.print("FPS: " + arguments.getFps()+"\n");
        System.out.print("nTiles: " + arguments.getnTiles()+"\n");
        System.out.print("seekRange: " + arguments.getSeekRange()+"\n");
        System.out.print("GOP: " + arguments.getGOP()+"\n");
        System.out.print("Quality: " + arguments.getQuality()+"\n");

        File file;
        BufferedImage img = null;
        int width, height;

        //try to read an image
        try{
            file = new File(zipPath);
            img = ImageIO.read(file);
            System.out.println("Image reading: correct");
        }catch (IOException error){
            System.out.println("Error reading image: " + error);
        }
    }

}
