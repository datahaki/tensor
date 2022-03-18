// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.ex.MatrixPower;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.spa.SparseArray;

public class TraceTest {
  // from wikipedia
  private static Scalar _tr2Formula(Tensor A) {
    assertTrue(SquareMatrixQ.of(A));
    Scalar trA1 = Power.of(Trace.of(A), 2);
    Tensor trA2 = Trace.of(MatrixPower.of(A, 2));
    return trA1.subtract(trA2).divide(RealScalar.of(2));
  }

  // from wikipedia
  @Test
  public void testViete() {
    Tensor matrix = Tensors.fromString("{{60, 30, 20}, {30, 20, 15}, {20, 15, 12}}");
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tolerance.CHOP.requireClose(Trace.of(matrix), Total.of(eigensystem.values())); // 1. Viete
    Tolerance.CHOP.requireClose(Det.of(matrix), //
        eigensystem.values().stream().map(Scalar.class::cast).reduce(Scalar::multiply).orElseThrow()); // 3. Viete
    {
      Scalar l1 = eigensystem.values().Get(0);
      Scalar l2 = eigensystem.values().Get(1);
      Scalar l3 = eigensystem.values().Get(2);
      Scalar res = _tr2Formula(matrix);
      Tensor vector = Tensors.of(l1.multiply(l2), l2.multiply(l3), l3.multiply(l1));
      Tensor cmp = Total.of(vector);
      Tolerance.CHOP.requireClose(cmp, res); // 2. Viete
    }
  }

  @Test
  public void testDetExpIsExpTrace() {
    Distribution distribution = NormalDistribution.of(0, 0.3);
    for (int n = 1; n < 5; ++n)
      for (int count = 0; count < 5; ++count) {
        Tensor matrix = RandomVariate.of(distribution, n, n);
        Scalar exp1 = Det.of(MatrixExp.of(matrix));
        Scalar exp2 = Exp.FUNCTION.apply(Trace.of(matrix));
        Tolerance.CHOP.requireClose(exp1, exp2);
      }
  }

  @Test
  public void testIdentityMatrix() {
    for (int n = 3; n < 8; ++n) {
      assertEquals(Trace.of(IdentityMatrix.of(n), 0, 1), RealScalar.of(n));
      assertEquals(Trace.of(IdentityMatrix.sparse(n), 0, 1), RealScalar.of(n));
    }
  }

  @Test
  public void testSparse() {
    Tensor tensor = SparseArray.of(Quantity.of(0, "m"), 3, 4, 3);
    Tensor traced = Trace.of(tensor, 0, 2);
    assertTrue(traced instanceof SparseArray);
    assertEquals(Dimensions.of(traced), Arrays.asList(4));
  }

  @Test
  public void testMatrix1X1() {
    assertEquals(Trace.of(Tensors.fromString("{{3+2*I}}")), ComplexScalar.of(3, 2));
  }

  @Test
  public void testEmpty() {
    assertThrows(IndexOutOfBoundsException.class, () -> Trace.of(Tensors.empty())); // mathematica gives 0 == Tr[{}]
    assertThrows(IllegalArgumentException.class, () -> Trace.of(Tensors.fromString("{{}}"))); // mathematica gives 0 == Tr[{{}}]
  }

  @Test
  public void testDimensionsFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> Trace.of(RealScalar.ONE));
    assertThrows(IndexOutOfBoundsException.class, () -> Trace.of(Tensors.vector(1, 2, 3)));
    assertThrows(ClassCastException.class, () -> Trace.of(LeviCivitaTensor.of(3)));
  }

  @Test
  public void testParamFail() {
    assertThrows(IllegalArgumentException.class, () -> Trace.of(HilbertMatrix.of(3, 3), 0, 0));
  }

  @Test
  public void testFormatFail() {
    assertThrows(IllegalArgumentException.class, () -> Trace.of(HilbertMatrix.of(3, 4), 0, 1));
  }
}
