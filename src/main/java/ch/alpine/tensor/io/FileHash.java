// code by jph
package ch.alpine.tensor.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;

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
    return HexFormat.of().formatHex(of(file, messageDigest));
  }
}
