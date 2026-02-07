// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.num.GaussScalar;

class CosineSimilarityTest {
  @Test
  void testZero() {
    Optional<Scalar> optional = CosineSimilarity.of(UnitVector.of(3, 1), UnitVector.of(3, 2));
    Scalar scalar = optional.orElseThrow();
    assertEquals(scalar, RealScalar.ZERO);
  }

  @Test
  void testNOne() {
    Optional<Scalar> optional = CosineSimilarity.of(UnitVector.of(3, 1), UnitVector.of(3, 1).negate());
    Scalar scalar = optional.orElseThrow();
    assertEquals(scalar, RealScalar.ONE.negate());
  }

  @Test
  void testGaussian() {
    int p = 71;
    Tensor u = Tensors.of(GaussScalar.of(3, p));
    Tensor v = Tensors.of(GaussScalar.of(4, p));
    Optional<Scalar> optional = CosineSimilarity.of(u, v);
    assertTrue(optional.isPresent());
  }
}
