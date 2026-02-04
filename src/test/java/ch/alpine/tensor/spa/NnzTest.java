// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.mat.MatrixDotConjugateTranspose;

class NnzTest {
  @Test
  void testSimple() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    SparseArray sparseArray = (SparseArray) TensorToSparseArray.of(tensor);
    assertEquals(Nnz.of(sparseArray), 8);
  }

  @Test
  void testSubtraction() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor raw = TensorToSparseArray.of(tensor);
    SparseArray sparse = (SparseArray) raw;
    SparseArray sparseArray = (SparseArray) sparse.subtract(sparse);
    assertEquals(Nnz.of(sparseArray), 0);
    // assertInstanceOf(SparseArray.class, MatrixDotConjugateTranspose.of(sparse));
    Tensor dot = MatrixDotConjugateTranspose.self(Transpose.of(sparse));
    dot.map(Scalar::zero);
    // assertInstanceOf(SparseArray.class, dot);
    // assertInstanceOf(SparseArray.class, raw.map(Conjugate.FUNCTION));
  }

  @Test
  void testSome() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4}}");
    Tensor sparse = TensorToSparseArray.of(tensor);
    sparse.set(Tensors.vector(1, 2, 3, 4, 5), 1);
    sparse.toString();
  }

  @RepeatedTest(5)
  @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
  void testLarge(RepetitionInfo repetitionInfo) {
    int size = 1000000 + repetitionInfo.getCurrentRepetition();
    Tensor tensor = SparseArray.of(RealScalar.ZERO, size, size, size);
    tensor.set(RealScalar.ONE, size - 2, size - 3, size - 4);
    int nnz = Nnz.of((SparseArray) tensor);
    assertEquals(nnz, 1);
  }

  @Test
  void testObject(@TempDir File folder) throws IOException, ClassNotFoundException, DataFormatException {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor raw = TensorToSparseArray.of(tensor);
    SparseArray sparse = (SparseArray) raw;
    File file = new File(folder, "sparse.object");
    Export.object(file, sparse);
    SparseArray object = Import.object(file);
    assertEquals(sparse, object);
  }
}
