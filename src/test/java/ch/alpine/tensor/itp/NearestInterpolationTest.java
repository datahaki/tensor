// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class NearestInterpolationTest {
  @Test
  void testEmpty() {
    Interpolation interpolation = NearestInterpolation.of(Tensors.empty());
    assertEquals(interpolation.get(Tensors.empty()), Tensors.empty());
  }

  @Test
  void testStandard() throws ClassNotFoundException, IOException {
    Interpolation interpolation = Serialization.copy(NearestInterpolation.of(Tensors.vector(10, 20, 30, 40)));
    assertEquals(interpolation.get(Tensors.vector(2.8)), RealScalar.of(40));
    assertEquals(interpolation.get(Tensors.vector(1.1)), RealScalar.of(20));
  }

  @Test
  void testSerialize() throws Exception {
    Serialization.copy(NearestInterpolation.of(Tensors.vector(9, 1, 8, 3, 4)));
  }

  @Test
  void test1D() {
    Interpolation interpolation = NearestInterpolation.of(Tensors.vector(10, 20, 30, 40));
    TestHelper.checkMatch(interpolation);
    TestHelper.checkMatchExact(interpolation);
    TestHelper.getScalarFail(interpolation);
  }

  @Test
  void test2D() {
    Distribution distribution = UniformDistribution.unit();
    Interpolation interpolation = NearestInterpolation.of(RandomVariate.of(distribution, 3, 5));
    TestHelper.checkMatch(interpolation);
    TestHelper.checkMatchExact(interpolation);
    TestHelper.getScalarFail(interpolation);
  }

  @Test
  void testFailNull() {
    assertThrows(NullPointerException.class, () -> NearestInterpolation.of(null));
  }
}
