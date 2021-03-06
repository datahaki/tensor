// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class AppendTest extends TestCase {
  public void testSimple() {
    Tensor x = Tensors.vector(1, 2, 3);
    Tensor y = Tensors.vector(4, 5, 6);
    Tensor tensor = Append.of(x, y);
    tensor.set(RealScalar.ZERO, 0);
    tensor.set(Array.zeros(3), 3);
    assertEquals(x, Tensors.vector(1, 2, 3));
    assertEquals(y, Tensors.vector(4, 5, 6));
  }

  public void testLast() {
    Tensor x = Tensors.vector(1, 2, 3);
    Tensor y = Tensors.vector(4, 5, 6);
    Tensor tensor = Append.of(x, y);
    y.set(RealScalar.ZERO, 1);
    assertEquals(tensor, Tensors.fromString("{1, 2, 3, {4, 5, 6}}"));
  }

  public void testEmpty() {
    Tensor tensor = Append.of(Tensors.empty().unmodifiable(), RealScalar.ONE);
    assertEquals(tensor, Tensors.vector(1));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Append.of(RealScalar.ONE, Pi.TWO));
  }

  public void testNullFail() {
    AssertFail.of(() -> Append.of(null, Tensors.vector(1)));
    AssertFail.of(() -> Append.of(Tensors.vector(1), null));
  }
}
