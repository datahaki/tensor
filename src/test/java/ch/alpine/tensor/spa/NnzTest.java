// code by jph
package ch.alpine.tensor.spa;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.MatrixDotConjugateTranspose;
import ch.alpine.tensor.sca.Conjugate;
import junit.framework.TestCase;

public class NnzTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    SparseArray sparseArray = (SparseArray) SparseArrays.of(tensor);
    assertEquals(Nnz.of(sparseArray), 8);
  }

  public void testSubtraction() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor raw = SparseArrays.of(tensor);
    SparseArray sparse = (SparseArray) raw;
    SparseArray sparseArray = (SparseArray) sparse.subtract(sparse);
    assertEquals(Nnz.of(sparseArray), 0);
    assertTrue(MatrixDotConjugateTranspose.of(sparse) instanceof SparseArray);
    Tensor dot = MatrixDotConjugateTranspose.of(Transpose.of(sparse));
    assertTrue(dot instanceof SparseArray);
    assertTrue(Conjugate.of(raw) instanceof SparseArray);
  }

  public void testSome() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4}}");
    Tensor sparse = SparseArrays.of(tensor);
    sparse.set(Tensors.vector(1, 2, 3, 4, 5), 1);
    sparse.toString();
  }
}
