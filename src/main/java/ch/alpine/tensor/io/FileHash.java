// code by jph
package ch.alpine.tensor.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** hash for the contents of the specified file
 * 
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/FileHash.html">FileHash</a> */
public enum FileHash {
  ;
  /** @param file
   * @param messageDigest
   * @return
   * @throws FileNotFoundException
   * @throws IOException */
  public static byte[] of(File file, MessageDigest messageDigest) throws FileNotFoundException, IOException {
    try (InputStream inputStream = new FileInputStream(file)) {
      try (DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)) {
        digestInputStream.readAllBytes();
        return messageDigest.digest();
      }
    }
  }

  /** for MessageDigest.getInstance("MD5") the function is consistent with
   * md5sum on linux.
   * 
   * @param file
   * @param messageDigest
   * @return
   * @throws FileNotFoundException
   * @throws IOException */
  public static String string(File file, MessageDigest messageDigest) throws FileNotFoundException, IOException {
    byte[] data = of(file, messageDigest);
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    // if ((data.length % 8) == 0) {
    // return IntStream.range(0, data.length / 8) //
    // .mapToObj(i -> String.format("%16x", byteBuffer.getLong())) //
    // .collect(Collectors.joining());
    // }
    return IntStream.range(0, data.length) //
        .mapToObj(i -> String.format("%02x", byteBuffer.get())) //
        .collect(Collectors.joining());
  }
}
