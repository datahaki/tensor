// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LinearColorDataGradientTest extends TestCase {
  public void testRandom() {
    ColorDataGradient colorDataGradient = //
        LinearColorDataGradient.of(RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 123, 4));
    Subdivide.of(0, 1, 10).map(colorDataGradient);
  }

  public void testSingle() {
    ColorDataGradient colorDataGradient = //
        LinearColorDataGradient.of(Tensors.fromString("{{1, 2, 3, 4}}"));
    Tensor matrix = Subdivide.of(0, 1, 10).map(colorDataGradient);
    assertEquals(matrix, ConstantArray.of(Range.of(1, 5), 11));
  }

  public void testCornerCaseLo() {
    AssertFail.of(() -> LinearColorDataGradient.of(Tensors.fromString("{{0, 0, 0, 0}, {-0.1, 0, 0, 0}}")));
  }

  public void testCornerCase() {
    AssertFail.of(() -> LinearColorDataGradient.of(Tensors.fromString("{{0, 0, 0, 0}, {256, 256, 256, 256}}")));
  }

  public void testRangeFail() {
    AssertFail.of(() -> LinearColorDataGradient.of(Tensors.fromString("{{1, 2, 3, 4}, {1, 2, 3, 257}}")));
  }

  public void testVectorFail() {
    AssertFail.of(() -> LinearColorDataGradient.of(Tensors.vector(1, 2, 3, 4)));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> LinearColorDataGradient.of(Tensors.empty()));
  }
}
