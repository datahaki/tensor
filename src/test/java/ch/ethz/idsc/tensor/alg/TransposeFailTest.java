// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TransposeFailTest extends TestCase {
  public void testScalarFail() {
    Tensor v = DoubleScalar.NEGATIVE_INFINITY;
    AssertFail.of(() -> Transpose.of(v));
    AssertFail.of(() -> Transpose.of(v, new Integer[] { 2 }));
  }

  public void testVectorFail() {
    AssertFail.of(() -> Transpose.of(Tensors.vector(2, 3, 4, 5)));
  }

  public void testEmpty2() {
    Tensor empty2 = Tensors.fromString("{{}, {}}");
    assertEquals(Transpose.of(empty2), Tensors.empty());
    AssertFail.of(() -> Transpose.of(Transpose.of(empty2)));
  }

  public void testRankFail() {
    Transpose.of(Array.zeros(1, 3, 2), 1, 2, 0);
    AssertFail.of(() -> Transpose.of(Array.zeros(3, 3, 3), 1, 0));
    AssertFail.of(() -> Transpose.of(Array.zeros(3, 3, 2), 3, 2, 1, 0));
  }

  public void testFail() {
    AssertFail.of(() -> Transpose.nonArray(Array.zeros(2, 3), 1));
    AssertFail.of(() -> Transpose.nonArray(Array.zeros(2, 3), 2, 0));
    AssertFail.of(() -> Transpose.nonArray(Array.zeros(2, 3), 0, -1));
  }

  public void testFail2() {
    AssertFail.of(() -> Transpose.of(Tensors.fromString("{{1, 2}, {3, 4, 5}}")));
    AssertFail.of(() -> Transpose.of(Tensors.fromString("{{1, 2, 3}, {4, 5}}")));
  }
}
