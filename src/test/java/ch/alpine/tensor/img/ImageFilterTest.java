// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;

class ImageFilterTest {
  private final TensorScalarFunction MIN = block -> Flatten.scalars(block).reduce(Min::of).orElseThrow();
  private final TensorScalarFunction MAX = block -> Flatten.scalars(block).reduce(Max::of).orElseThrow();

  @Test
  void testMin() {
    Distribution distribution = DiscreteUniformDistribution.forArray(256);
    Tensor tensor = RandomVariate.of(distribution, 20, 30);
    Tensor filter = MinFilter.of(tensor, 3);
    Tensor result = ImageFilter.of(tensor, 3, MIN);
    assertEquals(filter, result);
  }

  @Test
  void testMax() {
    Distribution distribution = DiscreteUniformDistribution.forArray(256);
    Tensor tensor = RandomVariate.of(distribution, 10, 15);
    Tensor filter = MaxFilter.of(tensor, 3);
    Tensor result = ImageFilter.of(tensor, 3, MAX);
    assertEquals(filter, result);
  }

  @Test
  void testEmpty() {
    Tensor result = ImageFilter.of(Tensors.empty(), 3, MAX);
    assertEquals(result, Tensors.empty());
  }

  @Test
  void testRadiusFail() {
    assertThrows(IllegalArgumentException.class, () -> ImageFilter.of(Tensors.empty(), -1, MAX));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> ImageFilter.of(RealScalar.ONE, 1, MAX));
  }

  @Test
  void testFunctionNullFail() {
    assertThrows(NullPointerException.class, () -> ImageFilter.of(Tensors.empty(), 3, null));
  }
}
