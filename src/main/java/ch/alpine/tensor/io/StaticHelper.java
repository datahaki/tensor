// code by jph
package ch.alpine.tensor.io;

import java.nio.charset.Charset;

/* package */ enum StaticHelper {
  ;
  /** As of Java 18, the default charset is UTF-8. */
  public static final Charset CHARSET = Charset.forName("UTF-8");
}
