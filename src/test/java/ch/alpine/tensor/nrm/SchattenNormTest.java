// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Sign;

class SchattenNormTest {
  @Test
  void testFrobenius() throws ClassNotFoundException, IOException {
    TensorScalarFunction tensorScalarFunction = Serialization.copy(SchattenNorm.of(2));
    Distribution distribution = UniformDistribution.of(-2, 2);
    Tensor design = RandomVariate.of(distribution, 5, 3);
    Scalar scalar = FrobeniusNorm.of(design);
    Tolerance.CHOP.requireClose(tensorScalarFunction.apply(design), scalar);
    Tolerance.CHOP.requireClose(tensorScalarFunction.apply(Transpose.of(design)), scalar);
    assertEquals(tensorScalarFunction.toString(), "SchattenNorm[2]");
  }

  @Test
  void testExact() throws ClassNotFoundException, IOException {
    Random random = new Random(1);
    TensorScalarFunction tensorScalarFunction = Serialization.copy(SchattenNorm.of(1.2));
    Distribution distribution = DiscreteUniformDistribution.of(0, 2);
    Tensor matrix = RandomVariate.of(distribution, random, 4, 3);
    Scalar scalar = tensorScalarFunction.apply(matrix);
    Sign.requirePositive(scalar);
  }

  @Test
  void testZero() {
    Tensor matrix = Array.zeros(3, 2);
    for (Tensor p : Subdivide.of(1, 3, 6)) {
      TensorScalarFunction tensorScalarFunction = SchattenNorm.of((Scalar) p);
      assertEquals(tensorScalarFunction.apply(matrix), RealScalar.ZERO);
    }
  }

  @Test
  void testPOutsideRangeFail() {
    assertThrows(Throw.class, () -> SchattenNorm.of(0.999));
  }

  @Test
  void testFormatFail() {
    TensorScalarFunction tensorScalarFunction = SchattenNorm.of(Rational.of(3, 2));
    assertThrows(ClassCastException.class, () -> tensorScalarFunction.apply(LeviCivitaTensor.of(3)));
    assertThrows(Exception.class, () -> tensorScalarFunction.apply(Tensors.vector(1, 2, 3)));
    assertThrows(Exception.class, () -> tensorScalarFunction.apply(Tensors.empty()));
    assertThrows(Throw.class, () -> tensorScalarFunction.apply(Pi.HALF));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> SchattenNorm.of((Number) null));
    assertThrows(NullPointerException.class, () -> SchattenNorm.of((Scalar) null));
  }
}
