// code by jph
package ch.alpine.tensor.sca.var;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.NormalizeTotal;

class InversePowerVariogramTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Tensor tensor = Tensors.vector(2, 3, 4, 5);
    Tensor w1 = NormalizeTotal.FUNCTION.apply(tensor.maps(Serialization.copy(InversePowerVariogram.of(2))));
    ExactTensorQ.require(w1);
  }

  @Test
  void testZero() {
    assertEquals(InversePowerVariogram.of(1).apply(RealScalar.ZERO), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(InversePowerVariogram.of(2).apply(RealScalar.ZERO), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testInverse() {
    assertEquals(InversePowerVariogram.of(1).apply(RealScalar.of(2)), Rational.HALF);
    assertEquals(InversePowerVariogram.of(2).apply(RealScalar.of(2)), Rational.of(1, 4));
  }

  @Test
  void testExponentZero() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator suo = Serialization.copy(InversePowerVariogram.of(0));
    Tensor domain = Subdivide.of(-1, 1, 6);
    assertEquals(domain.maps(suo), ConstantArray.of(RealScalar.ONE, 7));
  }
}
