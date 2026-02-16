// code by jph
package ch.alpine.tensor.ext.ref;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.alpine.tensor.ext.PackageTestAccess;

public enum ClassPaths {
  ;
  /** @return original classpath used in implementation by lcm */
  public static String getDefault() {
    return join(System.getenv("CLASSPATH"), System.getProperty("java.class.path"));
  }

  /** @return reduced class path used for instance in swisstrolley+ project */
  public static String getResource() {
    URL url = ClassDiscovery.class.getResource("/");
    return join(System.getenv("CLASSPATH"), url.getPath());
  }

  /** @param paths
   * @return concatenation of paths to a single class path */
  @PackageTestAccess
  static String join(String... paths) {
    return Arrays.stream(paths) //
        .filter(Objects::nonNull) //
        .collect(Collectors.joining(File.pathSeparator));
  }
}
