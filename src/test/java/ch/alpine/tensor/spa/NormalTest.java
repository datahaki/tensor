// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;

class NormalTest {
  @Test
  void testSimple() {
    Tensor tensor = Tensors.fromString("{{1}, 2}");
    Tensor result = Normal.of(tensor);
    assertEquals(tensor, result);
  }

  @Test
  void testScalar() {
    assertEquals(Normal.of(Pi.VALUE), Pi.VALUE);
  }

  @Test
  void testMixed() {
    Tensor mixed = Tensors.of( //
        SparseArray.of(RealScalar.ZERO, 3), Tensors.vector(1, 2), SparseArray.of(RealScalar.ZERO, 3));
    assertInstanceOf(SparseArray.class, mixed.get(0));
    assertEquals(Normal.of(mixed).toString(), "{{0, 0, 0}, {1, 2}, {0, 0, 0}}");
  }

  @SuppressWarnings("unchecked")
  @Test
  void testSerializable() {
    assertDoesNotThrow(() -> //
    Serialization.copy(new Normal((Function<Scalar, ? extends Tensor> & Serializable) s -> s.add(s))));
  }

  final Tensor tensor = Tensors.fromString("{{1,0,3,0,0,7},{0,0,0,0,0,9},{0,2,0,0,4,0},{0,0,0,0,0,0},{0,0,0,8,0,1}}");
  final Tensor sparse = TensorToSparseArray.of(tensor);

  private void _checkBlock(List<Integer> ofs, List<Integer> len) {
    Tensor block1 = tensor.block(ofs, len);
    Tensor block2 = sparse.block(ofs, len);
    assertEquals(block1, block2);
  }

  @Test
  void testBlock() {
    assertInstanceOf(SparseArray.class, sparse);
    int dimension1 = Unprotect.dimension1(tensor);
    for (int ofs0 = 0; ofs0 <= tensor.length(); ++ofs0)
      for (int len0 = 0; len0 <= tensor.length() - ofs0; ++len0) {
        _checkBlock(List.of(ofs0), List.of(len0));
        for (int ofs1 = 0; ofs1 <= dimension1; ++ofs1)
          for (int len1 = 0; len1 <= dimension1 - ofs1; ++len1)
            _checkBlock(Arrays.asList(ofs0, ofs1), Arrays.asList(len0, len1));
      }
  }

  private void _checkBlockFail(List<Integer> ofs, List<Integer> len) {
    assertThrows(Exception.class, () -> tensor.block(ofs, len));
    assertThrows(Exception.class, () -> sparse.block(ofs, len));
  }

  @Test
  void testBlockFail() {
    _checkBlockFail(List.of(-1), List.of(0));
    _checkBlockFail(List.of(6), List.of(0));
    // _checkBlockFail(Arrays.asList(0, -1), Arrays.asList(0, 0));
    _checkBlockFail(Arrays.asList(0, 7), Arrays.asList(1, 0));
  }

  private void _checkExtract(int head, int tail) {
    Tensor block1 = tensor.extract(head, tail);
    Tensor block2 = sparse.extract(head, tail);
    assertEquals(block1, block2);
  }

  @Test
  void testExtract() {
    assertInstanceOf(SparseArray.class, sparse);
    for (int ofs0 = 0; ofs0 <= tensor.length(); ++ofs0)
      for (int len0 = ofs0; len0 <= tensor.length(); ++len0)
        _checkExtract(ofs0, len0);
  }

  private void _checkExtractFail(int head, int tail) {
    assertThrows(Exception.class, () -> tensor.extract(head, tail));
    assertThrows(Exception.class, () -> sparse.extract(head, tail));
  }

  @Test
  void testExtractFail() {
    _checkExtractFail(-1, 2);
    _checkExtractFail(3, 2);
    _checkExtractFail(3, 6);
  }
}
