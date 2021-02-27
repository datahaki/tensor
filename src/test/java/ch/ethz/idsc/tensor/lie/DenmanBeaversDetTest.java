// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.HermitianMatrixQ;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DenmanBeaversDetTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    for (int n = 1; n < 10; ++n) {
      Tensor x = RandomVariate.of(NormalDistribution.standard(), n, n);
      Tensor x2 = MatrixPower.of(x, 2);
      DenmanBeaversDet denmanBeaversDet = Serialization.copy(new DenmanBeaversDet(x2, Tolerance.CHOP)); // <- should converge faster
      DenmanBeaversPfm denmanBeaversPfm = Serialization.copy(new DenmanBeaversPfm(x2, Tolerance.CHOP));
      Chop._06.requireClose(denmanBeaversPfm.sqrt(), denmanBeaversDet.sqrt());
      // System.out.println(denmanBeaversDet.count() + " <= " + denmanBeaversPfm.count());
      assertTrue(denmanBeaversDet.count() <= denmanBeaversPfm.count() + 2);
      Tensor pfm2 = MatrixPower.of(denmanBeaversPfm.sqrt(), 2);
      Tensor det2 = MatrixPower.of(denmanBeaversDet.sqrt(), 2);
      Chop._03.requireClose(pfm2, det2);
      Chop._03.requireClose(det2, x2);
    }
  }

  public void testComplex() {
    Tensor x2 = Tensors.fromString("{{6, 1+5*I}, {1-5*I, 11}}");
    HermitianMatrixQ.require(x2);
    DenmanBeaversDet denmanBeaversDet = new DenmanBeaversDet(x2, Tolerance.CHOP);
    Tolerance.CHOP.requireClose( //
        Dot.of(denmanBeaversDet.sqrt(), denmanBeaversDet.sqrt_inverse()), IdentityMatrix.of(2));
    Tolerance.CHOP.requireClose( //
        Dot.of(denmanBeaversDet.sqrt(), denmanBeaversDet.sqrt()), x2);
  }
}
