// code by jph
package ch.alpine.tensor.lie.ad;

import java.util.function.BinaryOperator;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.lie.MatrixBracket;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.spa.Normal;
import ch.alpine.tensor.spa.SparseArray;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixAlgebraTest extends TestCase {
  public void testSe2() {
    Tensor b0 = Tensors.fromString("{{0, 0, 1}, {0, 0, 0}, {0, 0, 0}}");
    Tensor b1 = Tensors.fromString("{{0, 0, 0}, {0, 0, 1}, {0, 0, 0}}");
    Tensor b2 = LeviCivitaTensor.of(3).get(2).negate();
    assertTrue(b2 instanceof SparseArray);
    Tensor basis = Tensors.of(b0, b1, b2);
    MatrixAlgebra matrixAlgebra = new MatrixAlgebra(basis);
    // System.out.println(ad);
    assertEquals(matrixAlgebra.ad(), LieAlgebras.se2());
    assertEquals(matrixAlgebra.toVector(b1.add(b2)), Tensors.vector(0, 1, 1));
    assertEquals(matrixAlgebra.toVector(b0.subtract(b2)), Tensors.vector(1, 0, -1));
    Tensor matrix = matrixAlgebra.toMatrix(Tensors.vector(2, 3, 4));
    Tensor tensor = Total.of(Tensors.of( //
        b0.multiply(RealScalar.of(2)), //
        b1.multiply(RealScalar.of(3)), //
        b2.multiply(RealScalar.of(4))));
    assertEquals(matrix, tensor);
    Tolerance.CHOP.requireClose( //
        matrixAlgebra.toVector(N.DOUBLE.of(b1.add(b2.multiply(RealScalar.of(0.3))))), Tensors.vector(0, 1, 0.3));
    Tensor rank4 = JacobiIdentity.of(matrixAlgebra.ad());
    assertTrue(rank4 instanceof SparseArray);
  }

  /** @return ad */
  public static Tensor sl2() {
    return Tensors.fromString( //
        "{{{0, 0, 0}, {0, 0, -2}, {0, 2, 0}}, {{0, 0, -2}, {0, 0, 0}, {2, 0, 0}}, {{0, -2, 0}, {2, 0, 0}, {0, 0, 0}}}");
  }

  public static Tensor sl2_basis() {
    Tensor b1 = Tensors.fromString("{{1, 0}, {0, -1}}");
    Tensor b2 = Tensors.fromString("{{0, 1}, {-1, 0}}");
    Tensor b3 = Tensors.fromString("{{0, 1}, {1, 0}}");
    return Tensors.of(b1, b2, b3);
  }

  public void testSl2() {
    Tensor ad = new MatrixAlgebra(sl2_basis()).ad();
    assertEquals(ad, LieAlgebras.sl2());
    Tensor form = KillingForm.of(ad);
    assertEquals(form, DiagonalMatrix.of(8, -8, 8));
  }

  public void testSo3() {
    Tensor basis = LeviCivitaTensor.of(3).negate();
    Tensor ad = new MatrixAlgebra(basis).ad();
    Tensor form = KillingForm.of(ad);
    assertEquals(form, DiagonalMatrix.of(-2, -2, -2));
  }

  public void testB0B2() {
    Tensor b0 = Tensors.fromString("{{0, 0, 1[m]}, {0, 0, 0[m]}, {0, 0, 0}}");
    b0.set(Quantity.of(0, "m^-1"), 2, 0);
    b0.set(Quantity.of(0, "m^-1"), 2, 1);
    Tensor b2 = Normal.of(LeviCivitaTensor.of(3).get(2).negate());
    b2.set(Quantity.of(0, "m"), 0, 2);
    b2.set(Quantity.of(0, "m"), 1, 2);
    b2.set(Quantity.of(0, "m^-1"), 2, 0);
    b2.set(Quantity.of(0, "m^-1"), 2, 1);
    // System.out.println(Pretty.of(b0));
    // System.out.println(Pretty.of(b2));
    b2.dot(b0);
  }

  public void testSe2Units() {
    Tensor b0 = Tensors.fromString("{{0, 0, 1[m]}, {0, 0, 0[m]}, {0[m^-1], 0[m^-1], 0}}");
    Tensor b1 = Tensors.fromString("{{0, 0, 0[m]}, {0, 0, 1[m]}, {0[m^-1], 0[m^-1], 0}}");
    Tensor b2 = Normal.of(LeviCivitaTensor.of(3).get(2).negate());
    b2.set(Quantity.of(0, "m"), 0, 2);
    b2.set(Quantity.of(0, "m"), 1, 2);
    b2.set(Quantity.of(0, "m^-1"), 2, 0);
    b2.set(Quantity.of(0, "m^-1"), 2, 1);
    Tensor basis = Tensors.of(b0, b1, b2);
    MatrixBracket.of(b0, b1);
    MatrixBracket.of(b0, b2);
    MatrixBracket.of(b1, b2);
    Tensor ad = new MatrixAlgebra(basis).ad(); // ad is unitless
    BinaryOperator<Tensor> binaryOperator = BakerCampbellHausdorff.of(ad, 6);
    Tensor x = Tensors.fromString("{1, 2, 3}");
    ad.dot(x);
    Tensor y = Tensors.fromString("{0.3, -0.4, 0.5}");
    Tensor tensor = binaryOperator.apply(x, y);
    Tensor expect = Tensors.fromString("{2.46585069444446, 2.417638888888879, 3.5}");
    Tolerance.CHOP.requireClose(tensor, expect);
    // Tensor approx = BchApprox.of(ad).apply(x, y);
    // Tolerance.CHOP.requireClose(approx, BakerCampbellHausdorff.of(ad, BchApprox.DEGREE).apply(x, y));
  }

  public void testZeroFail() {
    AssertFail.of(() -> new MatrixAlgebra(Array.zeros(1, 2, 2)));
  }

  public void testRedundantFail() {
    Tensor b0 = Tensors.fromString("{{0, 0, 1}, {0, 0, 0}, {0, 0, 0}}");
    AssertFail.of(() -> new MatrixAlgebra(Tensors.of(b0, b0)));
  }
}
