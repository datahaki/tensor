// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.lie.Permutations;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.Binomial;
import ch.alpine.tensor.num.Pi;

class SubsetsTest {
  @Test
  void testCardinality() {
    Tensor vector = Tensors.vector(3, 4, 5, 6);
    for (int k = 0; k <= vector.length(); ++k) {
      assertEquals(Subsets.stream(vector, k).count(), Binomial.of(4, k).number().intValue());
      assertEquals(Subsets.of(vector, k).length(), Binomial.of(4, k).number().intValue());
    }
  }

  @Test
  void testZero() {
    assertEquals(Subsets.of(Tensors.empty(), 0), Tensors.fromString("{{}}"));
    assertEquals(Subsets.of(Tensors.vector(1, 2, 3), 0), Tensors.fromString("{{}}"));
    assertEquals(Subsets.of(IdentityMatrix.of(2), 0), Tensors.fromString("{{}}"));
  }

  @Test
  void testOne() {
    Tensor tensor = Subsets.of(Tensors.vector(2, 3, 4), 1);
    assertEquals(tensor, Tensors.fromString("{{2}, {3}, {4}}"));
  }

  @Test
  void testTwo() {
    Tensor tensor = Subsets.of(Tensors.vector(2, 3, 4), 2);
    assertEquals(tensor, Tensors.fromString("{{2, 3}, {2, 4}, {3, 4}}"));
    assertEquals( //
        Subsets.of(Tensors.vector(2, 3, 4), 2), //
        Tensor.of(Subsets.stream(Tensors.vector(2, 3, 4), 2)));
  }

  @Test
  void testThree() {
    Tensor tensor = Subsets.of(Tensors.vector(2, 3, 4), 3);
    assertEquals(tensor, Tensors.fromString("{{2, 3, 4}}"));
  }

  /** Reference:
   * "Mathematische Raetsel und Spiele" Loyd/Garnder
   * p.118, Problem 86 */
  @Test
  void testDubletten() {
    Scalar _96 = RealScalar.of(96);
    Tensor tensor = Tensors.vector(1, 2, 3, 5, 10, 20, 25, 50);
    Tensor solution = Tensors.empty();
    for (Tensor subset : Subsets.of(tensor, 3)) {
      for (Tensor permutation : Permutations.of(Tensors.vector(1, 1, 4)))
        if (permutation.dot(subset).equals(_96))
          solution.append(Tensors.of(permutation, subset));
      for (Tensor permutation : Permutations.of(Tensors.vector(1, 2, 3)))
        if (permutation.dot(subset).equals(_96))
          solution.append(Tensors.of(permutation, subset));
      for (Tensor permutation : Permutations.of(Tensors.vector(2, 2, 2)))
        if (permutation.dot(subset).equals(_96))
          solution.append(Tensors.of(permutation, subset));
    }
    assertEquals(solution.length(), 3);
  }

  @Test
  void testNegativeFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> Subsets.of(Tensors.vector(2, 3, 4), -1));
  }

  @Test
  void testNegative2Fail() {
    assertThrows(IndexOutOfBoundsException.class, () -> Subsets.of(Tensors.empty(), -1));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> Subsets.of(Pi.HALF, 2));
  }
}
