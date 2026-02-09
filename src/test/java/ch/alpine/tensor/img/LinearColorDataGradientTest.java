// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class LinearColorDataGradientTest {
  @Test
  void testRandom() {
    ColorDataGradient colorDataGradient = //
        LinearColorDataGradient.of(RandomVariate.of(DiscreteUniformDistribution.forArray(256), 123, 4));
    Subdivide.of(0, 1, 10).maps(colorDataGradient);
  }

  @Test
  void testSingle() {
    ColorDataGradient colorDataGradient = //
        LinearColorDataGradient.of(Tensors.fromString("{{1, 2, 3, 4}}"));
    Tensor matrix = Subdivide.of(0, 1, 10).maps(colorDataGradient);
    assertEquals(matrix, ConstantArray.of(Range.of(1, 5), 11));
  }

  @Test
  void testCornerCaseLo() {
    LinearColorDataGradient.of(Tensors.fromString("{{255, 255, 255, 255}, {0, 0, 0, 255}}"));
    assertThrows(Throw.class, () -> LinearColorDataGradient.of(Tensors.fromString("{{0, 0, 0, 0}, {-0.1, 0, 0, 0}}")));
  }

  @Test
  void testCornerCase() {
    assertThrows(IllegalArgumentException.class, () -> LinearColorDataGradient.of(Tensors.fromString("{{0, 0, 0, 0}, {256, 256, 256, 256}}")));
  }

  @Test
  void testRangeFail() {
    assertThrows(IllegalArgumentException.class, () -> LinearColorDataGradient.of(Tensors.fromString("{{1, 2, 3, 4}, {1, 2, 3, 257}}")));
  }

  @Test
  void testVectorFail() {
    assertThrows(Throw.class, () -> LinearColorDataGradient.of(Tensors.vector(1, 2, 3, 4)));
  }

  @Test
  void testEmptyFail() {
    assertThrows(Throw.class, () -> LinearColorDataGradient.of(Tensors.empty()));
  }
}
