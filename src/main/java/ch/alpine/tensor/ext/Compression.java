// code adapted from
// http://qupera.blogspot.ch/2013/02/howto-compress-and-uncompress-java-byte.html
// code by gemini
package ch.alpine.tensor.ext;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/** class makes use of compression utilities provided by java.util.zip to compress byte arrays
 * {@link Deflater} and {@link Inflater} */
public enum Compression {
  ;
  private static final int BUFFER_SIZE = 8192;

  /** compression
   * 
   * @param data
   * @return byte array that is typically smaller than the input data */
  public static byte[] deflate(byte[] data) {
    try (Deflater deflater = new Deflater()) {
      deflater.setInput(data);
      deflater.finish();
      ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
      byte[] buffer = new byte[BUFFER_SIZE];
      while (!deflater.finished()) {
        int length = deflater.deflate(buffer);
        out.write(buffer, 0, length);
      }
      return out.toByteArray();
    }
  }

  /** decompression
   * 
   * @param data
   * @return byte array that is typically larger than the input data
   * @throws DataFormatException */
  public static byte[] inflate(byte[] data) throws DataFormatException {
    return inflate(data, 0, data.length);
  }

  /** decompression
   * 
   * @param data
   * @param off
   * @param len
   * @return
   * @throws DataFormatException */
  @PackageTestAccess
  static byte[] inflate(byte[] data, int off, int len) throws DataFormatException {
    try (Inflater inflater = new Inflater()) {
      inflater.setInput(data, off, len);
      ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(len * 2, BUFFER_SIZE));
      byte[] buffer = new byte[BUFFER_SIZE];
      while (true) {
        int length = inflater.inflate(buffer);
        if (0 < length)
          out.write(buffer, 0, length);
        // If finished, we are done (handles empty inputs and normal completions cleanly)
        if (inflater.finished())
          break;
        // If we got 0 length and aren't finished, the input is truncated
        if (length == 0)
          throw new DataFormatException("Compressed input data is truncated or incomplete.");
      }
      return out.toByteArray();
    }
  }
}
