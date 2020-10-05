// code by jph
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.lie.MatrixExp;
import ch.ethz.idsc.tensor.lie.MatrixPower;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TraceTest extends TestCase {
  // from wikipedia
  private static Scalar _tr2Formula(Tensor A) {
    assertTrue(SquareMatrixQ.of(A));
    Scalar trA1 = Power.of(Trace.of(A), 2);
    Scalar trA2 = Trace.of(MatrixPower.of(A, 2));
    return trA1.subtract(trA2).divide(RealScalar.of(2));
  }

  // from wikipedia
  public void testViete() {
    Tensor matrix = Tensors.fromString("{{60, 30, 20}, {30, 20, 15}, {20, 15, 12}}");
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Chop._10.requireClose(Trace.of(matrix), Total.of(eigensystem.values())); // 1. Viete
    Chop._10.requireClose(Det.of(matrix), Times.pmul(eigensystem.values())); // 3. Viete
    {
      Scalar l1 = eigensystem.values().Get(0);
      Scalar l2 = eigensystem.values().Get(1);
      Scalar l3 = eigensystem.values().Get(2);
      Scalar res = _tr2Formula(matrix);
      Tensor vector = Tensors.of(l1.multiply(l2), l2.multiply(l3), l3.multiply(l1));
      Tensor cmp = Total.of(vector);
      Chop._10.requireClose(cmp, res); // 2. Viete
    }
  }

  public void testDetExpIsExpTrace() {
    Distribution distribution = NormalDistribution.of(0, 0.3);
    for (int n = 1; n < 5; ++n)
      for (int count = 0; count < 5; ++count) {
        Tensor matrix = RandomVariate.of(distribution, n, n);
        Scalar exp1 = Det.of(MatrixExp.of(matrix));
        Scalar exp2 = Exp.FUNCTION.apply(Trace.of(matrix));
        Chop._10.requireClose(exp1, exp2);
      }
  }

  public void testIdentityMatrix() {
    for (int n = 3; n < 8; ++n)
      assertEquals(Trace.of(IdentityMatrix.of(n), 0, 1), RealScalar.of(n));
  }

  public void testMatrix1X1() {
    assertEquals(Trace.of(Tensors.fromString("{{3+2*I}}")), ComplexScalar.of(3, 2));
  }

  public void testEmpty() {
    AssertFail.of(() -> Trace.of(Tensors.empty())); // mathematica gives 0 == Tr[{}]
    AssertFail.of(() -> Trace.of(Tensors.fromString("{{}}"))); // mathematica gives 0 == Tr[{{}}]
  }

  public void testDimensionsFail() {
    AssertFail.of(() -> Trace.of(RealScalar.ONE));
    AssertFail.of(() -> Trace.of(Tensors.vector(1, 2, 3)));
    AssertFail.of(() -> Trace.of(LeviCivitaTensor.of(3)));
  }

  public void testParamFail() {
    AssertFail.of(() -> Trace.of(HilbertMatrix.of(3, 3), 0, 0));
  }

  public void testFormatFail() {
    AssertFail.of(() -> Trace.of(HilbertMatrix.of(3, 4), 0, 1));
  }
}
