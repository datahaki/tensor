// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class IntegersTest extends TestCase {
  public void testPositive() {
    for (int value : new int[] { 1, 2, Integer.MAX_VALUE })
      Integers.requirePositive(value);
  }

  public void testPositiveOrZero() {
    for (int value : new int[] { 0, 1, 2, Integer.MAX_VALUE })
      Integers.requirePositiveOrZero(value);
  }

  public void testPositiveFail() {
    for (int value : new int[] { Integer.MIN_VALUE, -3, -1, 0 })
      AssertFail.of(() -> Integers.requirePositive(value));
  }

  public void testPositiveOrZeroFail() {
    for (int value : new int[] { Integer.MIN_VALUE, -3, -1 })
      AssertFail.of(() -> Integers.requirePositiveOrZero(value));
  }
}
