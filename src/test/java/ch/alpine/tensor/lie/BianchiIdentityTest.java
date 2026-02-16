// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.pi.LinearSubspace;

class BianchiIdentityTest {
  /** dimensionality of subspace defined by equation seems to match
   * https://oeis.org/A072819 */
  static final Tensor DIMENSIONALITY = Tensors.vector( //
      0, 0, 8, 48, 160, 400, 840, 1568, 2688, 4320, 6600, 9680, 13728, 18928, //
      25480, 33600, 43520, 55488, 69768, 86640, 106400, 129360, 155848, 186208, //
      220800, 260000, 304200, 353808, 409248, 470960, 539400, 615040, 698368, //
      789888, 890120, 999600).unmodifiable();

  @Test
  void testRequireTrivial() {
    BianchiIdentity.INSTANCE.require(Array.zeros(4, 4, 4, 4));
  }

  @ParameterizedTest
  @CsvSource({ "1,0", "2,8", "3,48" }) // , "4,160", "5,400"
  void testDimensions(int n, int expect) {
    List<Integer> list = Collections.nCopies(4, n);
    LinearSubspace linearSubspace = LinearSubspace.of(BianchiIdentity.INSTANCE::defect, list);
    assertEquals(linearSubspace.dimensions(), expect);
    ExactTensorQ.of(linearSubspace.basis());
  }

  @Test
  void testRank1Fail() {
    assertThrows(Exception.class, () -> BianchiIdentity.INSTANCE.require(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testRank2Fail() {
    assertThrows(Exception.class, () -> BianchiIdentity.INSTANCE.require(HilbertMatrix.of(3, 3)));
  }

  @Test
  void testRank3Fail() {
    assertThrows(Exception.class, () -> BianchiIdentity.INSTANCE.require(Array.zeros(3, 3, 3)));
  }
}
