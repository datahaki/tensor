// code adapted from
// http://qupera.blogspot.ch/2013/02/howto-compress-and-uncompress-java-byte.html
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
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      for (byte[] buffer = new byte[BUFFER_SIZE]; !deflater.finished();) {
        int length = deflater.deflate(buffer);
        byteArrayOutputStream.write(buffer, 0, length);
      }
      return byteArrayOutputStream.toByteArray();
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
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[BUFFER_SIZE];
      while (true) {
        int length = inflater.inflate(buffer);
        byteArrayOutputStream.write(buffer, 0, length);
        if (inflater.finished())
          break;
        else //
        if (length == 0)
          throw new DataFormatException();
      }
      return byteArrayOutputStream.toByteArray();
    }
  }
}
