// adapted by jph
package ch.alpine.tensor.lie.rot;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.N;

public enum QuaternionToRotationMatrix {
  ;
  private static final Tensor ID3 = IdentityMatrix.of(3).maps(N.DOUBLE);

  /** @param quaternion with unit length
   * @return orthogonal 3x3 matrix
   * @throws Exception unless quaternion.abs == 1 */
  public static Tensor of(Quaternion quaternion) {
    Tolerance.CHOP.requireClose(quaternion.abs(), RealScalar.ONE);
    return of(quaternion.xyz(), quaternion.w());
  }

  /** @param xyzw vector of length 4 with 2-norm equals 1
   * @return
   * @throws Exception if given vector does not have 2-norm 1 */
  public static Tensor of(Tensor xyzw) {
    Integers.requireEquals(xyzw.length(), 4);
    Tolerance.CHOP.requireClose(Vector2Norm.of(xyzw), RealScalar.ONE);
    return of(xyzw.extract(0, 3), xyzw.Get(3));
  }

  private static Tensor of(Tensor xyz, Scalar w) {
    Tensor X1 = Cross.skew3(xyz.multiply(w));
    Tensor X2 = Cross.skew3(xyz);
    Tensor X3 = X2.dot(X2).add(X1);
    return ID3.add(X3).add(X3);
  }
}
