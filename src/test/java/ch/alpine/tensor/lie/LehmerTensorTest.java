// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.TensorRank;
import ch.alpine.tensor.chq.ExactTensorQ;

public class LehmerTensorTest {
  @Test
  public void testSimple() {
    Tensor lehmer = LehmerTensor.of(3);
    ExactTensorQ.require(lehmer);
    assertEquals(TensorRank.of(lehmer), 3);
    assertEquals(Dimensions.of(LehmerTensor.of(4)), Arrays.asList(4, 4, 4, 4));
  }

  @Test
  public void testEquivalent() {
    Tensor lehmer1 = LehmerTensor.of(3);
    Tensor lehmer2 = LehmerTensor.of(Range.of(1, 4));
    assertEquals(lehmer1, lehmer2);
  }

  @Test
  public void testNegativeFail() {
    assertThrows(NoSuchElementException.class, () -> LehmerTensor.of(0));
    assertThrows(IllegalArgumentException.class, () -> LehmerTensor.of(-1));
  }
}
