// adapted by jph
package ch.alpine.tensor.opt.qh3;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

class QhullTest {
  @Test
  void testSimple() {
    QuickHull3D hull = new QuickHull3D();
    QuickHull3DBase tester = new QuickHull3DBase();
    hull = new QuickHull3D();
    for (int i = 0; i < 100; i++) {
      Scalar[] pnts = tester.randomCubedPoints(100, RealScalar.of(1.0), RealScalar.of(0.5));
      hull.build(pnts, pnts.length / 3);
      hull.triangulate();
      if (!hull.check(System.out)) {
        System.out.println("failed for QuickHull3D triangulated");
      }
      // hull = new QuickHull3D ();
      hull.build(pnts, pnts.length / 3);
      if (!hull.check(System.out)) {
        System.out.println("failed for QuickHull3D regular");
      }
    }
  }
}
