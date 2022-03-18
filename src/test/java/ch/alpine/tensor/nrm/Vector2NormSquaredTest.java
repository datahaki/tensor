// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Chop;

public class Vector2NormSquaredTest {
  @Test
  public void testBetween() {
    Tensor v1 = Tensors.vector(1, 2, 5);
    Tensor v2 = Tensors.vector(0, -2, 10);
    Scalar n1 = Vector2NormSquared.between(v1, v2);
    Scalar n2 = Vector2Norm.between(v1, v2);
    Chop._13.requireClose(n1, AbsSquared.FUNCTION.apply(n2));
  }

  @Test
  public void testExact() {
    Scalar norm = Vector2NormSquared.of(Tensors.vector(3, 4));
    assertEquals(ExactScalarQ.require(norm), RealScalar.of(9 + 16));
  }

  @Test
  public void testEmpty() {
    assertThrows(NoSuchElementException.class, () -> Vector2NormSquared.of(Tensors.empty()));
  }
}
