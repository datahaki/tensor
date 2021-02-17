// code by jph
package ch.ethz.idsc.tensor.nrm;

import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NegativeBinomialDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
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
        Quantity.of(0, "s*rad"), //
        RealScalar.ZERO, //
        Quantity.of(-4, "m") //
    );
    assertEquals(Vector1Norm.of(vec), Quantity.of(7, "m"));
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
