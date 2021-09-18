// code by jph
package ch.alpine.tensor.num;

import java.lang.reflect.Modifier;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Reverse;
import junit.framework.TestCase;

public class HornerSchemeTest extends TestCase {
  public void testHorner1() {
    Tensor coeffs = Tensors.vector(-3, 4);
    Scalar actual = Polynomial.of(coeffs).apply(RealScalar.of(2));
    Scalar expected = RealScalar.of(-3 + 2 * 4);
    ExactScalarQ.require(actual);
    assertEquals(expected, actual);
  }

  public void testHorner2() {
    Tensor coeffs = Tensors.vector(-3, 4, -5);
    Scalar x = RealScalar.of(2);
    Scalar actual = Polynomial.of(coeffs).apply(x);
    ExactScalarQ.require(actual);
    Scalar expected = RealScalar.of(-3 + 4 * (2) - 5 * (2 * 2));
    assertEquals(expected, actual);
    assertEquals(expected, new HornerScheme(Reverse.of(coeffs)).apply(x));
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(Polynomial.class.getModifiers()));
    assertTrue(Modifier.isPublic(FromDigits.class.getModifiers()));
    assertFalse(Modifier.isPublic(HornerScheme.class.getModifiers()));
  }
}
