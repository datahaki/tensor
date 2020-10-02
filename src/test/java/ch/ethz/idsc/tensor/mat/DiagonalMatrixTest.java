// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DiagonalMatrixTest extends TestCase {
  public void testIdentity() {
    Tensor matrix = DiagonalMatrix.with(Tensors.vector(1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
    assertEquals(IdentityMatrix.of(10), matrix);
  }

  public void testMisc1() {
    Tensor matrix = DiagonalMatrix.of(-2, 3, -4);
    assertEquals(Det.of(matrix).number(), 2 * 3 * 4);
    ExactTensorQ.require(matrix);
  }

  public void testDiagonalMatrix() {
    Tensor m1 = DiagonalMatrix.with(Tensors.vectorDouble(12, 3.2, 0.32));
    Tensor m2 = DiagonalMatrix.of(12, 3.2, 0.32);
    assertEquals(m1, m2);
  }

  public void testMisc2() {
    Tensor matrix = DiagonalMatrix.of( //
        RealScalar.of(-2), RealScalar.of(3), RealScalar.of(-4));
    ExactTensorQ.require(matrix);
    assertEquals(Det.of(matrix).number(), 2 * 3 * 4);
  }

  public void testMisc3() {
    Tensor tensor = RealScalar.of(-2);
    AssertFail.of(() -> DiagonalMatrix.with(tensor));
  }

  public void testQuantity() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(2, "s");
    Tensor vec = Tensors.of(qs1, qs2);
    Tensor matrix = DiagonalMatrix.with(vec);
    ExactTensorQ.require(matrix);
    assertEquals(matrix.toString(), "{{1[m], 0[m]}, {0[s], 2[s]}}");
  }

  public void testFailScalar() {
    Tensor matrix = DiagonalMatrix.of(RealScalar.ONE);
    assertEquals(matrix.toString(), "{{1}}");
    AssertFail.of(() -> DiagonalMatrix.with(RealScalar.ONE));
  }

  public void testFailNonVector() {
    AssertFail.of(() -> DiagonalMatrix.with(Tensors.fromString("{1, 2, {3}}")));
  }

  public void testFailEmpty() {
    AssertFail.of(() -> DiagonalMatrix.with(Tensors.empty()));
  }

  public void testFailScalarEmpty() {
    AssertFail.of(() -> DiagonalMatrix.of(new Scalar[] {}));
  }

  public void testFailNumberEmpty() {
    AssertFail.of(() -> DiagonalMatrix.of(new Number[] {}));
  }
}
