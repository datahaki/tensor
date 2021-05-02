// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class FoldListTest extends TestCase {
  public void testEmpty() {
    Tensor tensor = FoldList.of(Tensor::add, Tensors.unmodifiableEmpty());
    assertEquals(tensor, Tensors.unmodifiableEmpty());
  }

  public void testAddUp() {
    Tensor tensor = FoldList.of(Tensor::add, Tensors.vector(1, 2, 3));
    assertEquals(tensor, Tensors.vector(1, 3, 6));
  }

  public void testMinMax() {
    assertEquals(FoldList.of(Min::of, Tensors.vector(1, 2, 1, 0)), Tensors.vector(1, 1, 1, 0));
    assertEquals(FoldList.of(Max::of, Tensors.vector(1, 2, 1, 0)), Tensors.vector(1, 2, 2, 2));
  }

  public void testFail() {
    AssertFail.of(() -> FoldList.of(Tensor::add, RealScalar.of(31)));
  }

  public void testAddUpPrependZero() {
    Tensor tensor = FoldList.of(Tensor::add, RealScalar.ZERO, Tensors.vector(1, 2, 3));
    assertEquals(tensor, Tensors.vector(0, 1, 3, 6));
    ExactTensorQ.require(tensor);
  }

  public void testAddUpPrependOne() {
    Tensor tensor = FoldList.of(Tensor::add, RealScalar.ONE, Tensors.vector(1, 2, 3));
    assertEquals(tensor, Tensors.vector(1, 2, 4, 7));
    ExactTensorQ.require(tensor);
  }

  public void testAddUpEmpty() {
    Tensor r = FoldList.of(Tensor::add, RealScalar.ONE, Tensors.unmodifiableEmpty());
    assertEquals(r, Tensors.vector(1));
  }

  public void testMinMaxSeed() {
    assertEquals(FoldList.of(Min::of, RealScalar.ONE, Tensors.vector(1, 2, 1, 0)), Tensors.vector(1, 1, 1, 1, 0));
    assertEquals(FoldList.of(Max::of, RealScalar.ZERO, Tensors.vector(1, 2, 1, 0)), Tensors.vector(0, 1, 2, 2, 2));
  }

  public void testFailSecond() {
    AssertFail.of(() -> FoldList.of(Tensor::add, RealScalar.of(31), RealScalar.of(31)));
  }
}
