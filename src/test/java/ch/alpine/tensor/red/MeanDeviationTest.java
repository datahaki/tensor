// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MeanDeviationTest extends TestCase {
  public void testMathematica1() {
    Scalar value = MeanDeviation.ofVector(Tensors.fromString("{1, 2, 3, 7}"));
    assertEquals(value, RationalScalar.of(15, 8));
  }

  public void testMathematica2() {
    Scalar value = MeanDeviation.ofVector(Tensors.fromString("{1, 2, 3, 7/11}"));
    assertEquals(value, RationalScalar.of(37, 44));
    Chop._14.requireClose(N.DOUBLE.apply(value), RealScalar.of(0.84090909090909090909090909091));
  }

  public void testArray() {
    Scalar value = MeanDeviation.ofVector(RandomVariate.of(UniformDistribution.unit(), 10000));
    Clip clip = Clips.interval(0.23, 0.27);
    clip.requireInside(value);
  }

  public void testVectorFail() {
    AssertFail.of(() -> MeanDeviation.ofVector(RealScalar.ONE));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> MeanDeviation.ofVector(Tensors.empty()));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> MeanDeviation.ofVector(HilbertMatrix.of(3, 4)));
  }

  public void testTensorFail() {
    AssertFail.of(() -> MeanDeviation.ofVector(LeviCivitaTensor.of(3)));
  }
}
