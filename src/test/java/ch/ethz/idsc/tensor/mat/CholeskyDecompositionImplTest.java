// code by jph
package ch.ethz.idsc.tensor.mat;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
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
