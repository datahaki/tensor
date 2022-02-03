// code by jph
package ch.alpine.tensor.itp;

import java.util.Arrays;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LanczosInterpolationTest extends TestCase {
  public void testVector() {
    Tensor vector = Tensors.vector(-1, 0, 3, 2, 0, -4, 2);
    for (int size = 1; size < 5; ++size) {
      Interpolation interpolation = LanczosInterpolation.of(vector, size);
      for (int index = 0; index < vector.length(); ++index) {
        Scalar scalar = interpolation.Get(Tensors.vector(index));
        Chop._14.requireClose(vector.Get(index), scalar);
      }
    }
  }

  public void testGetEmpty() {
    Interpolation interpolation = LanczosInterpolation.of(LeviCivitaTensor.of(3));
    Tensor tensor = interpolation.get(Tensors.empty());
    AssertFail.of(() -> tensor.set(t -> t.append(RealScalar.ONE), Tensor.ALL));
  }

  public void testImage() {
    String string = "/io/image/gray15x9.png";
    Tensor tensor = ResourceData.of(string);
    assertEquals(Dimensions.of(tensor), Arrays.asList(9, 15));
    Interpolation interpolation = LanczosInterpolation.of(tensor, 2);
    Tensor result = interpolation.get(Tensors.vector(6.2));
    assertEquals(Dimensions.of(result), Arrays.asList(15));
    Scalar scalar = interpolation.Get(Tensors.vector(4.4, 7.2));
    Chop._14.requireClose(scalar, RealScalar.of(105.27240539882584));
  }

  public void testImage3() {
    String string = "/io/image/gray15x9.png";
    Tensor tensor = ResourceData.of(string);
    assertEquals(Dimensions.of(tensor), Arrays.asList(9, 15));
    Interpolation interpolation = LanczosInterpolation.of(tensor);
    Tensor result = interpolation.get(Tensors.vector(6.2));
    assertEquals(Dimensions.of(result), Arrays.asList(15));
    Scalar scalar = interpolation.Get(Tensors.vector(4.4, 7.2));
    Chop._14.requireClose(scalar, RealScalar.of(94.24810834850828));
  }

  public void testUseCase() {
    Tensor tensor = Range.of(1, 11);
    Interpolation interpolation = LanczosInterpolation.of(tensor);
    Distribution distribution = DiscreteUniformDistribution.of(0, (tensor.length() - 1) * 3 + 1);
    for (int count = 0; count < 10; ++count) {
      Scalar index = RandomVariate.of(distribution).divide(RealScalar.of(3));
      Scalar scalar = interpolation.At(index);
      Scalar diff = RealScalar.ONE.add(index).subtract(scalar);
      Clips.interval(0, 0.5).requireInside(diff);
      assertEquals(scalar, interpolation.get(Tensors.of(index)));
      assertEquals(scalar, interpolation.at(index));
    }
  }

  public void test1D() {
    Interpolation interpolation = LanczosInterpolation.of(Tensors.vector(10, 20, 30, 40));
    TestHelper.checkMatch(interpolation);
    TestHelper.checkMatchExact(interpolation);
    TestHelper.getScalarFail(interpolation);
  }

  public void test2D() {
    Distribution distribution = UniformDistribution.unit();
    Interpolation interpolation = LanczosInterpolation.of(RandomVariate.of(distribution, 3, 5));
    TestHelper.checkMatch(interpolation);
    TestHelper.checkMatchExact(interpolation);
    TestHelper.getScalarFail(interpolation);
  }

  public void testFailNull() {
    AssertFail.of(() -> LanczosInterpolation.of(null, 3));
  }

  public void testFailSemi() {
    Tensor vector = Tensors.vector(-1, 0, 3, 2, 0, -4, 2);
    AssertFail.of(() -> LanczosInterpolation.of(vector, 0));
    AssertFail.of(() -> LanczosInterpolation.of(vector, -1));
  }
}
