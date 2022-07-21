/** Copyright John E. Lloyd, 2004. All rights reserved. Permission to use,
 * copy, modify and redistribute is granted, provided that this copyright
 * notice is retained and the author is given credit whenever appropriate.
 *
 * This software is distributed "as is", without any warranty, including
 * any implied warranty of merchantability or fitness for a particular
 * use. The author assumes no responsibility for, and shall not be liable
 * for, any special, indirect, or consequential damages, or any damages
 * whatsoever, arising out of or in connection with the use of this
 * software. */
package ch.alpine.tensor.opt.qh3;

import java.util.Random;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.num.RandomPermutation;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clips;

/** Testing class for QuickHull3D. Running the command
 * <pre>
 * java quickhull3d.QuickHull3DTest
 * </pre>
 * will cause QuickHull3D to be tested on a number of randomly
 * choosen input sets, with degenerate points added near
 * the edges and vertics of the convex hull.
 *
 * <p>The command
 * <pre>
 * java quickhull3d.QuickHull3DTest -timing
 * </pre>
 * will cause timing information to be produced instead.
 *
 * @author John E. Lloyd, Fall 2004 */
class QuickHull3DBase {
  private static final Scalar DOUBLE_PREC = RealScalar.of(2.2204460492503131e-16);
  final boolean triangulate = false;
  final boolean doTiming = false;
  static final boolean debugEnable = false;
  static final int NO_DEGENERACY = 0;
  static final int EDGE_DEGENERACY = 1;
  static final int VERTEX_DEGENERACY = 2;
  static final boolean testRotation = true;
  static final int degeneracyTest = VERTEX_DEGENERACY;
  static final Scalar epsScale = RealScalar.of(2.0);
  // ---
  final Random random; // random number generator

  /** Creates a testing object. */
  public QuickHull3DBase() {
    random = new Random();
    random.setSeed(0x1234);
  }

  /** Returns true if two face index sets are equal,
   * modulo a cyclical permuation.
   *
   * @param indices1 index set for first face
   * @param indices2 index set for second face
   * @return true if the index sets are equivalent */
  public boolean faceIndicesEqual(int[] indices1, int[] indices2) {
    if (indices1.length != indices2.length) {
      return false;
    }
    int len = indices1.length;
    int j;
    for (j = 0; j < len; j++) {
      if (indices1[0] == indices2[j]) {
        break;
      }
    }
    if (j == len) {
      return false;
    }
    for (int i = 1; i < len; i++) {
      if (indices1[i] != indices2[(j + i) % len]) {
        return false;
      }
    }
    return true;
  }

  /** Returns the coordinates for <code>num</code> points whose x, y, and
   * z values are randomly chosen within a given range.
   *
   * @param num number of points to produce
   * @param range coordinate values will lie between -range and range
   * @return array of coordinate values */
  public Tensor randomPoints(int num, Scalar range) {
    return RandomVariate.of(UniformDistribution.of(Clips.absolute(range)), random, num, 3);
  }

  private void randomlyPerturb(Vector3d pnt, Scalar tol) {
    pnt.x = pnt.x.add(RealScalar.of(random.nextDouble() - 0.5).multiply(tol));
    pnt.y = pnt.y.add(RealScalar.of(random.nextDouble() - 0.5).multiply(tol));
    pnt.z = pnt.z.add(RealScalar.of(random.nextDouble() - 0.5).multiply(tol));
  }

  /** Sets the elements of this vector to uniformly distributed
   * random values in a specified range, using a supplied
   * random number generator.
   *
   * @param lower lower random value (inclusive)
   * @param upper upper random value (exclusive)
   * @param generator random number generator */
  protected static void setRandom(Vector3d v, Scalar lower, Scalar upper, Random generator) {
    Distribution distribution = UniformDistribution.of(lower, upper);
    v.set(RandomVariate.of(distribution, generator, 3));
  }

  /** Returns the coordinates for <code>num</code> randomly
   * chosen points which are degenerate which respect
   * to the specified dimensionality.
   *
   * @param num number of points to produce
   * @param dimen dimensionality of degeneracy: 0 = coincident,
   * 1 = colinear, 2 = coplaner.
   * @return array of coordinate values */
  public Scalar[] randomDegeneratePoints(int num, int dimen) {
    Scalar[] coords = ScalarArray.ofVector(Array.zeros(num * 3));
    Vector3d pnt = new Vector3d();
    Vector3d base = new Vector3d();
    setRandom(base, RealScalar.of(-1), RealScalar.of(1), random);
    Scalar tol = DOUBLE_PREC;
    if (dimen == 0) {
      for (int i = 0; i < num; i++) {
        pnt.set(base);
        randomlyPerturb(pnt, tol);
        coords[i * 3 + 0] = pnt.x;
        coords[i * 3 + 1] = pnt.y;
        coords[i * 3 + 2] = pnt.z;
      }
    } else if (dimen == 1) {
      Vector3d u = new Vector3d();
      setRandom(u, RealScalar.of(-1), RealScalar.of(1), random);
      u.normalize();
      for (int i = 0; i < num; i++) {
        double a = 2 * (random.nextDouble() - 0.5);
        pnt.scale(RealScalar.of(a), u);
        pnt.add(base);
        randomlyPerturb(pnt, tol);
        coords[i * 3 + 0] = pnt.x;
        coords[i * 3 + 1] = pnt.y;
        coords[i * 3 + 2] = pnt.z;
      }
    } else // dimen == 2
    {
      Vector3d nrm = new Vector3d();
      setRandom(nrm, RealScalar.of(-1), RealScalar.of(1), random);
      nrm.normalize();
      for (int i = 0; i < num; i++) { // compute a random point and project it to the plane
        Vector3d perp = new Vector3d();
        setRandom(pnt, RealScalar.of(-1), RealScalar.of(1), random);
        perp.scale(pnt.dot(nrm), nrm);
        pnt.sub(perp);
        pnt.add(base);
        randomlyPerturb(pnt, tol);
        coords[i * 3 + 0] = pnt.x;
        coords[i * 3 + 1] = pnt.y;
        coords[i * 3 + 2] = pnt.z;
      }
    }
    return coords;
  }

  /** Returns the coordinates for <code>num</code> points whose x, y, and
   * z values are randomly chosen to lie within a sphere.
   *
   * @param num number of points to produce
   * @param radius radius of the sphere
   * @return array of coordinate values */
  public Tensor randomSphericalPoints(int num, Scalar radius) {
    Tensor coords = Tensors.empty();
    for (int i = 0; i < num;) {
      Vector3d pnt = new Vector3d();
      setRandom(pnt, radius.negate(), radius, random);
      if (Scalars.lessEquals(pnt.norm(), radius)) {
        coords.append(pnt.toTensor());
        i++;
      }
    }
    return coords;
  }

  /** Returns the coordinates for <code>num</code> points whose x, y, and
   * z values are each randomly chosen to lie within a specified
   * range, and then clipped to a maximum absolute
   * value. This means a large number of points
   * may lie on the surface of cube, which is useful
   * for creating degenerate convex hull situations.
   *
   * @param num number of points to produce
   * @param range coordinate values will lie between -range and
   * range, before clipping
   * @param max maximum absolute value to which the coordinates
   * are clipped
   * @return array of coordinate values */
  public Tensor randomCubedPoints(int num, Scalar range, Scalar max) {
    Tensor coords = Array.zeros(num, 3);
    for (int i = 0; i < num; i++) {
      for (int k = 0; k < 3; k++) {
        Scalar x = range.add(range).multiply(RealScalar.of(random.nextDouble() - 0.5));
        if (Scalars.lessThan(max, x)) { // x > max
          x = max;
        } else if (Scalars.lessThan(x, max.negate())) {
          x = max.negate();
        }
        coords.set(x, i, k);
        // [i * 3 + k] = x;
      }
    }
    return coords;
  }

  private Scalar[] shuffleCoords(Scalar[] coords) {
    int num = coords.length / 3;
    for (int i = 0; i < num; i++) {
      int i1 = random.nextInt(num);
      int i2 = random.nextInt(num);
      for (int k = 0; k < 3; k++) {
        Scalar tmp = coords[i1 * 3 + k];
        coords[i1 * 3 + k] = coords[i2 * 3 + k];
        coords[i2 * 3 + k] = tmp;
      }
    }
    return coords;
  }

  /** Returns randomly shuffled coordinates for points on a
   * three-dimensional grid, with a presecribed width between each point.
   *
   * @param gridSize number of points in each direction,
   * so that the total number of points produced is the cube of
   * gridSize.
   * @param width distance between each point along a particular
   * direction
   * @return array of coordinate values */
  public Tensor randomGridPoints(int gridSize, Scalar width) {
    // gridSize gives the number of points across a given dimension
    // any given coordinate indexed by i has value
    // (i/(gridSize-1) - 0.5)*width
    Tensor coords = Tensors.empty();
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        for (int k = 0; k < gridSize; k++) {
          coords.append(Tensors.of( //
              RealScalar.of(i / (double) (gridSize - 1) - 0.5).multiply(width), //
              RealScalar.of(j / (double) (gridSize - 1) - 0.5).multiply(width), //
              RealScalar.of(k / (double) (gridSize - 1) - 0.5).multiply(width)));
        }
      }
    }
    int[] index = RandomPermutation.of(coords.length());
    return Tensor.of(IntStream.of(index).mapToObj(coords::get));
  }

  void explicitFaceCheck(ConvexHull3D hull, int[][] checkFaces) throws Exception {
    int[][] faceIndices = hull.getFaces();
    if (faceIndices.length != checkFaces.length) {
      throw new Exception("Error: " + faceIndices.length + " faces vs. " + checkFaces.length);
    }
    // translate face indices back into original indices
    int[] vtxIndices = hull.getVertexPointIndices();
    for (int j = 0; j < faceIndices.length; j++) {
      int[] idxs = faceIndices[j];
      for (int k = 0; k < idxs.length; k++) {
        idxs[k] = vtxIndices[idxs[k]];
      }
    }
    for (int i = 0; i < checkFaces.length; i++) {
      int[] cf = checkFaces[i];
      int j;
      for (j = 0; j < faceIndices.length; j++) {
        if (faceIndices[j] != null) {
          if (faceIndicesEqual(cf, faceIndices[j])) {
            faceIndices[j] = null;
            break;
          }
        }
      }
      if (j == faceIndices.length) {
        String s = "";
        for (int k = 0; k < cf.length; k++) {
          s += cf[k] + " ";
        }
        throw new Exception("Error: face " + s + " not found");
      }
    }
  }

  int cnt = 0;

  void singleTest(Scalar[] coords, int[][] checkFaces) throws Exception {
    ConvexHull3D hull = new ConvexHull3D();
    hull.setDebug(debugEnable);
    hull.build(coords);
    if (triangulate) {
      hull.triangulate();
    }
    if (!hull.check(System.out)) {
      throw Throw.of("");
    }
    if (checkFaces != null) {
      explicitFaceCheck(hull, checkFaces);
    }
    if (degeneracyTest != NO_DEGENERACY) {
      degenerateTest(hull, coords);
    }
  }

  Scalar[] addDegeneracy(int type, Scalar[] coords, ConvexHull3D hull) {
    int numv = coords.length / 3;
    int[][] faces = hull.getFaces();
    Scalar[] coordsx = ScalarArray.ofVector(Array.zeros(coords.length + faces.length * 3));
    for (int i = 0; i < coords.length; i++) {
      coordsx[i] = coords[i];
    }
    Scalar[] lam = ScalarArray.ofVector(Array.zeros(3));
    Scalar eps = hull.getDistanceTolerance();
    for (int i = 0; i < faces.length; i++) {
      // random point on an edge
      lam[0] = RealScalar.of(random.nextDouble());
      lam[1] = RealScalar.ONE.subtract(lam[0]);
      lam[2] = RealScalar.ZERO;
      if (type == VERTEX_DEGENERACY && (i % 2 == 0)) {
        lam[0] = RealScalar.ONE;
        lam[1] = lam[2] = RealScalar.ZERO;
      }
      for (int j = 0; j < 3; j++) {
        int vtxi = faces[i][j];
        for (int k = 0; k < 3; k++) {
          // coordsx[numv * 3 + k] += lam[j] * coords[vtxi * 3 + k] + epsScale * eps * (rand.nextDouble() - 0.5);
          coordsx[numv * 3 + k] = coordsx[numv * 3 + k]
              .add(lam[j].multiply(coords[vtxi * 3 + k]).add(Times.of(epsScale, eps, RealScalar.of(random.nextDouble() - 0.5))));
        }
      }
      numv++;
    }
    shuffleCoords(coordsx);
    return coordsx;
  }

  void degenerateTest(ConvexHull3D hull, Scalar[] coords) {
    Scalar[] coordsx = addDegeneracy(degeneracyTest, coords, hull);
    ConvexHull3D xhull = new ConvexHull3D();
    xhull.setDebug(debugEnable);
    try {
      xhull.build(coordsx);
      if (triangulate) {
        xhull.triangulate();
      }
    } catch (Exception e) {
      for (int i = 0; i < coordsx.length / 3; i++) {
        System.out.println(coordsx[i * 3 + 0] + ", " + coordsx[i * 3 + 1] + ", " + coordsx[i * 3 + 2] + ", ");
      }
    }
    if (!xhull.check(System.out)) {
      throw Throw.of("");
    }
  }

  void rotateCoords(Scalar[] res, Scalar[] xyz, double roll, double pitch, double yaw) {
    double sroll = Math.sin(roll);
    double croll = Math.cos(roll);
    double spitch = Math.sin(pitch);
    double cpitch = Math.cos(pitch);
    double syaw = Math.sin(yaw);
    double cyaw = Math.cos(yaw);
    double m00 = croll * cpitch;
    double m10 = sroll * cpitch;
    double m20 = -spitch;
    double m01 = croll * spitch * syaw - sroll * cyaw;
    double m11 = sroll * spitch * syaw + croll * cyaw;
    double m21 = cpitch * syaw;
    double m02 = croll * spitch * cyaw + sroll * syaw;
    double m12 = sroll * spitch * cyaw - croll * syaw;
    double m22 = cpitch * cyaw;
    double x, y, z;
    for (int i = 0; i < xyz.length - 2; i += 3) {
      res[i + 0] = RealScalar.of(m00).multiply(xyz[i + 0]).add(RealScalar.of(m01).multiply(xyz[i + 1])).add(RealScalar.of(m02).multiply(xyz[i + 2]));
      res[i + 1] = RealScalar.of(m10).multiply(xyz[i + 0]).add(RealScalar.of(m11).multiply(xyz[i + 1])).add(RealScalar.of(m12).multiply(xyz[i + 2]));
      res[i + 2] = RealScalar.of(m20).multiply(xyz[i + 0]).add(RealScalar.of(m21).multiply(xyz[i + 1])).add(RealScalar.of(m22).multiply(xyz[i + 2]));
    }
  }

  void printCoords(Scalar[] coords) {
    int nump = coords.length / 3;
    for (int i = 0; i < nump; i++) {
      System.out.println(coords[i * 3 + 0] + ", " + coords[i * 3 + 1] + ", " + coords[i * 3 + 2] + ", ");
    }
  }

  void testException(Scalar[] coords, String msg) {
    ConvexHull3D hull = new ConvexHull3D();
    Exception ex = null;
    try {
      hull.build(coords);
    } catch (Exception e) {
      ex = e;
    }
    if (ex == null) {
      System.out.println("Expected exception " + msg);
      System.out.println("Got no exception");
      System.out.println("Input pnts:");
      printCoords(coords);
      throw Throw.of("");
    } else if (ex.getMessage() == null || !ex.getMessage().equals(msg)) {
      System.out.println("Expected exception " + msg);
      System.out.println("Got exception " + ex.getMessage());
      System.out.println("Input pnts:");
      printCoords(coords);
      throw Throw.of("");
    }
  }

  void test(Tensor coords, int[][] checkFaces) throws Exception {
    test(ScalarArray.ofVector(Flatten.of(coords)), checkFaces);
  }

  void test(Scalar[] coords, int[][] checkFaces) throws Exception {
    double[][] rpyList = new double[][] { { 0, 0, 0 }, { 10, 20, 30 }, { -45, 60, 91 }, { 125, 67, 81 } };
    Scalar[] xcoords = ScalarArray.ofVector(Array.zeros(coords.length));
    singleTest(coords, checkFaces);
    if (testRotation) {
      for (int i = 0; i < rpyList.length; i++) {
        double[] rpy = rpyList[i];
        rotateCoords(xcoords, coords, //
            Math.toRadians(rpy[0]), //
            Math.toRadians(rpy[1]), //
            Math.toRadians(rpy[2]));
        singleTest(xcoords, checkFaces);
      }
    }
  }

  /** Runs timing tests on QuickHull3D, and prints
   * the results to System.out. */
  public void timingTests() {
    long t0, t1;
    int n = 10;
    ConvexHull3D hull = new ConvexHull3D();
    System.out.println("warming up ... ");
    for (int i = 0; i < 2; i++) {
      Tensor coords = randomSphericalPoints(10000, RealScalar.of(1.0));
      hull.build(coords);
    }
    int cnt = 10;
    for (int i = 0; i < 4; i++) {
      n *= 10;
      Tensor coords = randomSphericalPoints(n, RealScalar.of(1.0));
      t0 = System.currentTimeMillis();
      for (int k = 0; k < cnt; k++) {
        hull.build(coords);
      }
      t1 = System.currentTimeMillis();
      System.out.println(n + " points: " + (t1 - t0) / (double) cnt + " msec");
    }
  }
}
