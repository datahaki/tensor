// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;

public class FoldListTest {
  @Test
  public void testEmpty() {
    Tensor tensor = FoldList.of(Tensor::add, Tensors.unmodifiableEmpty());
    assertEquals(tensor, Tensors.unmodifiableEmpty());
  }

  @Test
  public void testAddUp() {
    Tensor tensor = FoldList.of(Tensor::add, Tensors.vector(1, 2, 3));
    assertEquals(tensor, Tensors.vector(1, 3, 6));
  }

  @Test
  public void testMinMax() {
    assertEquals(FoldList.of(Min::of, Tensors.vector(1, 2, 1, 0)), Tensors.vector(1, 1, 1, 0));
    assertEquals(FoldList.of(Max::of, Tensors.vector(1, 2, 1, 0)), Tensors.vector(1, 2, 2, 2));
  }

  @Test
  public void testFail() {
    assertThrows(IllegalArgumentException.class, () -> FoldList.of(Tensor::add, RealScalar.of(31)));
  }

  @Test
  public void testAddUpPrependZero() {
    Tensor tensor = FoldList.of(Tensor::add, RealScalar.ZERO, Tensors.vector(1, 2, 3));
    assertEquals(tensor, Tensors.vector(0, 1, 3, 6));
    ExactTensorQ.require(tensor);
  }

  @Test
  public void testAddUpPrependOne() {
    Tensor tensor = FoldList.of(Tensor::add, RealScalar.ONE, Tensors.vector(1, 2, 3));
    assertEquals(tensor, Tensors.vector(1, 2, 4, 7));
    ExactTensorQ.require(tensor);
  }

  @Test
  public void testAddUpEmpty() {
    Tensor r = FoldList.of(Tensor::add, RealScalar.ONE, Tensors.unmodifiableEmpty());
    assertEquals(r, Tensors.vector(1));
  }

  @Test
  public void testMinMaxSeed() {
    assertEquals(FoldList.of(Min::of, RealScalar.ONE, Tensors.vector(1, 2, 1, 0)), Tensors.vector(1, 1, 1, 1, 0));
    assertEquals(FoldList.of(Max::of, RealScalar.ZERO, Tensors.vector(1, 2, 1, 0)), Tensors.vector(0, 1, 2, 2, 2));
  }

  @Test
  public void testFailSecond() {
    assertThrows(TensorRuntimeException.class, () -> FoldList.of(Tensor::add, RealScalar.of(31), RealScalar.of(31)));
  }
}
