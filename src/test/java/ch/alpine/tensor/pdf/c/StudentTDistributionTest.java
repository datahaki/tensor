// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class StudentTDistributionTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(StudentTDistribution.of(2, 3, 5));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose( //
        pdf.at(RealScalar.of(1.75)), //
        RealScalar.of(0.1260097929094335));
    assertEquals(distribution.toString(), "StudentTDistribution[2, 3, 5]");
  }

  @Test
  public void testMeanVar() {
    Distribution distribution = StudentTDistribution.of(5, 4, 3);
    assertEquals(ExactScalarQ.require(Mean.of(distribution)), RealScalar.of(5));
    assertEquals(ExactScalarQ.require(Variance.of(distribution)), RealScalar.of(48));
  }

  @Test
  public void testMeanVarSpecial() {
    assertFalse(FiniteScalarQ.of(Mean.of(StudentTDistribution.of(5, 4, 0.5))));
    assertFalse(FiniteScalarQ.of(Variance.of(StudentTDistribution.of(5, 4, 1.5))));
  }

  @Test
  public void testComplexFail() {
    assertThrows(ClassCastException.class, () -> StudentTDistribution.of(ComplexScalar.of(1, 2), RealScalar.ONE, RealScalar.ONE));
  }

  @Test
  public void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> StudentTDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "km"), RealScalar.ONE));
    assertThrows(TensorRuntimeException.class, () -> StudentTDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "m"), RealScalar.ONE));
    assertThrows(TensorRuntimeException.class, () -> StudentTDistribution.of(Quantity.of(0, ""), Quantity.of(2, "m"), RealScalar.ONE));
  }

  @Test
  public void testNegativeSigmaFail() {
    NormalDistribution.of(5, 1);
    assertThrows(TensorRuntimeException.class, () -> StudentTDistribution.of(5, -1, 1));
  }
}
