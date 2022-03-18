// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;

public class JoinTest {
  @Test
  public void testVectors() {
    Tensor v1 = Tensors.vector(2, 3, 4);
    Tensor v2 = Tensors.vector(0, -3);
    Tensor v3 = Tensors.vector();
    Tensor v4 = Tensors.vector(8, 99);
    Tensor j1 = Join.of(v1, v2, v3, v4);
    Tensor re = Tensors.vector(2, 3, 4, 0, -3, 8, 99);
    assertEquals(j1, re);
    assertEquals(Join.of(v1), v1);
    assertEquals(Join.of(), Tensors.empty());
  }

  @Test
  public void testExample() {
    Tensor v1 = Join.of(Tensors.vector(2, 3, 4), Tensors.vector(9, 8));
    Tensor re = Tensors.vector(2, 3, 4, 9, 8);
    assertEquals(v1, re);
  }

  @Test
  public void testMatrices() {
    Tensor m1 = Tensors.matrixInt(new int[][] { //
        { 1, 2 }, { 0, 5 }, { 9, 8 } });
    Tensor v2 = Tensors.vector(0, -3);
    Tensor j1 = Tensors.fromString("{{1, 2}, {0, 5}, {9, 8}, 0, -3}");
    assertEquals(Join.of(m1, v2), j1);
    Tensor m2 = Tensors.matrixDouble(new double[][] { //
        { 0.5, 0.25 } });
    Tensor j2 = Join.of(m1, m2, m1, m2);
    assertEquals(Dimensions.of(j2), Arrays.asList(8, 2));
    Tensor c2 = Tensors.fromString( //
        "{{1, 2}, {0, 5}, {9, 8}, {0.5, 0.25}, {1, 2}, {0, 5}, {9, 8}, {0.5, 0.25}}");
    assertEquals(j2, c2);
  }

  @Test
  public void testMatrices2() {
    Tensor m1 = Tensors.matrixInt(new int[][] { //
        { 1, 2 }, //
        { 0, 5 }, //
        { 9, 8 } });
    Tensor j2 = Join.of(1, m1, m1, m1);
    assertEquals(Dimensions.of(j2), Arrays.asList(3, 6));
    Tensor j3 = Join.of(0, j2, j2, j2);
    assertEquals(Dimensions.of(j3), Arrays.asList(9, 6));
  }

  @Test
  public void testRank3() {
    // t1 has dimensions [1, 3, 2]
    Tensor t1 = Tensors.of( //
        Tensors.matrixInt(new int[][] { //
            { 1, 2 }, //
            { 0, 5 }, //
            { 9, 8 } }));
    Tensor j2 = Join.of(0, t1, t1, t1, t1);
    assertEquals(Dimensions.of(j2), Arrays.asList(4, 3, 2));
    Tensor j3 = Join.of(2, j2, j2, j2);
    assertEquals(Dimensions.of(j3), Arrays.asList(4, 3, 6));
  }

  @Test
  public void testRank3d0() {
    Tensor tensor = Join.of(Array.zeros(3, 3, 3).unmodifiable(), Array.zeros(3, 3, 3).unmodifiable());
    assertEquals(Dimensions.of(tensor), Arrays.asList(6, 3, 3));
    tensor.set(t -> t.append(RealScalar.ONE), Tensor.ALL);
  }

  @Test
  public void testRank3d1() {
    Tensor tensor = Join.of(1, Array.zeros(3, 3, 3).unmodifiable(), Array.zeros(3, 3, 3).unmodifiable());
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 6, 3));
    tensor.set(t -> t.append(RealScalar.ONE), Tensor.ALL);
  }

  @Test
  public void testRank3d2() {
    Tensor tensor = Join.of(2, Array.zeros(3, 3, 3).unmodifiable(), Array.zeros(3, 3, 3).unmodifiable());
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 3, 6));
    tensor.set(t -> t.append(RealScalar.ONE), Tensor.ALL);
  }

  @Test
  public void testEmpty() {
    Tensor v1 = Tensors.vector(2, 3, 4);
    Tensor ap = Join.of(Tensors.empty(), v1);
    assertEquals(ap, v1);
  }

  @Test
  public void testSingle() {
    Tensor v1 = Tensors.vector(2, 3, 4);
    Tensor ap = Join.of(v1);
    assertEquals(ap, v1);
  }

  @Test
  public void testFailScalar() {
    // in Mathematica Join that involves scalars is not defined!
    AssertFail.of(() -> Join.of(RealScalar.of(2)));
  }

  @Test
  public void testFailScalarTwo() {
    AssertFail.of(() -> Join.of(RealScalar.of(2), RealScalar.of(3)));
  }

  @Test
  public void testFailVectorScalar() {
    AssertFail.of(() -> Join.of(Tensors.vector(0, 1, 2), RealScalar.of(3)));
  }
}
