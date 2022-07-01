import FileManagement.MatchReader;
import FileManagement.MatchWriter;
import org.junit.Test;

import java.io.*;

public class MatchWriterTest {

    @Test
    public void fileWritingTest(){
        String filePath = "C:\\Users\\adriv\\IdeaProjects\\Project_Video_Codec\\MatchFile.txt";
        File matchFile = new File(filePath);
        boolean fileCreated;
        try{
            fileCreated = matchFile.createNewFile();
            if(fileCreated){
                FileOutputStream fos = new FileOutputStream(matchFile, true);
                String outstr = "";
                int numFrames = 2;
                outstr += numFrames;
                byte[] b= outstr.getBytes();
                fos.write(b);
                fos.close();

                System.out.print("file Created");
                MatchWriter match = new MatchWriter();
                match.addMatch(1,1 , 2);
                match.addMatch(4,5, 6);
                match.saveToFile(matchFile);
                match.clearData();
                match.saveToFile(matchFile);

            }else{
                System.out.print("file Created");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fileReadingTest(){
        String filePath = "C:\\Users\\adriv\\IdeaProjects\\Project_Video_Codec\\MatchFile.txt";
        //String filePath = "C:\\Users\\adriv\\IdeaProjects\\Project_Video_Codec\\src\\main\\resources\\Out_images\\Cubo_Encoded\\MatchFile.txt";
        File matchFile = new File(filePath);


        try{
            boolean fileCreated = matchFile.createNewFile();

            if(fileCreated){

                DataOutputStream dos = new DataOutputStream(new FileOutputStream(matchFile, true));
                int numFrames = 3;
                dos.writeInt(numFrames);
                dos.close();

                System.out.print("file Created");
                MatchWriter match = new MatchWriter();
                match.saveToFile(matchFile);
                match.clearData();
                match.addMatch(257,458 , 642);
                match.addMatch(420,5, 7895);
                match.saveToFile(matchFile);
                match.clearData();
                match.saveToFile(matchFile);

            }else{
                System.out.print("file Created");
            }
            MatchReader reader = new MatchReader(matchFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
