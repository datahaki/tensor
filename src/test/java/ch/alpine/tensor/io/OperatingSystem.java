// code by jph
package ch.alpine.tensor.io;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/** ONLY FOR TEST SCOPE !
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/OperatingSystem.html">OperatingSystem</a> */
public enum OperatingSystem {
  ;
  private static final String OS_NAME = System.getProperty("os.name");

  /** @return whether host runs windows */
  public static boolean isWindows() {
    return OS_NAME.contains("Windows");
  }

  /** Careful: do not use function for accessing resources in jar files!
   * 
   * functionality suggested by github user axkr
   * 
   * @param name of resource, for instance "/ch/alpine/tensor/mat/sv/svd0.mathematica"
   * @return path to resource */
  public static Path pathOfResource(String name) {
    String string = OperatingSystem.class.getResource(name).getPath();
    return Paths.get(OperatingSystem.isWindows() && string.charAt(0) == '/' //
        ? string.substring(1)
        : string);
  }

  /** Careful: do not use function for accessing resources in jar files!
   * 
   * functionality suggested by github user axkr
   * 
   * @param name of resource, for instance "/ch/alpine/tensor/mat/sv/svd0.mathematica"
   * @return absolute file to resource */
  public static File fileOfResource(String name) {
    return pathOfResource(name).toFile();
  }
}
