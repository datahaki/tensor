// code by jph
package ch.ethz.idsc.tensor.nrm;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class Norm1Test extends TestCase {
  public void testQuantity1() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(-4, "m");
    Scalar qs3 = Quantity.of(7, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    assertEquals(Norm._1.ofVector(vec), qs3);
  }

  public void testQuantity2() {
    Tensor vec = Tensors.of( //
        Quantity.of(-3, "m"), //
        Quantity.of(0, "s*rad"), //
        RealScalar.ZERO, //
        Quantity.of(-4, "m") //
    );
    assertEquals(Norm._1.ofVector(vec), Quantity.of(7, "m"));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Norm._1.ofVector(RealScalar.ONE));
  }

  public void testEmptyStreamFail() {
    AssertFail.of(() -> Norm1.ofVector(Stream.of()));
  }

  public void testVisibility() {
    assertTrue(Modifier.isPublic(Norm1.class.getModifiers()));
  }
}
