// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class QRMathematicaTest extends TestCase {
  public void testSimple() {
    Tensor a = Tensors.fromString("{{1, 2, 3}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
    assertEquals(Dimensions.of(a), Arrays.asList(4, 3));
    QRDecomposition qrDecomposition = QRMathematica.wrap(QRDecomposition.of(a));
    assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(3, 3));
    assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(4, 3));
    assertEquals(Dimensions.of(qrDecomposition.getInverseQ()), Arrays.asList(3, 4));
    Tensor r = qrDecomposition.getR();
    Tensor q = qrDecomposition.getQ();
    Chop._10.requireClose(q.dot(r), a);
    Chop._10.requireClose(ConjugateTranspose.of(q), qrDecomposition.getInverseQ());
  }
}
