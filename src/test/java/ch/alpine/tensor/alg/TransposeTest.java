// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.stream.Stream;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.lie.Permutations;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.ExponentialDistribution;
import ch.alpine.tensor.pdf.NegativeBinomialDistribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.spa.Normal;
import junit.framework.TestCase;

public class TransposeTest extends TestCase {
  public void testScalar() {
    Tensor scalar = DoubleScalar.NEGATIVE_INFINITY;
    assertEquals(Transpose.of(scalar, new int[] {}), scalar);
  }

  public void testVector() {
    Tensor v = Tensors.vector(2, 3, 4, 5);
    Tensor r = Transpose.of(v, 0);
    assertEquals(v, r);
  }

  public void testMatrix() {
    // [[0, 1, 2, 3], [4, 5, 6, 7], [8, 9, 10, 11]]
    Tensor m = Tensors.matrix((i, j) -> RealScalar.of(i * 4 + j), 3, 4);
    Tensor t = Transpose.of(m, 1, 0);
    assertEquals(t.toString(), "{{0, 4, 8}, {1, 5, 9}, {2, 6, 10}, {3, 7, 11}}");
    Tensor r = Transpose.of(m);
    assertEquals(r.toString(), "{{0, 4, 8}, {1, 5, 9}, {2, 6, 10}, {3, 7, 11}}");
  }

  public void testMatrixWithVectors() {
    Tensor tensor = Tensors.fromString("{{1, {2, 2}}, {{3}, 4}, {5, {6}}}");
    Tensor transp = Transpose.of(tensor);
    assertEquals(transp, Tensors.fromString("{{1, {3}, 5}, {{2, 2}, 4, {6}}}"));
  }

  public void testTranspose2() {
    // [[[0, 1, 2], [3, 4, 5]], [[6, 7, 8], [9, 10, 11]]]
    Tensor m = Partition.of(Tensors.matrix((i, j) -> RealScalar.of(i * 3 + j), 4, 3), 2);
    Tensor t = Transpose.of(m, 0, 1, 2);
    assertEquals(t, Tensors.fromString("{{{0, 1, 2}, {3, 4, 5}}, {{6, 7, 8}, {9, 10, 11}}}"));
  }

  /** m = Array[0 &, {2, 3, 5}];
   * Dimensions[Transpose[m, {3, 2, 1}]] == {5, 3, 2}
   * Dimensions[Transpose[m, {2, 3, 1}]] == {5, 2, 3} */
  public void testTranspose3() {
    Tensor m = Partition.of(Tensors.matrix((i, j) -> RationalScalar.of(i * 5 + j, 1), 6, 5), 3);
    assertEquals(m, Tensors.fromString( //
        "{{{0, 1, 2, 3, 4}, {5, 6, 7, 8, 9}, {10, 11, 12, 13, 14}}, {{15, 16, 17, 18, 19}, {20, 21, 22, 23, 24}, {25, 26, 27, 28, 29}}}"));
    assertEquals(Dimensions.of(m), Arrays.asList(2, 3, 5));
    {
      Tensor t = Transpose.of(m, 2, 1, 0);
      assertEquals(Dimensions.of(t), Arrays.asList(5, 3, 2));
      assertEquals(t, Tensors.fromString( //
          "{{{0, 15}, {5, 20}, {10, 25}}, {{1, 16}, {6, 21}, {11, 26}}, {{2, 17}, {7, 22}, {12, 27}}, {{3, 18}, {8, 23}, {13, 28}}, {{4, 19}, {9, 24}, {14, 29}}}"));
    }
    {
      Tensor t = Transpose.of(m, 1, 2, 0);
      assertEquals(Dimensions.of(t), Arrays.asList(5, 2, 3));
      assertEquals(t, Tensors.fromString( //
          "{{{0, 5, 10}, {15, 20, 25}}, {{1, 6, 11}, {16, 21, 26}}, {{2, 7, 12}, {17, 22, 27}}, {{3, 8, 13}, {18, 23, 28}}, {{4, 9, 14}, {19, 24, 29}}}"));
    }
  }

  public void testModify() {
    Tensor m = Tensors.matrixInt(new int[][] { { 1, 2 }, { 2, 4 } });
    Tensor mt = Transpose.of(m);
    mt.set(i -> RealScalar.ZERO, 1, 1);
    assertFalse(m.equals(mt));
  }

  public void testZeros() {
    assertEquals(Array.zeros(2, 10), Transpose.of(Array.zeros(10, 2)));
  }

  public void testRep() {
    Integer[] input = new Integer[] { 3, 2, 6, 0 };
    int[] copy = Stream.of(input).mapToInt(Integer::intValue).toArray();
    assertEquals(copy[0], 3);
    assertEquals(copy[2], 6);
  }

  public void testFirstDimensions() {
    Tensor randn = RandomVariate.of(NormalDistribution.standard(), 3, 4, 5);
    assertEquals(Transpose.of(randn, 0), randn);
    assertEquals(Transpose.of(randn, 0, 1), randn);
    assertEquals(Transpose.of(randn, 0, 1, 2), randn);
    Tensor trans = Transpose.of(randn, 1, 0, 2);
    assertEquals(trans, Transpose.of(randn));
  }

  public void testIdentity() {
    Tensor randn = RandomVariate.of(ExponentialDistribution.standard(), 3, 4, 2);
    assertEquals(randn, Transpose.of(randn, new int[] {}));
    assertEquals(Tensors.empty(), Transpose.of(Tensors.empty(), new int[] {}));
    assertEquals(Tensors.vector(1, 2, 3), Transpose.of(Tensors.vector(1, 2, 3), new int[] {}));
  }

  public void testComparison() {
    Tensor randn = RandomVariate.of(NormalDistribution.standard(), 6, 5, 4);
    ArrayQ.require(randn);
    Tensor trans = Transpose.of(randn, 1, 2, 0);
    assertEquals(Dimensions.of(trans), Arrays.asList(4, 6, 5));
  }

  public void testIncomplete() {
    Tensor randn = RandomVariate.of(NormalDistribution.standard(), 2, 5, 4, 3);
    Tensor array = Transpose.of(randn, 1, 2, 0);
    assertEquals(Transpose.of(randn, 1, 2, 0, 3), array);
  }

  public void testSingle() {
    Tensor randn = RandomVariate.of(NormalDistribution.standard(), 2, 5, 4, 3);
    Tensor array = Transpose.of(randn, 0);
    assertEquals(Transpose.of(randn, 0, 1, 2, 3), array);
  }

  public void testMix() {
    Tensor b0 = Tensors.fromString("{{0, 0, 1}, {0, 0, 0}, {0, 0, 0}}");
    Tensor b1 = Tensors.fromString("{{0, 0, 0}, {0, 0, 1}, {0, 0, 0}}");
    Tensor b2 = LeviCivitaTensor.of(3).get(2).negate();
    Tensor basis = Tensors.of(b0, b1, b2);
    Tensor _full = Normal.of(basis);
    Permutations.stream(Range.of(0, 3)) //
        .map(Primitives::toIntArray) //
        .forEach(p -> assertEquals(Transpose.of(basis, p), Transpose.of(_full, p)));
  }

  public void testNonArray() {
    Tensor tensor = Tensors.fromString("{{0, 1, {2, 3, 4}}, {5, 6, 7}}");
    // mathematica gives ............... {{0, 5}, {1, 6}, {{2, 3, 4}, 7}}
    Tensor correct = Tensors.fromString("{{0, 5}, {1, 6}, {{2, 3, 4}, 7}}");
    assertTrue(2 <= TensorRank.of(tensor));
    Tensor r0 = Transpose.of(tensor, 1, 0);
    assertEquals(r0, correct);
  }

  public void testDotT() {
    Distribution distribution = NegativeBinomialDistribution.of(3, 0.7);
    Tensor a = RandomVariate.of(distribution, 2, 4);
    Tensor b = RandomVariate.of(distribution, 4, 3);
    assertEquals(Transpose.of(a.dot(b)), Transpose.of(b).dot(Transpose.of(a)));
  }
}
