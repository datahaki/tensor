// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;

public class DeleteDuplicatesTest {
  @Test
  public void testVector() {
    Tensor unique = DeleteDuplicates.of(Tensors.vector(7, 3, 3, 7, 1, 2, 3, 2, 3, 1));
    assertEquals(unique, Tensors.vector(7, 3, 1, 2));
  }

  @Test
  public void testReferences() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {1, 2}}");
    Tensor unique = DeleteDuplicates.of(tensor);
    unique.set(RealScalar.ZERO, 0, 0);
    assertEquals(tensor, Tensors.fromString("{{1, 2}, {1, 2}}"));
  }

  @Test
  public void testScalar() {
    AssertFail.of(() -> DeleteDuplicates.of(RealScalar.ONE));
  }
}
