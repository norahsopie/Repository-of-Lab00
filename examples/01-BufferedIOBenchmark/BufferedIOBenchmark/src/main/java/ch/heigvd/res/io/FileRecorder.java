package ch.heigvd.res.io;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;

/* This class collects data and from the output stream to write them to an csv file*/

public class FileRecorder implements IRecorder{
   
   private ISerializer serializer;
   private String fileName;
   private PrintStream ps ;
   private File outputFile;
   
   public FileRecorder(String fileName){
      this.fileName = fileName;
      this.serializer = new CsvSerializer();
   }
   
   /* This function collects data and serialize them to pass through the output stream*/
   public void record(IData data){
      serializer.serialize(data, ps);
   }
   
   /* This function create connection between the output stream and the file to write in the file */
   public void init() throws IOException {
      File outputFile = new File(fileName);
      ps = new PrintStream(outputFile);
      ps.println("operation,strategy,blockSize,fileSizeInBytes,durationInMs");
   }
   
   public void close(){
      ps.close();
   }
}
