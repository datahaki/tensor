// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;
import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CholeskyDecompositionImplTest extends TestCase {
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(CholeskyDecomposition.class.getModifiers()));
    assertFalse(Modifier.isPublic(CholeskyDecompositionImpl.class.getModifiers()));
  }

  public void testSolve() {
    Tensor matrix = Tensors.matrix(new Number[][] { //
        { 25, 15, -5 }, //
        { 15, 18, 0 }, //
        { -5, 0, 11 } //
    });
    CholeskyDecomposition choleskyDecomposition = CholeskyDecomposition.of(matrix);
    Tensor b = Tensors.vector(1, 2, 3);
    Tensor actual = choleskyDecomposition.solve(b);
    Tensor expect = Inverse.of(matrix).dot(b);
    assertEquals(actual, expect);
  }

  public void testSolveMatrix() {
    Tensor matrix = Tensors.matrix(new Number[][] { //
        { 25, 15, -5 }, //
        { 15, 18, 0 }, //
        { -5, 0, 11 } //
    });
    CholeskyDecomposition choleskyDecomposition = CholeskyDecomposition.of(matrix);
    Tensor b = Tensors.fromString("{{1, 2}, {3, 3}, {4, -1}}");
    Tensor expect = Inverse.of(matrix).dot(b);
    Tensor actual = choleskyDecomposition.solve(b);
    assertEquals(actual, expect);
  }

  public void testSolveQuantity() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString( //
        "{{60[m^2], 30[m*rad], 20[kg*m]}, {30[m*rad], 20[rad^2], 15[kg*rad]}, {20[kg*m], 15[kg*rad], 12[kg^2]}}");
    CholeskyDecomposition choleskyDecomposition = //
        Serialization.copy(CholeskyDecomposition.of(matrix));
    assertEquals( //
        choleskyDecomposition.solve(IdentityMatrix.of(3)), //
        Inverse.of(matrix));
  }

  public void testQuantityComplex() {
    Tensor matrix = Tensors.fromString("{{10[m^2], I[m*kg]}, {-I[m*kg], 10[kg^2]}}");
    CholeskyDecomposition choleskyDecomposition = CholeskyDecomposition.of(matrix);
    assertEquals( //
        choleskyDecomposition.solve(IdentityMatrix.of(2)), //
        Inverse.of(matrix));
  }

  public void testSolveFail() {
    Tensor matrix = Tensors.matrix(new Number[][] { //
        { 25, 15, -5 }, //
        { 15, 18, 0 }, //
        { -5, 0, 11 } //
    });
    CholeskyDecomposition choleskyDecomposition = CholeskyDecomposition.of(matrix);
    AssertFail.of(() -> choleskyDecomposition.solve(Tensors.vector(1, 2, 3, 4)));
    AssertFail.of(() -> choleskyDecomposition.solve(Tensors.vector(1, 2)));
    AssertFail.of(() -> choleskyDecomposition.solve(RealScalar.ONE));
  }
}
