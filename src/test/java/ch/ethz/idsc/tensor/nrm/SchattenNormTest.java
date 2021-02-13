// code by jph
package ch.ethz.idsc.tensor.nrm;

import java.io.IOException;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SchattenNormTest extends TestCase {
  public void testFrobenius() throws ClassNotFoundException, IOException {
    TensorScalarFunction schattenNorm = Serialization.copy(SchattenNorm.of(2));
    Distribution distribution = UniformDistribution.unit();
    Tensor matrix = RandomVariate.of(distribution, 5, 10);
    Scalar norm1 = schattenNorm.apply(matrix);
    Scalar norm2 = FrobeniusNorm.of(matrix);
    Chop._13.requireClose(norm1, norm2);
    assertEquals(schattenNorm.toString(), "SchattenNorm[2]");
  }

  public void testPFail() {
    AssertFail.of(() -> SchattenNorm.of(0.999));
  }

  public void testFail() throws ClassNotFoundException, IOException {
    TensorScalarFunction schattenNorm = Serialization.copy(SchattenNorm.of(1.2));
    Distribution distribution = UniformDistribution.unit();
    Tensor matrix = RandomVariate.of(distribution, 10, 5);
    Scalar scalar = schattenNorm.apply(matrix);
    Sign.requirePositive(scalar);
    AssertFail.of(() -> schattenNorm.apply(LeviCivitaTensor.of(3)));
  }
}
