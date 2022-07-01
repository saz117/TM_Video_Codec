package Parser;

import com.beust.jcommander.Parameter;

/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 *
 * Here we define all the parameters the user can give and their Default value
 */
public class Args {

    @Parameter(names = { "-i", "--input" },required = true, validateWith = FileParamaterValidator.class, description = "Path to file.zip")
    private String zipPath;

    @Parameter(names = { "-o", "--output" }, description = "Path to output file")
    private String outputName;

    @Parameter(names = { "-e", "--encode" },help = true, description = "Encode mode")
    private boolean encode;

    @Parameter(names = { "-d", "--decode" },help = true, description = "Decode mode")
    private boolean decode;

    @Parameter(names = "--fps",help = true, description = "Frames per second on the reproduction")
    private int fps = 24;

    @Parameter(names = "--nTiles",help = true, description = "Indicate if we want to apply filter to the images")
    private int nTiles = 8;

    @Parameter(names = "--seekRange",help = true, description = "Maximum sliding of tessles")
    private int seekRange = 4;


    @Parameter(names = "--GOP",help = true, description = "Number of images between two base images")
    private int GOP = 10;

    @Parameter(names = "--quality",help = true, description = "Quality factor for tessle coincidence")
    private int quality = 5;


    @Parameter(names = { "--batch", "-b" },help = true, description = "Batch mode")
    private boolean batch = false;

    @Parameter(names = { "--filter", "-f" },help = true, description ="Filter for the images" )
    private String filter = "null";

    @Parameter(names = { "--help", "-h" },help = true, description = "Display help information")
    private boolean help;

    /**  @return true if the help flag is active, false otherwise*/
    public boolean isHelp(){
        return help;
    }

    /** @return true if the encode flag is active, false otherwise*/
    public boolean isEncode(){
        return encode;
    }

    /** @return true if the decode flag is active, false otherwise*/
    public boolean isDecode(){
        return decode;
    }

    /** @return true if the batch flag is active, false otherwise*/
    public boolean isBatch(){
        return batch;
    }

    /** @return path of the base zip with images*/
    public String getZipPath(){return zipPath;}

    /*** @return path of the directory where we want to save the images */
    public String getOutputName(){return outputName;}

    /** @return number of frames per second of the video*/
    public int getFps(){return fps;}

    /*** @return size of tiles(8,16 or 32)*/
    public int getnTiles(){return nTiles;}

    /** @return limit of iterations of the search algorithm*/
    public int getSeekRange(){return seekRange;}

    /** @return number of images between two base frames*/
    public int getGOP(){return GOP;}

    /** @return threshold for the difference of two tiles */
    public int getQuality(){return quality;}

    /** @return what filer is being applied to the images*/
    public String getFilter(){return filter;}

}