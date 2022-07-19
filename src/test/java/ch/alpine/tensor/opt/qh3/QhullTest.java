// adapted by jph
package ch.alpine.tensor.opt.qh3;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;

class QhullTest {
  @Test
  void testSimple() {
    ConvexHull3D hull = new ConvexHull3D();
    QuickHull3DBase tester = new QuickHull3DBase();
    hull = new ConvexHull3D();
    for (int i = 0; i < 100; i++) {
      Tensor pnts = tester.randomCubedPoints(100, RealScalar.of(1.0), RealScalar.of(0.5));
      hull.build(pnts);
      hull.triangulate();
      if (!hull.check(System.out)) {
        System.out.println("failed for QuickHull3D triangulated");
      }
      // hull = new QuickHull3D ();
      hull.build(pnts);
      if (!hull.check(System.out)) {
        System.out.println("failed for QuickHull3D regular");
      }
    }
  }
}
