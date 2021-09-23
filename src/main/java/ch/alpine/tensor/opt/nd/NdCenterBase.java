// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.nrm.VectorInfinityNorm;

public abstract class NdCenterBase implements NdCenterInterface, Serializable {
  /** @param center vector
   * @return */
  public static NdCenterInterface of1Norm(Tensor center) {
    return new NdCenterBase(center) {
      @Override
      public Scalar distance(Tensor vector) {
        return Vector1Norm.between(vector, center);
      }
    };
  }

  /** @param center vector
   * @return */
  public static NdCenterInterface of2Norm(Tensor center) {
    return new NdCenterBase(center) {
      @Override
      public Scalar distance(Tensor vector) {
        return Vector2Norm.between(vector, center);
      }
    };
  }

  /** @param center vector
   * @return */
  public static NdCenterInterface ofInfinityNorm(Tensor center) {
    return new NdCenterBase(center) {
      @Override
      public Scalar distance(Tensor vector) {
        return VectorInfinityNorm.between(vector, center);
      }
    };
  }

  // ---
  private final Tensor center;

  private NdCenterBase(Tensor center) {
    this.center = center;
  }

  @Override // from NdCenterInterface
  public Tensor center() {
    return center;
  }
}
