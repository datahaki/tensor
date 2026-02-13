// code by jph
package ch.alpine.tensor.ext;

import java.nio.file.Path;

/* package */ enum StaticHelper {
  ;
  public static final Path USER_HOME = Path.of(System.getProperty("user.home"));
}
