// code by jph
package ch.alpine.tensor.io;

import java.nio.charset.Charset;

/* package */ enum StaticHelper {
  ;
  /** UTF-8 is the default charset for modern java versions */
  public static final Charset CHARSET = Charset.forName("UTF-8");
}
