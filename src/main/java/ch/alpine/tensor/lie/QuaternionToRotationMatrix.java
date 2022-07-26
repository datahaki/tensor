// adapted by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;

/** Reference:
 * http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm */
public enum QuaternionToRotationMatrix {
  ;
  /** @param quaternion non-zero
   * @return orthogonal 3x3 matrix
   * @throws Exception if given quaternion cannot be normalized */
  public static Tensor of(Quaternion quaternion) {
    Scalar abs = quaternion.abs();
    if (Scalars.isZero(abs))
      throw new Throw(quaternion);
    Quaternion unit = quaternion.divide(abs); // normalize
    Scalar q_w = unit.w();
    Tensor xyz = unit.xyz();
    Scalar q_x = xyz.Get(0);
    Scalar q_y = xyz.Get(1);
    Scalar q_z = xyz.Get(2);
    Scalar qxx = q_x.multiply(q_x);
    Scalar qxy = q_x.multiply(q_y);
    Scalar qyy = q_y.multiply(q_y);
    Scalar qxz = q_x.multiply(q_z);
    Scalar qyz = q_y.multiply(q_z);
    Scalar qzz = q_z.multiply(q_z);
    Scalar qxw = q_x.multiply(q_w);
    Scalar qyw = q_y.multiply(q_w);
    Scalar qzw = q_z.multiply(q_w);
    Scalar qww = q_w.multiply(q_w);
    Scalar m00 = qxx.subtract(qyy).subtract(qzz);
    Scalar m11 = qyy.subtract(qzz).subtract(qxx);
    Scalar m22 = qzz.subtract(qxx).subtract(qyy);
    Scalar m10 = qxy.add(qzw);
    Scalar m01 = qxy.subtract(qzw);
    Scalar m20 = qxz.subtract(qyw);
    Scalar m02 = qxz.add(qyw);
    Scalar m21 = qyz.add(qxw);
    Scalar m12 = qyz.subtract(qxw);
    return Tensors.matrix(new Scalar[][] { //
        { m00.add(qww), m01.add(m01), m02.add(m02) }, //
        { m10.add(m10), m11.add(qww), m12.add(m12) }, //
        { m20.add(m20), m21.add(m21), m22.add(qww) } });
  }
}
