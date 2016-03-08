package ch.heigvd.res.io;
import java.util.Map;
import java.util.HashMap;
import ch.heigvd.res.io.BufferedIOBenchmark.IOStrategy;

/* This class shows how the csv file will be organized */

public class MyExperimentData implements IData{
   
   private Map<String, Object> map = new HashMap<>();
   private IOStrategy strategy;
   private long blockSize;
   private long fileSizeInBytes;
   private long durationInMs;
   String operation;
   
   public MyExperimentData(String operation,IOStrategy strategy, long blockSize, long fileSizeInBytes, long durationInMs){
      map.put("operation", operation);
      map.put("strategy", strategy);
      map.put("blockSize", blockSize);
      map.put("fileSizeInBytes", fileSizeInBytes);
      map.put("durationInMs", durationInMs);
   }
   
   public Object getStrategy(){
      return map.get("strategy");
   }
   
   public Object getOperation(){
      return map.get("operation");
   }
   
   public Object getBlockSize(){
      return map.get("blockSize");
   }
   
   public Object getFileSizeInBytes(){
      return map.get("fileSizeInBytes");
   }
   
   public Object getDurationInMs(){
      return map.get("durationInMs");
   }
   
}
