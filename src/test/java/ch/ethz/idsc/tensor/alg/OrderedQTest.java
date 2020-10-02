// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Factorial;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class OrderedQTest extends TestCase {
  public void testCornerCases() {
    assertTrue(OrderedQ.of(Tensors.empty()));
    assertTrue(OrderedQ.of(Tensors.vector(1123)));
  }

  public void testSimple() {
    assertTrue(OrderedQ.of(Tensors.vector(1, 2)));
    assertFalse(OrderedQ.of(Tensors.vector(2, 1)));
    assertFalse(OrderedQ.of(Tensors.vector(0, 3, 1)));
    assertFalse(OrderedQ.of(Tensors.vector(1, 0, 2)));
    assertTrue(OrderedQ.of(Tensors.vector(1, 1, 2, 4, 4, 4)));
  }

  public void testPermutations() {
    for (int index = 0; index < 5; ++index) {
      assertEquals(Permutations.stream(Range.of(0, index)).count(), Factorial.of(index).number().intValue());
      long count = Permutations.stream(Range.of(0, index)) //
          .filter(OrderedQ::of) //
          .count();
      assertEquals(count, 1);
    }
  }

  public void testMatrixFail() {
    assertFalse(OrderedQ.of(IdentityMatrix.of(4)));
    assertTrue(OrderedQ.of(Reverse.of(IdentityMatrix.of(4))));
  }

  public void testRequire() {
    OrderedQ.require(Tensors.vector(1, 1, 2, 4, 4, 4));
    AssertFail.of(() -> OrderedQ.require(Tensors.vector(0, 3, 1)));
  }

  public void testScalarFail() {
    AssertFail.of(() -> OrderedQ.of(Pi.VALUE));
  }

  public void testUncomparable1Fail() {
    Tensor tensor = Tensors.fromString("{3[s], 4[s], 2[m]}");
    AssertFail.of(() -> OrderedQ.of(tensor));
  }

  public void testUncomparable2Fail() {
    Tensor tensor = Tensors.fromString("{3[s], 1[s], 2[m]}");
    AssertFail.of(() -> OrderedQ.of(tensor));
  }
}
