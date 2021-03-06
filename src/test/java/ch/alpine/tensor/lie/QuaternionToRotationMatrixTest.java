// code by jph
package ch.alpine.tensor.lie;

import java.security.SecureRandom;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QuaternionToRotationMatrixTest extends TestCase {
  private static final Tensor ID3 = IdentityMatrix.of(3);

  public void testSimple() {
    Quaternion quaternion = Quaternion.of(0.240810, -0.761102, -0.355923, -0.485854);
    Tensor matrix = QuaternionToRotationMatrix.of(quaternion);
    assertTrue(OrthogonalMatrixQ.of(matrix));
    Chop._12.requireClose(Det.of(matrix), RealScalar.ONE);
    Tensor altern = QuaternionToRotationMatrix.of(quaternion.multiply(RealScalar.of(3)));
    Chop._12.requireClose(matrix, altern);
  }

  public void testRandom() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor wxyz = RandomVariate.of(distribution, 4);
      Tensor matrix = QuaternionToRotationMatrix.of(Quaternion.of(wxyz.Get(0), wxyz.extract(1, 4)));
      assertTrue(OrthogonalMatrixQ.of(matrix, Chop._12));
      Chop._12.requireClose(Det.of(matrix), RealScalar.ONE);
      Quaternion quaternion = Quaternion.of(wxyz.Get(0), wxyz.Get(1), wxyz.Get(2), wxyz.Get(3));
      Quaternion reciprocal = quaternion.reciprocal();
      Tensor invmat = QuaternionToRotationMatrix.of(reciprocal);
      Chop._12.requireClose(matrix.dot(invmat), ID3);
    }
  }

  public void testQuaternionVector() {
    Random random = new SecureRandom();
    for (int index = 0; index < 10; ++index) {
      Quaternion quaternion = Quaternion.of(random.nextGaussian(), random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
      quaternion = quaternion.divide(quaternion.abs()); // normalize
      Tensor vector = RandomVariate.of(NormalDistribution.standard(), 3);
      Scalar v = Quaternion.of(RealScalar.ZERO, vector);
      Quaternion qvq = quaternion.multiply(v).multiply(Conjugate.FUNCTION.apply(quaternion));
      Quaternion qq = quaternion;
      Tensor matrix = QuaternionToRotationMatrix.of(qq);
      assertTrue(OrthogonalMatrixQ.of(matrix, Chop._12));
      Chop._12.requireClose(Det.of(matrix), RealScalar.ONE);
      Tensor result = matrix.dot(vector);
      Chop._12.requireClose(result, qvq.xyz());
    }
  }

  public void testFail() {
    AssertFail.of(() -> QuaternionToRotationMatrix.of(Quaternion.of(0, 0, 0, 0)));
    AssertFail.of(() -> QuaternionToRotationMatrix.of(Quaternion.of(0.0, 0.0, 0.0, 0.0)));
  }
}
