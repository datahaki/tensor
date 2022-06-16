// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.lie.Permutations;

class SparseEntryVisitorTest {
  @Test
  void testSimple() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    SparseArray sparseArray = (SparseArray) TestHelper.of(tensor);
    sparseArray.set(RealScalar.ZERO, 2, 0);
    List<String> entries = new LinkedList<>();
    sparseArray.visit((list, scalar) -> entries.add(list + " " + scalar));
    assertEquals(entries.size(), 8);
  }

  @Test
  void testTransposeMatrix() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    SparseArray sparseArray = (SparseArray) TestHelper.of(tensor);
    sparseArray.set(s -> RealScalar.ZERO, 2, 0);
    Tensor transp = Transpose.of(sparseArray);
    assertInstanceOf(SparseArray.class, transp);
    assertEquals(transp, Transpose.of(tensor));
    assertThrows(IllegalArgumentException.class, () -> Transpose.of(sparseArray, 1, 0, 2));
  }

  @Test
  void testTransposeAd() {
    Tensor tensor = Tensors.fromString("{{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}},{{0,1,0,0,6},{2,0,0,9,8},{2,1,0,5,3}}}");
    SparseArray sparseArray = (SparseArray) TestHelper.of(tensor);
    Tensor transp = Transpose.of(sparseArray);
    assertInstanceOf(SparseArray.class, transp);
    assertEquals(transp, Transpose.of(tensor));
  }

  @Test
  void testTransposeAd3() {
    Tensor tensor = Tensors.fromString("{{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}},{{0,1,0,0,6},{2,0,0,9,8},{2,1,0,5,3}}}");
    SparseArray sparseArray = (SparseArray) TestHelper.of(tensor);
    for (Tensor perm : Permutations.of(Range.of(0, 3))) {
      int[] sigma = Primitives.toIntArray(perm);
      Tensor transp = Transpose.of(sparseArray, sigma);
      assertInstanceOf(SparseArray.class, transp);
      assertEquals(transp, Transpose.of(tensor, sigma));
    }
  }
}
