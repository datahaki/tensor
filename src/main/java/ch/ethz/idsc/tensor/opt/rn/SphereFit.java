// code by jph
package ch.ethz.idsc.tensor.opt.rn;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.mat.LeastSquares;
import ch.ethz.idsc.tensor.mat.MatrixRank;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** reference: "Circle fitting by linear and non-linear least squares", by J. D. Coope */
public class SphereFit implements Serializable {
  private static final long serialVersionUID = -8481182039157613940L;

  /** @param points encoded as matrix
   * @return optional with instance of SphereFit containing the center and radius
   * of the fitted sphere, or empty if points are numerically co-linear
   * @throws Exception if points is empty, or not a matrix */
  public static Optional<SphereFit> of(Tensor points) {
    Tensor A = Tensor.of(points.stream() //
        .map(point -> point.add(point)) //
        .map(point -> point.append(RealScalar.ONE)));
    Tensor b = Tensor.of(points.stream().map(Norm2Squared::ofVector));
    int cols = Unprotect.dimension1(A);
    if (A.length() < cols || //
        MatrixRank.of(A) < cols)
      return Optional.empty();
    Tensor x = LeastSquares.of(A, b);
    Tensor center = x.extract(0, cols - 1);
    return Optional.of(new SphereFit( //
        center, //
        Sqrt.FUNCTION.apply(x.Get(cols - 1).add(Norm2Squared.ofVector(center)))));
  }

  /***************************************************/
  private final Tensor center;
  private final Scalar radius;

  private SphereFit(Tensor center, Scalar radius) {
    this.center = center;
    this.radius = radius;
  }

  /** @return center of fitted sphere */
  public Tensor center() {
    return center;
  }

  /** @return radius of fitted sphere */
  public Scalar radius() {
    return radius;
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[center=%s, radius=%s]", getClass().getSimpleName(), center(), radius());
  }
}
