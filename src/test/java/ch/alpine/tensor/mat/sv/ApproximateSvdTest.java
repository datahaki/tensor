// code by jph
package ch.alpine.tensor.mat.sv;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.qr.GramSchmidt;
import ch.alpine.tensor.mat.qr.QRDecomposition;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import junit.framework.TestCase;

public class ApproximateSvdTest extends TestCase {
  public void testSquare() {
    Tensor matrix = HilbertMatrix.of(6);
    QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
    SingularValueDecomposition approximateSvd = ApproximateSvd.of(qrDecomposition);
    Tensor v1 = SingularValueList.of(matrix);
    Tensor v2 = SingularValueList.of(approximateSvd);
    Tolerance.CHOP.requireClose(v1, v2);
  }

  public void testRect5x2() throws ClassNotFoundException, IOException {
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(), 5, 2);
    QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
    List<Integer> list = Dimensions.of(qrDecomposition.getR());
    // TODO the zeros in r are to be avoided when computing SVD
    // System.out.println(list);
    list.size();
    SingularValueDecomposition approximateSvd = Serialization.copy(ApproximateSvd.of(qrDecomposition));
    Tensor v1 = SingularValueList.of(matrix);
    Tensor v2 = SingularValueList.of(approximateSvd);
    Tolerance.CHOP.requireClose(v1, v2);
  }

  public void testRect5x3() {
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(), 5, 3);
    QRDecomposition qrDecomposition = GramSchmidt.of(matrix);
    assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(3, 3));
    SingularValueDecomposition approximateSvd = ApproximateSvd.of(qrDecomposition);
    Tensor v1 = SingularValueList.of(matrix);
    Tensor v2 = SingularValueList.of(approximateSvd);
    Tolerance.CHOP.requireClose(v1, v2);
  }
}
