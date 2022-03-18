// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;

public class TensorExtractTest {
  @Test
  public void testEmpty() {
    assertEquals(TensorExtract.of(Tensors.empty(), 0, t -> t), Tensors.empty());
  }

  @Test
  public void testRadiusFail() {
    AssertFail.of(() -> TensorExtract.of(Tensors.empty(), -1, t -> t));
  }

  @Test
  public void testFunctionNullFail() {
    AssertFail.of(() -> TensorExtract.of(Tensors.empty(), 2, null));
  }
}
