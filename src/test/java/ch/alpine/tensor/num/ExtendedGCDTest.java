// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Abs;

class ExtendedGCDTest {
  private static boolean isConsistent(Tensor vector, ExtendedGCD extendedGCD) {
    return vector.dot(extendedGCD.factors()).equals(extendedGCD.gcd());
  }

  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Function<Tensor, ExtendedGCD> function = ExtendedGCD.function();
    Tensor vector = Tensors.vector(12334 * 5, 32332 * 5);
    ExtendedGCD extendedGCD = Serialization.copy(function.apply(vector));
    assertTrue(isConsistent(vector, extendedGCD));
    assertEquals(extendedGCD.gcd(), RealScalar.of(10)); // confirmed with Mathematica
  }

  @Test
  public void testGCD() {
    Distribution distribution = DiscreteUniformDistribution.of(-10000, 10000);
    for (int index = 0; index <= 100; ++index) {
      Scalar a = RandomVariate.of(distribution);
      Scalar b = RandomVariate.of(distribution);
      Scalar gcd1 = GCD.of(a, b);
      Scalar gcd2 = ExtendedGCD.function().apply(Tensors.of(a, b)).gcd();
      assertEquals(gcd1, Abs.FUNCTION.apply(gcd2));
    }
  }

  @Test
  public void testGaussScalar() {
    int prime = 379;
    Scalar a = GaussScalar.of(4 * 3, prime);
    Scalar b = GaussScalar.of(16 * 3, prime);
    Function<Tensor, ExtendedGCD> function = ExtendedGCD.function();
    ExtendedGCD result = function.apply(Tensors.of(a, b));
    VectorQ.requireLength(result.factors(), 2);
    result.toString();
  }
}
