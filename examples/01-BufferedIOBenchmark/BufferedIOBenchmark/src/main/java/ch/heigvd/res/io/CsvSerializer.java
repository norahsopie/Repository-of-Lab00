package ch.heigvd.res.io;

import java.io.PrintStream;

/* This class serialise the data collected in an CSV file */

public class CsvSerializer implements ISerializer{
   
   public void serialize(IData data, PrintStream ps){
      String csv = data.getOperation() + "," + data.getStrategy() + "," + data.getBlockSize() + "," + data.getFileSizeInBytes() + "," + data.getDurationInMs();
       ps.println(csv);
   }
   
}
