// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.spa.SparseArray;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ArrayTest extends TestCase {
  public void testEmpty1() {
    assertEquals(Array.of(l -> {
      assertTrue(l.isEmpty());
      return RealScalar.ONE;
    }), RealScalar.ONE);
  }

  public void testArray() {
    Tensor table = Array.of(l -> RealScalar.of(l.get(0) * l.get(2)), 3, 2, 4);
    Tensor res = Tensors.fromString("{{{0, 0, 0, 0}, {0, 0, 0, 0}}, {{0, 1, 2, 3}, {0, 1, 2, 3}}, {{0, 2, 4, 6}, {0, 2, 4, 6}}}");
    assertEquals(table, res);
  }

  public void testCopy() {
    Tensor hilbert = HilbertMatrix.of(3, 5);
    Tensor table = Array.of(hilbert::get, Dimensions.of(hilbert));
    assertEquals(hilbert, table);
  }

  public void testVectorBlock() {
    Tensor tensor = Tensors.vector(0, 1, 2, 3, 4, 5);
    assertEquals(tensor.block(Arrays.asList(2), Arrays.asList(2)), tensor.extract(2, 4));
  }

  private static void checkDims(Tensor tensor, List<Integer> fromIndex, List<Integer> dims) {
    Tensor array = tensor.block(fromIndex, dims);
    assertEquals(dims, Dimensions.of(array).subList(0, dims.size()));
    int rank = TensorRank.of(tensor);
    assertEquals( //
        Dimensions.of(tensor).subList(dims.size(), rank), //
        Dimensions.of(array).subList(dims.size(), rank));
  }

  public void testFillEmpty() {
    Tensor tensor = Array.fill(() -> Pi.VALUE, Arrays.asList());
    assertEquals(tensor, Pi.VALUE);
  }

  public void testFillIntegers() {
    Tensor tensor = Array.fill(() -> Pi.VALUE, 1, 2);
    assertEquals(Dimensions.of(tensor), Arrays.asList(1, 2));
  }

  public void testBlock() {
    Tensor table = Array.of(l -> Tensors.vector(l.get(0), l.get(1), l.get(2)), 3, 2, 4);
    checkDims(table, Arrays.asList(), Arrays.asList());
    checkDims(table, Arrays.asList(0), Arrays.asList(3));
    checkDims(table, Arrays.asList(2, 1), Arrays.asList(1, 1));
    checkDims(table, Arrays.asList(1, 1, 1), Arrays.asList(2, 1, 1));
    checkDims(table, Arrays.asList(2, 1, 0, 1), Arrays.asList(1, 1, 2, 1));
  }

  public void testZeros() {
    Tensor zeros = Array.zeros(3, 5, 2, 7);
    Tensor table = Array.of(l -> RealScalar.ZERO, 3, 5, 2, 7);
    assertEquals(zeros, table);
    zeros.set(RealScalar.ONE, 0, 1, 0, 2);
    Map<Tensor, Long> map = Tally.of(Flatten.of(zeros));
    assertEquals(map.get(RealScalar.ZERO), (Long) (3L * 5 * 2 * 7 - 1L));
    assertEquals(map.get(RealScalar.ONE), (Long) 1L);
  }

  public void testZeros2() {
    Tensor zeros = Array.zeros(2, 3);
    assertEquals(zeros, Tensors.fromString("{ {  0,0, 0} ,  {0,0,0}  }"));
  }

  public void testEmpty() {
    assertEquals(Array.zeros(), RealScalar.ZERO);
    assertEquals(Array.zeros(0), Tensors.empty());
    assertEquals(Array.zeros(0, 1), Tensors.empty());
    assertEquals(Array.zeros(0, 0, 1), Tensors.empty());
    assertEquals(Array.zeros(1, 0, 0, 1), Tensors.of(Tensors.empty()));
  }

  public void testInvalid() {
    AssertFail.of(() -> Array.zeros(-1));
  }

  public void testInvalid2() {
    AssertFail.of(() -> Array.of(l -> Tensors.vector(l.get(0), l.get(1), l.get(2)), 3, -2, 4));
  }

  public void testForEach() {
    Set<List<Integer>> set = new HashSet<>();
    Array.forEach(set::add, 2, 3, 4);
    assertEquals(set.size(), 2 * 3 * 4);
  }

  public void testSparseDot() {
    int n = 7;
    Tensor p = Array.sparse(n, 1);
    Tensor q = Array.sparse(1, n);
    assertTrue(p.dot(q) instanceof SparseArray);
  }

  public void testForEachFail() {
    Set<List<Integer>> set = new HashSet<>();
    AssertFail.of(() -> Array.forEach(set::add, 2, -1, 4));
  }
}
