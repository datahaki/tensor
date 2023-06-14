// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.ext.Serialization;

class ReImTest {
  @Test
  void testSimple() {
    assertEquals(ReIm.of(RealScalar.ONE), UnitVector.of(2, 0));
    assertEquals(ReIm.of(ComplexScalar.I), UnitVector.of(2, 1));
    assertEquals(ReIm.of(ComplexScalar.of(3, 4)), Tensors.vector(3, 4));
  }

  @Test
  void testSerialization() throws ClassNotFoundException, IOException {
    ReIm reIm = new ReIm(ComplexScalar.of(2, 5));
    Serialization.copy(reIm);
  }
}
