// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.usr.AssertFail;
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
