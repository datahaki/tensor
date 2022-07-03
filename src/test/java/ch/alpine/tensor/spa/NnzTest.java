// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.MatrixDotConjugateTranspose;
import ch.alpine.tensor.sca.Conjugate;

class NnzTest {
  @Test
  void testSimple() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    SparseArray sparseArray = (SparseArray) TestHelper.of(tensor);
    assertEquals(Nnz.of(sparseArray), 8);
  }

  @Test
  void testSubtraction() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor raw = TestHelper.of(tensor);
    SparseArray sparse = (SparseArray) raw;
    SparseArray sparseArray = (SparseArray) sparse.subtract(sparse);
    assertEquals(Nnz.of(sparseArray), 0);
    assertInstanceOf(SparseArray.class, MatrixDotConjugateTranspose.of(sparse));
    Tensor dot = MatrixDotConjugateTranspose.of(Transpose.of(sparse));
    assertInstanceOf(SparseArray.class, dot);
    assertInstanceOf(SparseArray.class, Conjugate.of(raw));
  }

  @Test
  void testSome() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4}}");
    Tensor sparse = TestHelper.of(tensor);
    sparse.set(Tensors.vector(1, 2, 3, 4, 5), 1);
    sparse.toString();
  }

  @Test
  @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
  void testLarge() {
    int size = 1000000;
    Tensor tensor = SparseArray.of(RealScalar.ZERO, size, size, size);
    tensor.set(RealScalar.ONE, size - 2, size - 3, size - 4);
    int nnz = Nnz.of((SparseArray) tensor);
    assertEquals(nnz, 1);
  }
}
