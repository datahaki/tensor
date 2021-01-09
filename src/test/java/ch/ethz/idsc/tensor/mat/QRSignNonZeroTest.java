// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QRSignNonZeroTest extends TestCase {
  public void testZeroFail() throws ClassNotFoundException, IOException {
    QRSignOperator qrSignOperator = Serialization.copy(QRSignNonZero.LEAST_SQUARES);
    qrSignOperator.sign(RealScalar.of(1));
    AssertFail.of(() -> qrSignOperator.sign(RealScalar.of(1e-20)));
  }

  public void testNullFail() {
    AssertFail.of(() -> new QRSignNonZero(null, Tolerance.CHOP));
    AssertFail.of(() -> new QRSignNonZero(QRSignOperators.STABILITY, null));
  }
}
