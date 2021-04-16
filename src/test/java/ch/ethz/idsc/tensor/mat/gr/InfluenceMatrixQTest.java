// code by jph
package ch.ethz.idsc.tensor.mat.gr;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class InfluenceMatrixQTest extends TestCase {
  public void testSimple() {
    Tensor matrix = Tensors.fromString("{{1, 0}, {2, 3}}");
    assertFalse(InfluenceMatrixQ.of(matrix));
    AssertFail.of(() -> InfluenceMatrixQ.require(matrix));
  }

  public void testChop() {
    Tensor matrix = Tensors.fromString("{{1, 0}, {2, 3}}");
    assertFalse(InfluenceMatrixQ.of(matrix, Tolerance.CHOP));
    AssertFail.of(() -> InfluenceMatrixQ.require(matrix, Tolerance.CHOP));
  }
}
