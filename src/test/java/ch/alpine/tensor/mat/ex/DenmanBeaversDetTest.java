// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class DenmanBeaversDetTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
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

  @Test
  void testComplex() {
    Tensor x2 = Tensors.fromString("{{6, 1+5*I}, {1-5*I, 11}}");
    HermitianMatrixQ.require(x2);
    DenmanBeaversDet denmanBeaversDet = new DenmanBeaversDet(x2, Tolerance.CHOP);
    Tolerance.CHOP.requireClose( //
        Dot.of(denmanBeaversDet.sqrt(), denmanBeaversDet.sqrt_inverse()), IdentityMatrix.of(2));
    Tolerance.CHOP.requireClose( //
        Dot.of(denmanBeaversDet.sqrt(), denmanBeaversDet.sqrt()), x2);
  }

  @SuppressWarnings("unused")
  @Test
  void testFail() {
    Tensor matrix = Import.of("/ch/alpine/tensor/mat/ex/dbd_fail.csv");
    try {
      new DenmanBeaversDet(matrix, Tolerance.CHOP);
    } catch (Exception exception) {
      // ---
    }
  }
}
