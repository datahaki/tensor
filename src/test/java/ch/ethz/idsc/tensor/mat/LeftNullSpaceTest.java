// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LeftNullSpaceTest extends TestCase {
  public void testRankDeficient() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {0, 1}, {0, 1}, {0, 1}}");
    Tensor nullsp = LeftNullSpace.of(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(3, 4));
    Chop._10.requireAllZero(nullsp.dot(matrix));
  }

  public void testMaxRank() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {2, 1}, {0, 1}, {0, 1}}");
    Tensor nullsp = LeftNullSpace.of(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(2, 4));
    Chop._10.requireAllZero(nullsp.dot(matrix));
  }

  public void testRankDeficientTranspose() {
    Tensor matrix = Transpose.of(Tensors.fromString("{{0, 1}, {0, 1}, {0, 1}, {0, 1}}"));
    Tensor nullsp = LeftNullSpace.of(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(1, 2));
    Chop._10.requireAllZero(nullsp.dot(matrix));
  }

  public void testMaxRankTranspose() {
    Tensor matrix = Transpose.of(Tensors.fromString("{{0, 1}, {2, 1}, {0, 1}, {0, 1}}"));
    Tensor nullsp = LeftNullSpace.of(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(0));
  }
}
