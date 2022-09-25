// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.chq.ExactScalarQ;

class HornerSchemeTest {
  @Test
  void testHorner1() {
    Tensor coeffs = Tensors.vector(-3, 4);
    Scalar actual = Polynomial.of(coeffs).apply(RealScalar.of(2));
    Scalar expected = RealScalar.of(-3 + 2 * 4);
    ExactScalarQ.require(actual);
    assertEquals(expected, actual);
  }

  @Test
  void testHorner2() {
    Tensor coeffs = Tensors.vector(-3, 4, -5);
    Scalar x = RealScalar.of(2);
    Scalar actual = Polynomial.of(coeffs).apply(x);
    ExactScalarQ.require(actual);
    Scalar expected = RealScalar.of(-3 + 4 * (2) - 5 * (2 * 2));
    assertEquals(expected, actual);
    assertEquals(expected, new HornerScheme(Reverse.of(coeffs)).apply(x));
  }

  @Test
  void testPackageVisibility() {
    assertTrue(Modifier.isPublic(Polynomial.class.getModifiers()));
    assertTrue(Modifier.isPublic(FromDigits.class.getModifiers()));
    assertFalse(Modifier.isPublic(HornerScheme.class.getModifiers()));
  }
}
