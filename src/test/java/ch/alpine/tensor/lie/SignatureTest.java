// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Tally;

class SignatureTest {
  @Test
  public void testEmpty() {
    assertEquals(Signature.of(Tensors.empty()), RealScalar.ONE);
  }

  @Test
  public void testSingle() {
    assertEquals(Signature.of(Tensors.vector(0)), RealScalar.ONE);
  }

  @Test
  public void testTwo() {
    assertEquals(Signature.of(Tensors.vector(0, 1)), RealScalar.ONE);
    assertEquals(Signature.of(Tensors.vector(1, 0)), RealScalar.ONE.negate());
  }

  @Test
  public void testThree() {
    Scalar neg = RealScalar.ONE.negate();
    assertEquals(Signature.of(Tensors.vector(0, 1, 2)), RealScalar.ONE);
    assertEquals(Signature.of(Tensors.vector(0, 2, 1)), neg);
    assertEquals(Signature.of(Tensors.vector(1, 0, 2)), neg);
    assertEquals(Signature.of(Tensors.vector(1, 2, 0)), RealScalar.ONE);
    assertEquals(Signature.of(Tensors.vector(2, 0, 1)), RealScalar.ONE);
    assertEquals(Signature.of(Tensors.vector(2, 1, 0)), neg);
  }

  @Test
  public void testFour() {
    for (int length = 2; length < 6; ++length) {
      Map<Scalar, Long> map = Tally.of(Permutations.of(Range.of(0, length)).stream().map(Signature::of));
      assertEquals(map.get(RealScalar.of(1)), map.get(RealScalar.of(-1)));
    }
  }

  @Test
  public void testZero() {
    assertEquals(Signature.of(Tensors.vector(0, 1, 0)), RealScalar.ZERO);
    assertEquals(Signature.of(Tensors.of(Pi.HALF, Pi.HALF)), RealScalar.ZERO);
  }

  @Test
  public void testNonSequential() {
    assertEquals(Signature.of(Tensors.vector(0, 1, 3)), RealScalar.ONE);
    assertEquals(Signature.of(Tensors.vector(0, -1)), RealScalar.ONE.negate());
    assertEquals(Signature.of(Tensors.vector(3.5, -1)), RealScalar.ONE.negate());
  }

  @Test
  public void testMatrix() {
    assertEquals(Signature.of(Array.zeros(2, 2)), RealScalar.ZERO);
    assertEquals(Signature.of(HilbertMatrix.of(3)), RealScalar.ONE.negate());
    assertEquals(Signature.of(Reverse.of(HilbertMatrix.of(3))), RealScalar.ONE);
  }

  @Test
  public void testUnstructuredFail() {
    assertEquals(Signature.of(Tensors.fromString("{1, {2}}")), RealScalar.ONE);
    assertEquals(Signature.of(Tensors.fromString("{3, 1, {2}}")), RealScalar.ONE.negate());
  }

  @Test
  public void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Signature.of(RealScalar.ZERO));
  }
}
