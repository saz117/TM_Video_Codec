package Parser;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import  java.nio.file.Paths;
import  java.nio.file.Path;

/**
 *
 * @author Adrià Valls, Sebastian Andrade 2022
 */
public class FileParamaterValidator implements IParameterValidator {

    //Name of parameter and value (path to file) checks if it exists.

    @Override
    /**
     * @param name name of the variable
     * @param value directory or file path to be checked
     *
     * This method is used to make sure the given directories exist.
     */
    public void validate(String name, String value) throws ParameterException {
        Path pathToZipDir = Paths.get(value);
        if(!exists(pathToZipDir)){
            String message = (value + " does not exist");
            throw  new ParameterException(message);
        }
        if(!Files.isRegularFile(pathToZipDir, LinkOption.NOFOLLOW_LINKS) && !Files.isDirectory(pathToZipDir, LinkOption.NOFOLLOW_LINKS)){
            String message = (value + " Is not a file");
            throw  new ParameterException(message);
        }
    }

    private Boolean exists(Path path){
        return (Files.exists(path, LinkOption.NOFOLLOW_LINKS));
    }
}
