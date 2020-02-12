// code by jph
package ch.ethz.idsc.tensor.opt;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.SingularValueDecomposition;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;

/** function computes the best-fitting rigid transformation that aligns
 * two sets of corresponding n elements from d-dimensional vector space.
 * 
 * Reference:
 * "Least-Squares Rigid Motion Using SVD"
 * Olga Sorkine-Hornung and Michael Rabinovich, 2016 */
public class RigidMotionFit implements TensorUnaryOperator {
  /** @param points matrix of dimension n x d
   * @param target matrix of dimension n x d
   * @param weights vector of length n with entries that sum up to 1
   * @return
   * @throws Exception if total of weights does not equal 1 */
  public static RigidMotionFit of(Tensor points, Tensor target, Tensor weights) {
    Chop._12.requireClose(Total.of(weights), RealScalar.ONE);
    return _of(points, target, weights);
  }

  /** @param points matrix of dimension n x d
   * @param target matrix of dimension n x d
   * @return */
  public static RigidMotionFit of(Tensor points, Tensor target) {
    return _of(points, target, ConstantArray.of(RationalScalar.of(1, points.length()), points.length()));
  }

  /** @param points
   * @param target
   * @param weights normalized to sum up to 1
   * @return */
  private static RigidMotionFit _of(Tensor points, Tensor target, Tensor weights) {
    Tensor pm = weights.dot(points);
    Tensor qm = weights.dot(target);
    Tensor xt = Tensor.of(points.stream().map(pm::subtract));
    Tensor yt = Tensor.of(target.stream().map(qm::subtract));
    SingularValueDecomposition svd = //
        SingularValueDecomposition.of(Transpose.of(xt).dot(weights.pmul(yt)));
    Tensor ut = Transpose.of(svd.getU());
    Tensor rotation = svd.getV().dot(ut);
    if (Sign.isNegative(Det.of(rotation))) {
      Tensor ue = Last.of(ut).negate();
      int last = ut.length() - 1;
      Tensor delta = TensorProduct.of(svd.getV().get(Tensor.ALL, last), ue.add(ue));
      rotation = rotation.add(delta);
    }
    return new RigidMotionFit(rotation, qm.subtract(rotation.dot(pm)));
  }

  // ---
  private final Tensor rotation;
  private final Tensor translation;

  private RigidMotionFit(Tensor rotation, Tensor translation) {
    this.rotation = rotation;
    this.translation = translation;
  }

  /** rotation dot point plus translation == target
   * 
   * @return orthogonal matrix with dimension d x d and determinant +1 */
  public Tensor rotation() {
    return rotation;
  }

  /** rotation dot point plus translation == target
   * 
   * @return vector of length d */
  public Tensor translation() {
    return translation;
  }

  @Override
  public Tensor apply(Tensor point) {
    return rotation.dot(point).add(translation);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[rotation=%s, translation=%s]", getClass().getSimpleName(), rotation(), translation());
  }
}
