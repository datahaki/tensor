package ch.alpine.tensor.opt.qh3;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

/** Runs a set of tests on the QuickHull3D class, and
 * prints <code>Passed</code> if all is well.
 * Otherwise, an error message and stack trace
 * are printed.
 *
 * <p>If the option <code>-timing</code> is supplied,
 * then timing information is produced instead. */
class ConvexHull3DTest {
  @Test
  void test() {
    QuickHull3DBase tester = new QuickHull3DBase();
    if (tester.doTiming) {
      tester.timingTests();
    }
  }

  /** Runs a set of explicit and random tests on QuickHull3D,
   * and prints <code>Passed</code> to System.out if all is well. */
  @Test
  void test1() {
    QuickHull3DBase tester = new QuickHull3DBase();
    System.out.println("Testing degenerate input ...");
    for (int dimen = 0; dimen < 3; dimen++) {
      for (int i = 0; i < 10; i++) {
        Scalar[] coords = tester.randomDegeneratePoints(10, dimen);
        if (dimen == 0) {
          tester.testException(coords, "Input points appear to be coincident");
        } else if (dimen == 1) {
          tester.testException(coords, "Input points appear to be colinear");
        } else if (dimen == 2) {
          tester.testException(coords, "Input points appear to be coplanar");
        }
      }
    }
  }

  @Test
  void test2() throws Exception {
    QuickHull3DBase tester = new QuickHull3DBase();
    System.out.println("Explicit tests ...");
    // test cases furnished by Mariano Zelke, Berlin
    {
      Scalar[] coords = ScalarArray.ofVector(Tensors.vectorDouble( //
          21, 0, 0, 0, 21, 0, 0, 0, 0, 18, 2, 6, 1, 18, 5, 2, 1, 3, 14, 3, 10, 4, 14, 14, 3, 4, 10, 10, 6, 12, 5, 10, 15));
      tester.test(coords, null);
    }
    {
      Scalar[] coords = ScalarArray.ofVector(Tensors.vectorDouble( //
          0.0, 0.0, 0.0, 21.0, 0.0, 0.0, 0.0, 21.0, 0.0, 2.0, 1.0, 2.0, 17.0, 2.0, 3.0, 1.0, 19.0, 6.0, 4.0, 3.0, 5.0, 13.0, 4.0, 5.0, 3.0, 15.0, 8.0, 6.0, 5.0,
          6.0, 9.0, 6.0, 11.0));
      tester.test(coords, null);
    }
  }

  @Test
  void test3() throws Exception {
    QuickHull3DBase tester = new QuickHull3DBase();
    System.out.println("Testing 20 to 200 random points ...");
    for (int n = 20; n < 200; n += 10) {
      Tensor coords = tester.randomPoints(n, RealScalar.of(1.0));
      tester.test(coords, null);
    }
  }

  @Test
  void test3b() throws Exception {
    QuickHull3DBase tester = new QuickHull3DBase();
    System.out.println("Testing 20 to 200 exact random points ...");
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    for (int n = 20; n < 200; n += 10) {
      Tensor coords = RandomVariate.of(distribution, tester.random, n, 3);
      tester.test(coords, null);
    }
  }

  @Test
  void test4() throws Exception {
    QuickHull3DBase tester = new QuickHull3DBase();
    System.out.println("Testing 20 to 200 random points in a sphere ...");
    for (int n = 20; n < 200; n += 10) {
      Tensor coords = tester.randomSphericalPoints(n, RealScalar.of(1.0));
      tester.test(coords, null);
    }
  }

  @Test
  void test5() throws Exception {
    QuickHull3DBase tester = new QuickHull3DBase();
    System.out.println("Testing 20 to 200 random points clipped to a cube ...");
    for (int n = 20; n < 200; n += 10) {
      Tensor coords = tester.randomCubedPoints(n, RealScalar.of(1.0), RealScalar.of(0.5));
      tester.test(coords, null);
    }
  }

  @Test
  void test6() throws Exception {
    QuickHull3DBase tester = new QuickHull3DBase();
    System.out.println("Testing 8 to 1000 randomly shuffled points on a grid ...");
    for (int n = 2; n <= 10; n++) {
      Tensor coords = tester.randomGridPoints(n, RealScalar.of(4.0));
      tester.test(coords, null);
    }
  }
}
