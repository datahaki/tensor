// code by jph
package ch.alpine.tensor.nrm;

import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.NegativeBinomialDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class Vector1NormTest extends TestCase {
  public void testQuantity1() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(-4, "m");
    Scalar qs3 = Quantity.of(7, "m");
    Tensor vec = Tensors.of(qs1, qs2);
    assertEquals(Vector1Norm.of(vec), qs3);
  }

  public void testQuantity2() {
    Tensor vec = Tensors.of( //
        Quantity.of(-3, "m"), //
        Quantity.of(-4, "m") //
    );
    assertEquals(Vector1Norm.of(vec), Quantity.of(7, "m"));
  }

  public void testQuantityFail() {
    Tensor vec = Tensors.of( //
        Quantity.of(-3, "m"), //
        RealScalar.ZERO, //
        Quantity.of(-4, "m") //
    );
    AssertFail.of(() -> Vector1Norm.of(vec));
  }

  public void testBetween() {
    Distribution distribution = NegativeBinomialDistribution.of(3, 0.8);
    Tensor a = RandomVariate.of(distribution, 7);
    Tensor b = RandomVariate.of(distribution, 7);
    Scalar vab = Vector1Norm.of(a.subtract(b));
    assertEquals(vab, Vector1Norm.of(b.subtract(a)));
    assertEquals(vab, Vector1Norm.between(a, b));
  }

  public void testEmptyStreamFail() {
    AssertFail.of(() -> Vector1Norm.of(Stream.of()));
  }
}
