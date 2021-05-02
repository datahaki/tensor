// code by jph
package ch.alpine.tensor.usr;

import java.util.Objects;

import junit.framework.Assert;

public enum AssertFail {
  ;
  public static void of(Runnable runnable) {
    Objects.requireNonNull(runnable);
    try {
      runnable.run();
      Assert.fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
