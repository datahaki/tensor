// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.num.Pi;

class DifferencesTest {
  @Test
  void testVector() {
    Tensor dif = Differences.of(Tensors.vector(3, 2, 9).unmodifiable());
    assertEquals(dif, Tensors.vector(-1, 7));
    dif.set(Scalar::zero, Tensor.ALL);
    assertEquals(dif, Tensors.vector(0, 0));
  }

  @Test
  void testMatrix1() {
    Tensor dif = Differences.of(Tensors.of(Tensors.vector(3, 2, 9)));
    assertEquals(dif, Tensors.empty());
  }

  @Test
  void testMatrix2() {
    Tensor dif = Differences.of(Tensors.of( //
        Tensors.vector(3, 2, 9), //
        Tensors.vector(9, 3, 1) //
    ).unmodifiable());
    assertEquals(dif, Tensors.fromString("{{6, 1, -8}}"));
    dif.set(row -> row.append(RealScalar.of(3)), 0);
    assertEquals(dif.get(0), Tensors.vector(6, 1, -8, 3));
  }

  @Test
  void testAd() {
    Tensor dif = Differences.of(Array.zeros(3, 3, 3));
    assertEquals(Dimensions.of(dif), Arrays.asList(2, 3, 3));
  }

  @Test
  void testConsistent() {
    assertEquals(Differences.of(Tensors.empty()), Tensors.empty());
    assertEquals(Differences.of(Tensors.vector(1)), Tensors.empty());
  }

  @Test
  void testNonArray() {
    Tensor tensor = Tensors.fromString("{{1, {2, 4}}}");
    assertEquals(Differences.of(tensor), Tensors.empty());
  }

  @Test
  void testScalar() {
    assertThrows(Throw.class, () -> Differences.of(Pi.TWO));
  }
}
