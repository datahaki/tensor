// code by jph
package ch.alpine.tensor;

import java.util.Arrays;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ViewTensorTest extends TestCase {
  public void testBlock() {
    Tensor array = Array.zeros(5, 5);
    Tensor refs = Unprotect.references(array);
    refs.block(Arrays.asList(1, 2), Arrays.asList(2, 3)).set(RealScalar.ONE::add, Tensor.ALL, Tensor.ALL);
    assertEquals(array, //
        Tensors.fromString("{{0, 0, 0, 0, 0}, {0, 0, 1, 1, 1}, {0, 0, 1, 1, 1}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}}"));
  }

  public void testUnmodifiableView() {
    Tensor tensor = Tensors.vector(1, 2, 3, 4).unmodifiable();
    AssertFail.of(() -> ViewTensor.wrap(tensor));
  }

  public void testExtract() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor access = Unprotect.references(vector);
    Tensor slevel = access.extract(1, 3);
    slevel.set(RealScalar.ONE::add, 0);
    assertEquals(vector, Tensors.vector(1, 3, 3));
  }

  public void testUnmodifiableFail() {
    AssertFail.of(() -> Unprotect.references(Tensors.vector(1, 2, 3).unmodifiable()));
  }

  public void testUnmodifiableIterateFail() {
    AssertFail.of(() -> Unprotect.references(Tensors.matrixInt(new int[][] { { 1, 2, 3 } }).unmodifiable().iterator().next()));
  }

  public void testUnmodifiableLoopFail() {
    for (Tensor tensor : Tensors.matrixInt(new int[][] { { 1, 2 }, { 3, 4, 5 } }).unmodifiable())
      AssertFail.of(() -> Unprotect.references(tensor));
  }

  public void testNonPublic() {
    assertEquals(ViewTensor.class.getModifiers(), 0);
  }
}
