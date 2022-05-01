// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.Permutations;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.gam.Factorial;

class OrderedQTest {
  @Test
  public void testCornerCases() {
    assertTrue(OrderedQ.of(Tensors.empty()));
    assertTrue(OrderedQ.of(Tensors.vector(1123)));
  }

  @Test
  public void testSimple() {
    assertTrue(OrderedQ.of(Tensors.vector(1, 2)));
    assertFalse(OrderedQ.of(Tensors.vector(2, 1)));
    assertFalse(OrderedQ.of(Tensors.vector(0, 3, 1)));
    assertFalse(OrderedQ.of(Tensors.vector(1, 0, 2)));
    assertTrue(OrderedQ.of(Tensors.vector(1, 1, 2, 4, 4, 4)));
  }

  @Test
  public void testPermutations() {
    for (int index = 0; index < 5; ++index) {
      assertEquals(Permutations.stream(Range.of(0, index)).count(), Factorial.of(index).number().intValue());
      long count = Permutations.stream(Range.of(0, index)) //
          .filter(OrderedQ::of) //
          .count();
      assertEquals(count, 1);
    }
  }

  @Test
  public void testMatrixFail() {
    assertFalse(OrderedQ.of(IdentityMatrix.of(4)));
    assertTrue(OrderedQ.of(Reverse.of(IdentityMatrix.of(4))));
  }

  @Test
  public void testRequire() {
    OrderedQ.require(Tensors.vector(1, 1, 2, 4, 4, 4));
    assertThrows(TensorRuntimeException.class, () -> OrderedQ.require(Tensors.vector(0, 3, 1)));
  }

  @Test
  public void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> OrderedQ.of(Pi.VALUE));
  }

  @Test
  public void testUncomparable1Fail() {
    Tensor tensor = Tensors.fromString("{3[s], 4[s], 2[m]}");
    assertThrows(TensorRuntimeException.class, () -> OrderedQ.of(tensor));
  }

  @Test
  public void testUncomparable2Fail() {
    Tensor tensor = Tensors.fromString("{3[s], 1[s], 2[m]}");
    assertThrows(TensorRuntimeException.class, () -> OrderedQ.of(tensor));
  }
}
