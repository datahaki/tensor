// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class DotTest {
  /** @param tensor
   * @param v's
   * @return ( ... ( ( tensor . v[0] ) . v[1] ). ... ) . v[end-1] */
  private static Tensor dot(Tensor tensor, Tensor... v) {
    if (v.length == 0)
      return tensor.copy();
    for (Tensor value : v)
      tensor = tensor.dot(value);
    return tensor;
  }

  @Test
  void testDot1() {
    Tensor m1 = Tensors.matrix((i, j) -> RealScalar.of(2 + i - 3 * j), 3, 4);
    Tensor m2 = Tensors.matrix((i, j) -> RealScalar.of(8 + 2 * i + 9 * j), 4, 2);
    Tensor m3 = Tensors.matrix((i, j) -> RealScalar.of(-3 - 7 * i + j), 2, 7);
    Tensor m4 = Transpose.of( //
        Tensors.matrix((i, j) -> RealScalar.of(100 - 7 * i + j), 2, 7));
    Tensor d1 = Dot.of(m1, m2, m3, m4);
    assertEquals(d1, m1.dot(m2).dot(m3).dot(m4));
  }

  @Test
  void testDot2() {
    Tensor m1 = Tensors.matrix((i, j) -> RealScalar.of(2 + i - 3 * j), 3, 4);
    Tensor m2 = Tensors.vector(i -> RealScalar.of(8 + 2 * i), 4);
    Tensor m3 = Tensors.vector(i -> RealScalar.of(2 + 2 * i), 3);
    Tensor d1 = Dot.of(m1, m2, m3);
    assertEquals(d1, m1.dot(m2).dot(m3));
  }

  @Test
  void testCopy() {
    Tensor in = Array.zeros(2);
    Tensor re = Dot.of(in);
    re.set(RealScalar.ONE::add, Tensor.ALL);
    assertNotEquals(in, re);
    assertEquals(in, Array.zeros(2));
  }

  @Test
  void testIdentity() {
    Tensor m = IdentityMatrix.of(7);
    Tensor d = Dot.of(m, m, m, m);
    assertEquals(m, d);
  }

  @Test
  void testSimple1() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor x = RandomVariate.of(distribution, 5);
    Tensor y = RandomVariate.of(distribution, 5);
    assertEquals(Dot.of(x), dot(x));
    assertEquals(Dot.of(x, y), dot(x, y));
  }

  @Test
  void testSimple1Empty() {
    Tensor x = Tensors.empty();
    Tensor y = Tensors.empty();
    assertEquals(Dot.of(x), dot(x));
    assertEquals(Dot.of(x, y), dot(x, y));
    assertEquals(Dot.of(x, y), x.dot(y));
  }

  @Test
  void testSimple1Fail() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor x = RandomVariate.of(distribution, 5);
    Tensor y = RandomVariate.of(distribution, 6);
    assertThrows(IllegalArgumentException.class, () -> Dot.of(x, y));
  }

  @Test
  void testSimple2() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor x = RandomVariate.of(distribution, 3);
    Tensor y = RandomVariate.of(distribution, 4);
    Tensor m = RandomVariate.of(distribution, 4, 3);
    assertEquals(Dot.of(m), dot(m));
    assertEquals(Dot.of(m, x), dot(m, x));
    assertEquals(Dot.of(m, x, y), dot(m, x, y));
    assertEquals(Dot.of(y, m, x), dot(y, m, x));
  }

  @Test
  void testSimple2b() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor x = RandomVariate.of(distribution, 3);
    Tensor y = RandomVariate.of(distribution, 5);
    Tensor m1 = RandomVariate.of(distribution, 4, 3);
    Tensor m2 = RandomVariate.of(distribution, 4, 5);
    assertEquals(Dot.of(m1, x, m2, y), dot(m1, x, m2, y));
    assertEquals(Dot.of(m2, y, m1, x), dot(m2, y, m1, x));
  }

  @Test
  void testSimple3() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor x = RandomVariate.of(distribution, 3);
    Tensor y = RandomVariate.of(distribution, 4);
    Tensor z = RandomVariate.of(distribution, 5);
    Tensor m = RandomVariate.of(distribution, 3, 4, 5);
    assertEquals(Dot.of(m, z, y), dot(m, z, y));
    assertEquals(Dot.of(m, z, y, x), dot(m, z, y, x));
    assertEquals(Dot.of(x, m, z), dot(x, m, z));
    assertEquals(Dot.of(x, m, z, y), dot(x, m, z, y));
  }

  @Test
  void testExampleP156() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor m1 = RandomVariate.of(distribution, 50, 10);
    Tensor m2 = RandomVariate.of(distribution, 10, 20);
    Tensor m3 = RandomVariate.of(distribution, 20, 5);
    Dot dot = new Dot(m1, m2, m3);
    assertEquals(dot.multiplications(), 3500);
    assertEquals(dot.dimensions(), Arrays.asList(50, 5));
  }

  @Test
  void testExampleP159() {
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

  @Test
  void testExample1() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor m1 = RandomVariate.of(distribution, 5, 5, 3);
    Tensor m2 = RandomVariate.of(distribution, 3, 2, 4);
    Tensor m3 = RandomVariate.of(distribution, 4, 3, 5);
    Dot dot = new Dot(m1, m2, m3);
    assertEquals(dot.multiplications(), 2610); // otherwise 3600
    assertEquals(dot.dimensions(), Arrays.asList(5, 5, 2, 3, 5));
  }

  @Test
  void testExample2() {
    Distribution distribution = DiscreteUniformDistribution.of(-3, 3);
    Tensor m1 = RandomVariate.of(distribution, 5, 5, 3, 7);
    Tensor mx = RandomVariate.of(distribution, 7);
    Tensor m2 = RandomVariate.of(distribution, 3, 2, 4);
    Tensor m3 = RandomVariate.of(distribution, 4, 3, 5);
    Dot dot = new Dot(m1, mx, m2, m3);
    assertEquals(dot.multiplications(), 2610); // otherwise 3600
    assertEquals(dot.dimensions(), Arrays.asList(5, 5, 2, 3, 5));
  }

  @Test
  void testCombine() {
    assertEquals(Dot.combine(List.of(2), Arrays.asList(2, 3, 4, 5)), Arrays.asList(3, 4, 5));
    assertEquals(Dot.combine(List.of(3), List.of(3)), List.of());
    assertEquals(Dot.combine(Arrays.asList(1, 2, 3), Arrays.asList(3, 4, 5)), Arrays.asList(1, 2, 4, 5));
  }

  @ParameterizedTest
  @MethodSource(value = "test.TestDistributions#distributions")
  void testComparison(Distribution distribution) {
    Tensor v = RandomVariate.of(distribution, 50);
    Tensor m1 = RandomVariate.of(distribution, 50, 3);
    Tensor m2 = RandomVariate.of(distribution, 3, 50);
    v.dot(m1).dot(m2);
    assertEquals(new Dot(v, m1, m2).multiplications(), 0); // immediately collapse due to vector
    assertEquals(new Dot(m1, m2).multiplications(), 3 * 50 * 50);
  }

  @Test
  void testCombineFail() {
    assertThrows(IllegalArgumentException.class, () -> Dot.combine(Arrays.asList(2, 2), Arrays.asList(3, 4, 5)));
  }

  @Test
  void testDot0Fail() {
    assertThrows(ArrayIndexOutOfBoundsException.class, Dot::of);
  }
}
