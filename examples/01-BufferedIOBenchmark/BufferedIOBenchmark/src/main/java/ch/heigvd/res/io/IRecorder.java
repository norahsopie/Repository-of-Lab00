package ch.heigvd.res.io;
import java.io.IOException;
import java.io.PrintStream;

public interface IRecorder {
   
   public void record(IData data);
   public void init() throws IOException;
   public void close();
}
