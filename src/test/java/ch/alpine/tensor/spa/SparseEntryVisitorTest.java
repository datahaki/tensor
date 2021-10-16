// code by jph
package ch.alpine.tensor.spa;

import java.util.LinkedList;
import java.util.List;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.lie.Permutations;
import junit.framework.TestCase;

public class SparseEntryVisitorTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    SparseArray sparseArray = (SparseArray) SparseArrays.of(tensor, RealScalar.ZERO);
    sparseArray.set(RealScalar.ZERO, 2, 0);
    List<String> entries = new LinkedList<>();
    sparseArray.visit((list, scalar) -> entries.add(list + " " + scalar));
    assertEquals(entries.size(), 8);
  }

  public void testTransposeMatrix() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    SparseArray sparseArray = (SparseArray) SparseArrays.of(tensor, RealScalar.ZERO);
    Tensor transp = Transpose.of(sparseArray, 1, 0);
    assertTrue(transp instanceof SparseArray);
    assertEquals(transp, Transpose.of(tensor));
  }

  public void testTransposeAd() {
    Tensor tensor = Tensors.fromString("{{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}},{{0,1,0,0,6},{2,0,0,9,8},{2,1,0,5,3}}}");
    SparseArray sparseArray = (SparseArray) SparseArrays.of(tensor, RealScalar.ZERO);
    Tensor transp = Transpose.of(sparseArray, 1, 0);
    assertTrue(transp instanceof SparseArray);
    assertEquals(transp, Transpose.of(tensor));
  }

  public void testTransposeAd3() {
    Tensor tensor = Tensors.fromString("{{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}},{{0,1,0,0,6},{2,0,0,9,8},{2,1,0,5,3}}}");
    SparseArray sparseArray = (SparseArray) SparseArrays.of(tensor, RealScalar.ZERO);
    for (Tensor perm : Permutations.of(Range.of(0, 3))) {
      int[] sigma = Primitives.toIntArray(perm);
      Tensor transp = Transpose.of(sparseArray, sigma);
      assertTrue(transp instanceof SparseArray);
      assertEquals(transp, Transpose.of(tensor, sigma));
    }
  }
}
