// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.N;

class MeanDeviationTest {
  @Test
  void testMathematica1() {
    Scalar value = MeanDeviation.ofVector(Tensors.fromString("{1, 2, 3, 7}"));
    assertEquals(value, Rational.of(15, 8));
  }

  @Test
  void testMathematica2() {
    Scalar value = MeanDeviation.ofVector(Tensors.fromString("{1, 2, 3, 7/11}"));
    assertEquals(value, Rational.of(37, 44));
    Chop._14.requireClose(N.DOUBLE.apply(value), RealScalar.of(0.84090909090909090909090909091));
  }

  @Test
  void testArray() {
    Scalar value = MeanDeviation.ofVector(RandomVariate.of(UniformDistribution.unit(), 10000));
    Clip clip = Clips.interval(0.23, 0.27);
    clip.requireInside(value);
  }

  @Test
  void testVectorFail() {
    assertThrows(Throw.class, () -> MeanDeviation.ofVector(RealScalar.ONE));
  }

  @Test
  void testEmptyFail() {
    assertThrows(ArithmeticException.class, () -> MeanDeviation.ofVector(Tensors.empty()));
  }

  @Test
  void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> MeanDeviation.ofVector(HilbertMatrix.of(3, 4)));
  }

  @Test
  void testTensorFail() {
    assertThrows(ClassCastException.class, () -> MeanDeviation.ofVector(LeviCivitaTensor.of(3)));
  }
}
