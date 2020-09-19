// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import junit.framework.TestCase;

public class SubsetsTest extends TestCase {
  public void testCardinality() {
    Tensor vector = Tensors.vector(3, 4, 5, 6);
    for (int k = 0; k <= vector.length(); ++k) {
      assertEquals(Subsets.stream(vector, k).count(), Binomial.of(4, k).number().intValue());
      assertEquals(Subsets.of(vector, k).length(), Binomial.of(4, k).number().intValue());
    }
  }

  public void testZero() {
    assertEquals(Subsets.of(Tensors.empty(), 0), Tensors.fromString("{{}}"));
    assertEquals(Subsets.of(Tensors.vector(1, 2, 3), 0), Tensors.fromString("{{}}"));
    assertEquals(Subsets.of(IdentityMatrix.of(2), 0), Tensors.fromString("{{}}"));
  }

  public void testOne() {
    Tensor tensor = Subsets.of(Tensors.vector(2, 3, 4), 1);
    assertEquals(tensor, Tensors.fromString("{{2}, {3}, {4}}"));
  }

  public void testTwo() {
    Tensor tensor = Subsets.of(Tensors.vector(2, 3, 4), 2);
    assertEquals(tensor, Tensors.fromString("{{2, 3}, {2, 4}, {3, 4}}"));
    assertEquals( //
        Subsets.of(Tensors.vector(2, 3, 4), 2), //
        Tensor.of(Subsets.stream(Tensors.vector(2, 3, 4), 2)));
  }

  public void testThree() {
    Tensor tensor = Subsets.of(Tensors.vector(2, 3, 4), 3);
    assertEquals(tensor, Tensors.fromString("{{2, 3, 4}}"));
  }

  /** Reference:
   * "Mathematische Raetsel und Spiele" Loyd/Garnder
   * p.118, Problem 86 */
  public void testDubletten() {
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

  public void testNegativeFail() {
    try {
      Subsets.of(Tensors.vector(2, 3, 4), -1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNegative2Fail() {
    try {
      Subsets.of(Tensors.empty(), -1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testScalarFail() {
    try {
      Subsets.of(Pi.HALF, 2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
