package ch.heigvd.res.io;
import ch.heigvd.res.io.BufferedIOBenchmark.IOStrategy;

public interface IData {
   public Object getOperation();
   public Object getBlockSize();
   public Object getFileSizeInBytes();
   public Object getDurationInMs();
   public Object getStrategy();
}
