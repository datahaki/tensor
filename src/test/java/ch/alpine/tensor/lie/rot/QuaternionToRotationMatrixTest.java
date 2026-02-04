// code by jph
package ch.alpine.tensor.lie.rot;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;

class QuaternionToRotationMatrixTest {
  private static final Tensor ID3 = IdentityMatrix.of(3);

  @Test
  void testSimple() {
    Quaternion quaternion = Quaternion.of(0.240810, -0.761102, -0.355923, -0.485854);
    quaternion = quaternion.divide(quaternion.abs());
    Tensor matrix = QuaternionToRotationMatrix.of(quaternion);
    assertTrue(OrthogonalMatrixQ.INSTANCE.isMember(matrix));
    Chop._12.requireClose(Det.of(matrix), RealScalar.ONE);
    Tensor xyza = Append.of(quaternion.xyz(), quaternion.w());
    xyza = Vector2Norm.NORMALIZE.apply(xyza);
    Tensor rot = QuaternionToRotationMatrix.of(xyza);
    Chop._12.requireClose(matrix, rot);
    assertTrue(OrthogonalMatrixQ.INSTANCE.isMember(rot));
  }

  @ParameterizedTest
  @MethodSource(value = "test.bulk.TestDistributions#distributions")
  void testRandom(Distribution distribution) {
    Tensor wxyz = RandomVariate.of(distribution, 4);
    Quaternion q = Quaternion.of(wxyz.Get(0), wxyz.extract(1, 4));
    q = q.divide(q.abs());
    Tensor matrix = QuaternionToRotationMatrix.of(q);
    assertTrue(OrthogonalMatrixQ.INSTANCE.isMember(matrix));
    Chop._12.requireClose(Det.of(matrix), RealScalar.ONE);
    Quaternion reciprocal = q.reciprocal();
    Tensor invmat = QuaternionToRotationMatrix.of(reciprocal);
    Chop._12.requireClose(matrix.dot(invmat), ID3);
  }

  @ParameterizedTest
  @MethodSource(value = "test.bulk.TestDistributions#distributions")
  void testQuaternionVector(Distribution distribution) {
    RandomGenerator randomGenerator = ThreadLocalRandom.current();
    Quaternion quaternion = Quaternion.of(randomGenerator.nextGaussian(), randomGenerator.nextGaussian(), randomGenerator.nextGaussian(),
        randomGenerator.nextGaussian());
    quaternion = quaternion.divide(quaternion.abs()); // normalize
    Tensor vector = RandomVariate.of(distribution, 3);
    Scalar v = Quaternion.of(RealScalar.ZERO, vector);
    Quaternion qvqi = quaternion.multiply(v).multiply(Conjugate.FUNCTION.apply(quaternion));
    Quaternion qq = quaternion;
    Tensor matrix = QuaternionToRotationMatrix.of(qq);
    assertTrue(OrthogonalMatrixQ.INSTANCE.isMember(matrix));
    Chop._12.requireClose(Det.of(matrix), RealScalar.ONE);
    Tensor result = matrix.dot(vector);
    Chop._12.requireClose(result, qvqi.xyz());
    Chop._12.requireZero(qvqi.w());
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7 })
  void testThrows(int length) {
    assertThrows(Exception.class, () -> QuaternionToRotationMatrix.of(UnitVector.of(length, 0)));
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> QuaternionToRotationMatrix.of(Quaternion.of(0, 0, 0, 0)));
    assertThrows(Throw.class, () -> QuaternionToRotationMatrix.of(Quaternion.of(0.0, 0.0, 0.0, 0.0)));
  }
}
