// code by jph
package ch.alpine.tensor.itp;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NearestInterpolationTest extends TestCase {
  public void testEmpty() {
    Interpolation interpolation = NearestInterpolation.of(Tensors.empty());
    assertEquals(interpolation.get(Tensors.empty()), Tensors.empty());
  }

  public void testStandard() throws ClassNotFoundException, IOException {
    Interpolation interpolation = Serialization.copy(NearestInterpolation.of(Tensors.vector(10, 20, 30, 40)));
    assertEquals(interpolation.get(Tensors.vector(2.8)), RealScalar.of(40));
    assertEquals(interpolation.get(Tensors.vector(1.1)), RealScalar.of(20));
  }

  public void testSerialize() throws Exception {
    Serialization.copy(NearestInterpolation.of(Tensors.vector(9, 1, 8, 3, 4)));
  }

  public void test1D() {
    Interpolation interpolation = NearestInterpolation.of(Tensors.vector(10, 20, 30, 40));
    TestHelper.checkMatch(interpolation);
    TestHelper.checkMatchExact(interpolation);
    TestHelper.getScalarFail(interpolation);
  }

  public void test2D() {
    Distribution distribution = UniformDistribution.unit();
    Interpolation interpolation = NearestInterpolation.of(RandomVariate.of(distribution, 3, 5));
    TestHelper.checkMatch(interpolation);
    TestHelper.checkMatchExact(interpolation);
    TestHelper.getScalarFail(interpolation);
  }

  public void testFailNull() {
    AssertFail.of(() -> NearestInterpolation.of(null));
  }
}
