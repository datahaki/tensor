// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.MatrixDotConjugateTranspose;
import ch.alpine.tensor.sca.Conjugate;

public class NnzTest {
  @Test
  public void testSimple() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    SparseArray sparseArray = (SparseArray) TestHelper.of(tensor);
    assertEquals(Nnz.of(sparseArray), 8);
  }

  @Test
  public void testSubtraction() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor raw = TestHelper.of(tensor);
    SparseArray sparse = (SparseArray) raw;
    SparseArray sparseArray = (SparseArray) sparse.subtract(sparse);
    assertEquals(Nnz.of(sparseArray), 0);
    assertTrue(MatrixDotConjugateTranspose.of(sparse) instanceof SparseArray);
    Tensor dot = MatrixDotConjugateTranspose.of(Transpose.of(sparse));
    assertTrue(dot instanceof SparseArray);
    assertTrue(Conjugate.of(raw) instanceof SparseArray);
  }

  @Test
  public void testSome() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4}}");
    Tensor sparse = TestHelper.of(tensor);
    sparse.set(Tensors.vector(1, 2, 3, 4, 5), 1);
    sparse.toString();
  }
}
