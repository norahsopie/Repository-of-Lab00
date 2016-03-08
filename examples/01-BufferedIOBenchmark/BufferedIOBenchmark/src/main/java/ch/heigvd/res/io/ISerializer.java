package ch.heigvd.res.io;
import java.io.PrintStream;

public interface ISerializer {
   
   public void serialize(IData data, PrintStream ps);
   
}
