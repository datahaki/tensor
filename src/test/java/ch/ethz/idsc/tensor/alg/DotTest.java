// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Increment;
import junit.framework.TestCase;

public class DotTest extends TestCase {
  public void testDot1() {
    Tensor m1 = Tensors.matrix((i, j) -> RealScalar.of(2 + i - 3 * j), 3, 4);
    Tensor m2 = Tensors.matrix((i, j) -> RealScalar.of(8 + 2 * i + 9 * j), 4, 2);
    Tensor m3 = Tensors.matrix((i, j) -> RealScalar.of(-3 - 7 * i + j), 2, 7);
    Tensor m4 = Transpose.of( //
        Tensors.matrix((i, j) -> RealScalar.of(100 - 7 * i + j), 2, 7));
    Tensor d1 = Dot.of(m1, m2, m3, m4);
    assertEquals(d1, m1.dot(m2).dot(m3).dot(m4));
  }

  public void testDot2() {
    Tensor m1 = Tensors.matrix((i, j) -> RealScalar.of(2 + i - 3 * j), 3, 4);
    Tensor m2 = Tensors.vector(i -> RealScalar.of(8 + 2 * i), 4);
    Tensor m3 = Tensors.vector(i -> RealScalar.of(2 + 2 * i), 3);
    Tensor d1 = Dot.of(m1, m2, m3);
    assertEquals(d1, m1.dot(m2).dot(m3));
  }

  public void testCopy() {
    Tensor in = Array.zeros(2);
    Tensor re = Dot.of(in);
    re.set(Increment.ONE, Tensor.ALL);
    assertFalse(in.equals(re));
    assertEquals(in, Array.zeros(2));
  }

  public void testIdentity() {
    Tensor m = IdentityMatrix.of(7);
    Tensor d = Dot.of(m, m, m, m);
    assertEquals(m, d);
  }

  public void testSimple1() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor x = RandomVariate.of(distribution, 5);
    Tensor y = RandomVariate.of(distribution, 5);
    assertEquals(Dot.of(x), Dot.minimal(x));
    assertEquals(Dot.of(x, y), Dot.minimal(x, y));
  }

  public void testSimple2() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor x = RandomVariate.of(distribution, 3);
    Tensor y = RandomVariate.of(distribution, 4);
    Tensor m = RandomVariate.of(distribution, 4, 3);
    assertEquals(Dot.of(m), Dot.minimal(m));
    assertEquals(Dot.of(m, x), Dot.minimal(m, x));
    assertEquals(Dot.of(m, x, y), Dot.minimal(m, x, y));
    assertEquals(Dot.of(y, m, x), Dot.minimal(y, m, x));
  }

  public void testSimple3() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor x = RandomVariate.of(distribution, 3);
    Tensor y = RandomVariate.of(distribution, 4);
    Tensor z = RandomVariate.of(distribution, 5);
    Tensor m = RandomVariate.of(distribution, 3, 4, 5);
    assertEquals(Dot.of(m, z, y), Dot.minimal(m, z, y));
    assertEquals(Dot.of(m, z, y, x), Dot.minimal(m, z, y, x));
    assertEquals(Dot.of(x, m, z), Dot.minimal(x, m, z));
    assertEquals(Dot.of(x, m, z, y), Dot.minimal(x, m, z, y));
  }

  public void testExampleP156() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor m1 = RandomVariate.of(distribution, 50, 10);
    Tensor m2 = RandomVariate.of(distribution, 10, 20);
    Tensor m3 = RandomVariate.of(distribution, 20, 5);
    Dot dot = new Dot(m1, m2, m3);
    assertEquals(dot.multiplications(), 3500);
    assertEquals(dot.dimensions(), Arrays.asList(50, 5));
  }

  public void testExampleP159() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor m1 = RandomVariate.of(distribution, 6, 12);
    Tensor m2 = RandomVariate.of(distribution, 12, 20);
    Tensor m3 = RandomVariate.of(distribution, 20, 3);
    Tensor m4 = RandomVariate.of(distribution, 3, 10);
    Tensor m5 = RandomVariate.of(distribution, 10, 5);
    Tensor m6 = RandomVariate.of(distribution, 5, 18);
    Dot dot = new Dot(m1, m2, m3, m4, m5, m6);
    assertEquals(dot.multiplications(), 1680);
    Tensor res2 = Dot.of(m1, m2, m3, m4, m5, m6);
    assertEquals(dot.product(), res2);
  }

  public void testExample1() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor m1 = RandomVariate.of(distribution, 5, 5, 3);
    Tensor m2 = RandomVariate.of(distribution, 3, 2, 4);
    Tensor m3 = RandomVariate.of(distribution, 4, 3, 5);
    Dot dot = new Dot(m1, m2, m3);
    assertEquals(dot.multiplications(), 2610); // otherwise 3600
    assertEquals(dot.dimensions(), Arrays.asList(5, 5, 2, 3, 5));
  }

  public void testExample2() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor m1 = RandomVariate.of(distribution, 5, 5, 3, 7);
    Tensor mx = RandomVariate.of(distribution, 7);
    Tensor m2 = RandomVariate.of(distribution, 3, 2, 4);
    Tensor m3 = RandomVariate.of(distribution, 4, 3, 5);
    Dot dot = new Dot(m1, mx, m2, m3);
    assertEquals(dot.multiplications(), 2610); // otherwise 3600
    assertEquals(dot.dimensions(), Arrays.asList(5, 5, 2, 3, 5));
  }
}
