// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.IOException;
import java.util.Random;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class MatrixSqrtSymmetricTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Random random = new Random();
    int n = 1 + random.nextInt(15);
    Tensor x = Symmetrize.of(RandomVariate.of(NormalDistribution.standard(), random, n, n));
    Tensor x2 = x.dot(x);
    DenmanBeaversDet denmanBeaversDet = //
        Serialization.copy(new DenmanBeaversDet(x2, Tolerance.CHOP)); // <- should converge faster
    MatrixSqrtSymmetric matrixSqrtSymmetric = Serialization.copy(new MatrixSqrtSymmetric(x2));
    Chop._06.requireClose(matrixSqrtSymmetric.sqrt(), denmanBeaversDet.sqrt());
    Tensor pfm2 = MatrixPower.of(matrixSqrtSymmetric.sqrt(), 2);
    Tensor det2 = MatrixPower.of(denmanBeaversDet.sqrt(), 2);
    Chop._03.requireClose(pfm2, det2);
    Chop._03.requireClose(det2, x2);
  }
}
