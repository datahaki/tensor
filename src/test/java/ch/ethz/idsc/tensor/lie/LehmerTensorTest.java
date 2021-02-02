// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.Arrays;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.TensorRank;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LehmerTensorTest extends TestCase {
  public void testSimple() {
    Tensor lehmer = LehmerTensor.of(3);
    ExactTensorQ.require(lehmer);
    assertEquals(TensorRank.of(lehmer), 3);
    assertEquals(Dimensions.of(LehmerTensor.of(4)), Arrays.asList(4, 4, 4, 4));
  }

  public void testEquivalent() {
    Tensor lehmer1 = LehmerTensor.of(3);
    Tensor lehmer2 = LehmerTensor.of(Range.of(1, 4));
    assertEquals(lehmer1, lehmer2);
  }

  public void testNegativeFail() {
    AssertFail.of(() -> LehmerTensor.of(0));
    AssertFail.of(() -> LehmerTensor.of(-1));
  }
}
