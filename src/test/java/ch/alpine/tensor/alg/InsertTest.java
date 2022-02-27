// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.StringScalarQ;
import ch.alpine.tensor.spa.Normal;
import ch.alpine.tensor.spa.SparseArray;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class InsertTest extends TestCase {
  public void testAIndex0() {
    Tensor tensor = Tensors.fromString("{{1}, {2}, {3, 4}, 5, {}}");
    Tensor result = Insert.of(tensor, Tensors.fromString("{{{9}}}"), 0);
    assertEquals(result, Tensors.fromString("{{{{9}}}, {1}, {2}, {3, 4}, 5, {}}"));
  }

  public void testAIndex1() {
    Tensor tensor = Tensors.fromString("{{1}, {2}, {3, 4}, 5, {}}");
    Tensor result = Insert.of(tensor, Tensors.fromString("{{{9}}}"), 1);
    assertEquals(result, Tensors.fromString("{{1}, {{{9}}}, {2}, {3, 4}, 5, {}}"));
  }

  public void testAIndexLast() {
    Tensor tensor = Tensors.fromString("{{1}, {2}, {3, 4}, 5, {}}");
    Tensor result = Insert.of(tensor.unmodifiable(), Tensors.fromString("{{{9}}}"), 5);
    assertEquals(result, Tensors.fromString("{{1}, {2}, {3, 4}, 5, {}, {{{9}}}}"));
  }

  public void testExample() {
    assertEquals(Insert.of(Tensors.vector(1, 2, 3).unmodifiable(), RealScalar.of(0), 0), Range.of(0, 4));
    assertEquals(Insert.of(Tensors.vector(0, 2, 3).unmodifiable(), RealScalar.of(1), 1), Range.of(0, 4));
    assertEquals(Insert.of(Tensors.vector(0, 1, 2).unmodifiable(), RealScalar.of(3), 3), Range.of(0, 4));
  }

  public void testSparse() {
    Tensor sparse = SparseArray.of(RealScalar.ZERO, 2, 3);
    Tensor insert = Insert.of(sparse, Tensors.vector(1, 2), 1);
    assertTrue(insert.get(0) instanceof SparseArray);
    Tensor expect = Tensors.fromString("{{0, 0, 0}, {1, 2}, {0, 0, 0}}");
    assertFalse(StringScalarQ.any(expect));
    assertEquals(Normal.of(insert), expect);
    assertEquals(insert, expect);
  }

  public void testAFailSmall() {
    Insert.of(Tensors.vector(1, 2, 3), RealScalar.ZERO, 0);
    AssertFail.of(() -> Insert.of(Tensors.vector(1, 2, 3), RealScalar.ZERO, -1));
  }

  public void testAFailLarge() {
    Insert.of(Tensors.vector(1, 2, 3), RealScalar.ZERO, 3);
    AssertFail.of(() -> Insert.of(Tensors.vector(1, 2, 3), RealScalar.ZERO, 4));
  }

  public void testNullFail() {
    AssertFail.of(() -> Insert.of(null, RealScalar.ZERO, 0));
    AssertFail.of(() -> Insert.of(Tensors.vector(1, 2, 3), null, 0));
  }
}
