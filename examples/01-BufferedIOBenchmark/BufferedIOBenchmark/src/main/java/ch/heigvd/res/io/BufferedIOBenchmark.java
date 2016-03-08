package ch.heigvd.res.io;

import ch.heigvd.res.io.util.Timer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a very simple program, which main objective is to show that you can
 * observe very significant performance differences, depending on you implement IO
 * processing.
 *
 * Running the program allows you to compare both the WRITING and the READING of
 * bytes to the local file system. Different methods are compared: processing bytes
 * one by one, processing bytes in blocks, using buffered streams or not.
 *
 * @author Olivier Liechti
 */
public class BufferedIOBenchmark {

   static final Logger LOG = Logger.getLogger(BufferedIOBenchmark.class.getName());

   /**
    * This enum is used to describe the 4 different strategies for doing the IOs
    */
   public enum IOStrategy {

      ByteByByteWithoutBufferedStream,
      ByteByByteWithBufferedStream,
      BlockByBlockWithoutBufferedStream,
      BlockByBlockWithBufferedStream
   };

   final static String FILENAME_PREFIX = "test-data"; // we will write and read test files at this location
   final static long NUMBER_OF_BYTES_TO_WRITE = 1024 * 1024 * 10; // we will write and read 10 MB files

   /**
    * This method drives the generation of test data file, based on the parameters
    * passed. The method opens a FileOutputStream. Depending on the strategy, it
    * wraps a BufferedOutputStream around it, or not. The method then delegates the
    * actual production of bytes to another method, passing it the stream.
    */
   
   // Here, I have modified this function so that it can returns the time duration
   private long produceTestData(IOStrategy ioStrategy, long numberOfBytesToWrite, int blockSize) {
      LOG.log(Level.INFO, "Generating test data ({0}, {1} bytes, block size: {2}...", new Object[]{ioStrategy, numberOfBytesToWrite, blockSize});
      Timer.start();

      OutputStream os = null;
      try {
         // Let's connect our stream to a file data sink
         os = new FileOutputStream(FILENAME_PREFIX + "-" + ioStrategy + "-" + blockSize + ".bin");

         // If the strategy dictates to use a buffered stream, then let's wrap one around our file output stream
         if ((ioStrategy == IOStrategy.BlockByBlockWithBufferedStream) || (ioStrategy == IOStrategy.ByteByByteWithBufferedStream)) {
            os = new BufferedOutputStream(os);
         }

         // Now, let's call the method that does the actual work and produces bytes on the stream
         produceDataToStream(os, ioStrategy, numberOfBytesToWrite, blockSize);

         // We are done, so we only have to close the output stream
         os.close();
      } catch (IOException ex) {
         LOG.log(Level.SEVERE, ex.getMessage(), ex);
      } finally {
         try {
            if (os != null) {
               os.close();
            }
         } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
         }
      }
      long duration = Timer.takeTime();
      LOG.log(Level.INFO, "  > Done in {0} ms.", duration);

      return duration;
   }

   /**
    * This method produces bytes on the passed stream (the method does not know this
    * stream is buffered or not) Depending on the strategy, the method either writes
    * bytes one by one OR in chunks (the size of the chunk is passed in parameter)
    */
   private void produceDataToStream(OutputStream os, IOStrategy ioStrategy, long numberOfBytesToWrite, int blockSize) throws IOException {
      // If the strategy dictates to write byte by byte, then it's easy to write the loop; but let's just hope that our client has 
      // given us a buffered output stream, otherwise the performance will be really bad
      if ((ioStrategy == IOStrategy.ByteByByteWithBufferedStream) || (ioStrategy == IOStrategy.ByteByByteWithoutBufferedStream)) {
         for (int i = 0; i < numberOfBytesToWrite; i++) {
            os.write('h');
         }

         // If the strategy dictates to write block by block, then the loop is a bit longer to write
      } else {
         long remainder = numberOfBytesToWrite % blockSize;
         long numberOfBlocks = (numberOfBytesToWrite / blockSize);
         byte[] block = new byte[blockSize];

         // we start by writing a number of entire blocks
         for (int i = 0; i < numberOfBlocks; i++) {
            for (int j = 0; j < blockSize; j++) {
               block[j] = 'b';
            }
            os.write(block);
         }

         // and we write a partial block at the end
         if (remainder != 0) {
            for (int j = 0; j < remainder; j++) {
               block[j] = 'B';
            }
            os.write(block, 0, (int) remainder);
         }
      }
   }

   /**
    * This method drives the consumption of test data file, based on the parameters
    * passed. The method opens a FileInputStream. Depending on the strategy, it wraps
    * a BufferedInputStream around it, or not. The method then delegates the actual
    * consumption of bytes to another method, passing it the stream.
    */
   
   // Here, I have modified this function so that it can returns the time duration
   private long consumeTestData(IOStrategy ioStrategy, int blockSize) {
      LOG.log(Level.INFO, "Consuming test data ({0}, block size: {1}...", new Object[]{ioStrategy, blockSize});
      Timer.start();

      InputStream is = null;
      try {
         // Let's connect our stream to a file data sink
         is = new FileInputStream(FILENAME_PREFIX + "-" + ioStrategy + "-" + blockSize + ".bin");

         // If the strategy dictates to use a buffered stream, then let's wrap one around our file input stream
         if ((ioStrategy == IOStrategy.BlockByBlockWithBufferedStream) || (ioStrategy == IOStrategy.ByteByByteWithBufferedStream)) {
            is = new BufferedInputStream(is);
         }

         // Now, let's call the method that does the actual work and produces bytes on the stream
         consumeDataFromStream(is, ioStrategy, blockSize);

         // We are done, so we only have to close the input stream
         is.close();
      } catch (IOException ex) {
         LOG.log(Level.SEVERE, ex.getMessage(), ex);
      } finally {
         try {
            if (is != null) {
               is.close();
            }
         } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
         }
      }
      long duration = Timer.takeTime();
      LOG.log(Level.INFO, "  > Done in {0} ms.", duration);
      return duration;
   }

   /**
    * This method consumes bytes on the passed stream (the method does not know this
    * stream is buffered or not) Depending on the strategy, the method either reads
    * bytes one by one OR in chunks (the size of the chunk is passed in parameter).
    * The method does not do anything with the read bytes, except counting them.
    */
   private void consumeDataFromStream(InputStream is, IOStrategy ioStrategy, int blockSize) throws IOException {
      int totalBytes = 0;
      // If the strategy dictates to write byte by byte, then it's easy to write the loop; but let's just hope that our client has 
      // given us a buffered output stream, otherwise the performance will be really bad
      if ((ioStrategy == IOStrategy.ByteByByteWithBufferedStream) || (ioStrategy == IOStrategy.ByteByByteWithoutBufferedStream)) {
         int c;
         while ((c = is.read()) != -1) {
            // here, we could cast c to a byte and process it
            totalBytes++;
         }

         // If the strategy dictates to write block by block, then the loop is a bit longer to write
      } else {
         byte[] block = new byte[blockSize];
         int bytesRead = 0;
         while ((bytesRead = is.read(block)) != -1) {
            // here, we can process bytes block[0..bytesRead]
            totalBytes += bytesRead;
         }
      }

      LOG.log(Level.INFO, "Number of bytes read: {0}", new Object[]{totalBytes});
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {

      ISerializer serializer = new CsvSerializer();// create a file to serialize data
      IRecorder recorder = new FileRecorder("fileData.csv");// create a CSV file to put serialized data
      long duration;
      try {
         recorder.init();// 

         System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

         BufferedIOBenchmark bm = new BufferedIOBenchmark();
         LOG.log(Level.INFO, "");
         LOG.log(Level.INFO, "*** BENCHMARKING WRITE OPERATIONS (with BufferedStream)", Timer.takeTime());
         duration = bm.produceTestData(IOStrategy.BlockByBlockWithBufferedStream, NUMBER_OF_BYTES_TO_WRITE, 500);
         IData data1 = new MyExperimentData("WRITE", IOStrategy.BlockByBlockWithBufferedStream, 500, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data1);
         duration = bm.produceTestData(IOStrategy.BlockByBlockWithBufferedStream, NUMBER_OF_BYTES_TO_WRITE, 50);
         IData data2 = new MyExperimentData("WRITE", IOStrategy.BlockByBlockWithBufferedStream, 50, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data2);
         duration = bm.produceTestData(IOStrategy.BlockByBlockWithBufferedStream, NUMBER_OF_BYTES_TO_WRITE, 5);
         IData data3 = new MyExperimentData("WRITE", IOStrategy.BlockByBlockWithBufferedStream, 5, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data3);
         duration = bm.produceTestData(IOStrategy.ByteByByteWithBufferedStream, NUMBER_OF_BYTES_TO_WRITE, 0);
         IData data4 = new MyExperimentData("WRITE", IOStrategy.ByteByByteWithBufferedStream, 0, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data4);

         LOG.log(Level.INFO, "");
         LOG.log(Level.INFO, "*** BENCHMARKING WRITE OPERATIONS (without BufferedStream)", Timer.takeTime());
         duration = bm.produceTestData(IOStrategy.BlockByBlockWithoutBufferedStream, NUMBER_OF_BYTES_TO_WRITE, 500);
         IData data5 = new MyExperimentData("WRITE", IOStrategy.BlockByBlockWithoutBufferedStream, 500, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data5);
         duration = bm.produceTestData(IOStrategy.BlockByBlockWithoutBufferedStream, NUMBER_OF_BYTES_TO_WRITE, 50);
         IData data6 = new MyExperimentData("WRITE", IOStrategy.BlockByBlockWithoutBufferedStream, 50, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data6);
         duration = bm.produceTestData(IOStrategy.BlockByBlockWithoutBufferedStream, NUMBER_OF_BYTES_TO_WRITE, 5);
         IData data7 = new MyExperimentData("WRITE", IOStrategy.BlockByBlockWithoutBufferedStream, 5, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data7);
         duration = bm.produceTestData(IOStrategy.ByteByByteWithoutBufferedStream, NUMBER_OF_BYTES_TO_WRITE, 0);
         IData data8 = new MyExperimentData("WRITE", IOStrategy.ByteByByteWithoutBufferedStream, 0, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data8);
         LOG.log(Level.INFO, "");

         LOG.log(Level.INFO, "*** BENCHMARKING READ OPERATIONS (with BufferedStream)", Timer.takeTime());
         duration = bm.consumeTestData(IOStrategy.BlockByBlockWithBufferedStream, 500);
         IData data9 = new MyExperimentData("READ", IOStrategy.BlockByBlockWithBufferedStream, 500, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data9);
         duration = bm.consumeTestData(IOStrategy.BlockByBlockWithBufferedStream, 50);
         IData data10 = new MyExperimentData("READ", IOStrategy.BlockByBlockWithBufferedStream, 50, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data10);
         duration = bm.consumeTestData(IOStrategy.BlockByBlockWithBufferedStream, 5);
         IData data11 = new MyExperimentData("READ", IOStrategy.BlockByBlockWithBufferedStream, 5, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data11);
         duration = bm.consumeTestData(IOStrategy.ByteByByteWithBufferedStream, 0);
         IData data12 = new MyExperimentData("READ", IOStrategy.ByteByByteWithBufferedStream, 0, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data12);
         LOG.log(Level.INFO, "");

         LOG.log(Level.INFO, "*** BENCHMARKING READ OPERATIONS (without BufferedStream)", Timer.takeTime());
         duration = bm.consumeTestData(IOStrategy.BlockByBlockWithoutBufferedStream, 500);
         IData data13 = new MyExperimentData("READ", IOStrategy.BlockByBlockWithoutBufferedStream, 500, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data13);
         duration = bm.consumeTestData(IOStrategy.BlockByBlockWithoutBufferedStream, 50);
         IData data14 = new MyExperimentData("READ", IOStrategy.BlockByBlockWithoutBufferedStream, 50, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data14);
         duration = bm.consumeTestData(IOStrategy.BlockByBlockWithoutBufferedStream, 5);
         IData data15 = new MyExperimentData("READ", IOStrategy.BlockByBlockWithoutBufferedStream, 5, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data15);
         duration = bm.consumeTestData(IOStrategy.ByteByByteWithoutBufferedStream, 0);
         IData data16 = new MyExperimentData("READ", IOStrategy.ByteByByteWithoutBufferedStream, 0, NUMBER_OF_BYTES_TO_WRITE, duration);
         recorder.record(data16);
         recorder.close();

      } catch (IOException ex) {
         LOG.log(Level.SEVERE, ex.getMessage(), ex);
      } finally {
         if (recorder != null) {
            recorder.close();
         }
      }
   }
}
