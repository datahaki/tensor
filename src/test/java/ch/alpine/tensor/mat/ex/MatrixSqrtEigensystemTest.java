// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Sign;

class MatrixSqrtEigensystemTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Random random = new Random(1);
    int n = 11;
    Tensor x = Symmetrize.of(RandomVariate.of(NormalDistribution.standard(), random, n, n));
    Tensor x2 = x.dot(x);
    DenmanBeaversDet denmanBeaversDet = //
        Serialization.copy(new DenmanBeaversDet(x2, Tolerance.CHOP)); // <- should converge faster
    Eigensystem eigensystem = Eigensystem.ofSymmetric(x2);
    eigensystem.values().stream().map(Scalar.class::cast).forEach(Sign::requirePositiveOrZero);
    MatrixSqrtEigensystem matrixSqrtSymmetric = Serialization.copy(new MatrixSqrtEigensystem(eigensystem));
    Chop._06.requireClose(matrixSqrtSymmetric.sqrt(), denmanBeaversDet.sqrt());
    Tensor pfm2 = MatrixPower.of(matrixSqrtSymmetric.sqrt(), 2);
    Tensor det2 = MatrixPower.of(denmanBeaversDet.sqrt(), 2);
    Chop._03.requireClose(pfm2, det2);
    Chop._03.requireClose(det2, x2);
    Eigensystem ofHermitian = Eigensystem.ofHermitian(x2);
    ofHermitian.values().stream().map(Scalar.class::cast).forEach(Sign::requirePositiveOrZero);
    MatrixSqrtEigensystem matrixSqrtHermitian = new MatrixSqrtEigensystem(ofHermitian);
    Tolerance.CHOP.requireClose(matrixSqrtSymmetric.sqrt(), matrixSqrtHermitian.sqrt());
    Tolerance.CHOP.requireClose(matrixSqrtSymmetric.sqrt_inverse(), matrixSqrtHermitian.sqrt_inverse());
  }

  @Test
  void testNegative() {
    Tensor matrix = Tensors.fromString("{{-10[m^2], -2[m^2]}, {-2[m^2], 4[m^2]}}");
    MatrixSqrt matrixSqrt = new MatrixSqrtEigensystem(Eigensystem.ofSymmetric(matrix));
    assertTrue(Scalars.lessThan(Quantity.of(2, "m"), Matrix2Norm.bound(matrixSqrt.sqrt().map(Imag.FUNCTION))));
    Tensor eye = Dot.of(matrixSqrt.sqrt(), matrixSqrt.sqrt_inverse());
    Tolerance.CHOP.requireClose(eye, IdentityMatrix.of(2));
  }

  @Test
  void testQuantity() {
    Tensor matrix = Tensors.fromString("{{10[m^2], -2[m^2]}, {-2[m^2], 4[m^2]}}");
    MatrixSqrtEigensystem matrixSqrtSymmetric = new MatrixSqrtEigensystem(Eigensystem.ofSymmetric(matrix));
    Tensor eye = Dot.of(matrixSqrtSymmetric.sqrt(), matrixSqrtSymmetric.sqrt_inverse());
    Tolerance.CHOP.requireClose(eye, IdentityMatrix.of(2));
  }
}
