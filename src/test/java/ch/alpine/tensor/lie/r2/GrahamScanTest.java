// code by jph
package ch.alpine.tensor.lie.r2;

import java.util.Arrays;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

public class GrahamScanTest extends TestCase {
  public void testColinear() {
    assertTrue(Scalars.isZero(GrahamScan.ccw( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(1.5, 0))));
    assertTrue(Scalars.isZero(GrahamScan.ccw( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(3, 0))));
    assertTrue(Scalars.isZero(GrahamScan.ccw( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(-1, 0))));
  }

  public void testCcw() {
    assertTrue(Sign.isPositive(GrahamScan.ccw( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(1, 10))));
    assertTrue(Sign.isPositive(GrahamScan.ccw( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(3, 1))));
    assertTrue(Sign.isPositive(GrahamScan.ccw( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(-1, 1))));
  }

  public void testCw() {
    assertTrue(Sign.isNegative(GrahamScan.ccw( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(1, -10))));
    assertTrue(Sign.isNegative(GrahamScan.ccw( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(3, -1))));
    assertTrue(Sign.isNegative(GrahamScan.ccw( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(-1, -1))));
  }

  public void testCluster() {
    Tensor tensor = Tensors.empty();
    tensor.append(Tensors.vector(1, 1));
    double variance = 1e-15;
    Distribution distribution = NormalDistribution.of(2.0, variance);
    RandomVariate.of(distribution, 5 - 1, 2).stream().forEach(tensor::append);
    Tensor hull = ConvexHull.of(tensor);
    assertTrue(2 <= hull.length());
    assertTrue(hull.length() <= 3);
    RandomVariate.of(distribution, 200 - 1, 2).stream().forEach(tensor::append);
    hull = ConvexHull.of(tensor);
    assertTrue(2 <= hull.length());
    assertTrue(hull.length() <= 3);
  }

  public void testClusterOnly() {
    double variance = 1e-20;
    Distribution distribution = NormalDistribution.of(0.0, variance);
    Tensor tensor = RandomVariate.of(distribution, 5, 2);
    Tensor hull = ConvexHull.of(tensor);
    assertEquals(Dimensions.of(hull), Arrays.asList(2, 2));
    tensor = RandomVariate.of(distribution, 200, 2);
    hull = ConvexHull.of(tensor);
    assertEquals(Dimensions.of(hull), Arrays.asList(2, 2));
  }
}
