// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ImageFilterTest extends TestCase {
  private static final TensorScalarFunction MIN = block -> (Scalar) block.flatten(-1).reduce(Min::of).get();
  private static final TensorScalarFunction MAX = block -> (Scalar) block.flatten(-1).reduce(Max::of).get();

  public void testMin() {
    Distribution distribution = DiscreteUniformDistribution.of(0, 256);
    Tensor tensor = RandomVariate.of(distribution, 20, 30);
    Tensor filter = MinFilter.of(tensor, 3);
    Tensor result = ImageFilter.of(tensor, 3, MIN);
    assertEquals(filter, result);
  }

  public void testMax() {
    Distribution distribution = DiscreteUniformDistribution.of(0, 256);
    Tensor tensor = RandomVariate.of(distribution, 10, 15);
    Tensor filter = MaxFilter.of(tensor, 3);
    Tensor result = ImageFilter.of(tensor, 3, MAX);
    assertEquals(filter, result);
  }

  public void testEmpty() {
    Tensor result = ImageFilter.of(Tensors.empty(), 3, MAX);
    assertEquals(result, Tensors.empty());
  }

  public void testRadiusFail() {
    AssertFail.of(() -> ImageFilter.of(Tensors.empty(), -1, MAX));
  }

  public void testScalarFail() {
    AssertFail.of(() -> ImageFilter.of(RealScalar.ONE, 1, MAX));
  }

  public void testFunctionNullFail() {
    AssertFail.of(() -> ImageFilter.of(Tensors.empty(), 3, null));
  }
}
