// code by jph
package ch.alpine.tensor.num;

import java.util.Arrays;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class IntegerDigitsTest extends TestCase {
  public void testSimple() {
    assertEquals(IntegerDigits.of(RealScalar.of(+321)), Tensors.vector(3, 2, 1));
    assertEquals(IntegerDigits.of(RealScalar.of(-321)), Tensors.vector(3, 2, 1));
    assertEquals(IntegerDigits.of(RealScalar.of(+123456789)), Tensors.vector(1, 2, 3, 4, 5, 6, 7, 8, 9));
    assertEquals(IntegerDigits.of(RealScalar.of(-123456789)), Tensors.vector(1, 2, 3, 4, 5, 6, 7, 8, 9));
  }

  public void testExact() {
    assertEquals(IntegerDigits.of(Scalars.fromString("123456789012345678901234567890")), Tensors.vector(Arrays.asList( //
        1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0)));
  }

  public void testBase() {
    assertEquals(IntegerDigits.base(2).apply(Scalars.fromString("9")), Tensors.vector(Arrays.asList(1, 0, 0, 1)));
    assertEquals(IntegerDigits.base(2).apply(Scalars.fromString("11")), Tensors.vector(Arrays.asList(1, 0, 1, 1)));
    assertEquals(IntegerDigits.base(3).apply(Scalars.fromString("11")), Tensors.vector(Arrays.asList(1, 0, 2)));
  }

  public void testBaseFail() {
    AssertFail.of(() -> IntegerDigits.base(1));
    AssertFail.of(() -> IntegerDigits.base(0));
    AssertFail.of(() -> IntegerDigits.base(-1));
  }

  public void testZero() {
    assertEquals(IntegerDigits.of(RealScalar.ZERO), Tensors.vector(Arrays.asList()));
  }

  public void testPrecisionFail() {
    AssertFail.of(() -> IntegerDigits.of(RealScalar.of(1.0)));
  }

  public void testRationalFail() {
    AssertFail.of(() -> IntegerDigits.of(RationalScalar.of(10, 3)));
  }
}
