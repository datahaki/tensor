// code by jph
package ch.alpine.tensor.ext;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

/** ObjectFormat is the serialization of objects in deflated form.
 * 
 * <p>The motivation to compress the byte arrays stems from the fact that
 * Java native serialization is not space efficient.
 * Compression factors of up to 10 are expected.
 * 
 * <p>In order to store an object to a file, use Export.object, or
 * <code>Files.write(path, ObjectFormat.of(object));</code>
 * 
 * <p>In order to read an object from a file, use Import.object, or
 * <code>ObjectFormat.parse(Files.readAllBytes(path));</code> */
public enum ObjectFormat {
  ;
  /** @param object may be null
   * @return deflated serialization of object as byte array
   * @throws IOException */
  public static byte[] of(Object object) throws IOException {
    return Compression.deflate(Serialization.of(object));
  }

  /** @param bytes containing the deflated serialization of object
   * @return object prior to serialization, may be null
   * @throws ClassNotFoundException
   * @throws DataFormatException
   * @throws IOException */
  public static <T> T parse(byte[] bytes) //
      throws ClassNotFoundException, IOException, DataFormatException {
    return Serialization.parse(Compression.inflate(bytes));
  }

  /** @param inputStream
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws DataFormatException */
  public static <T> T parse(InputStream inputStream) //
      throws ClassNotFoundException, IOException, DataFormatException {
    int length = inputStream.available();
    byte[] bytes = new byte[length];
    inputStream.read(bytes);
    return parse(bytes);
  }
}
