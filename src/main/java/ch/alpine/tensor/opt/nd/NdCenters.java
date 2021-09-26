// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.nrm.VectorInfinityNorm;

/** default implementation of {@link NdCenterInterface} for standard vector norms
 * in the space R^d.
 * 
 * The input parameter is the center of the region.
 * 
 * {@link #VECTOR_2_NORM} is invariant under rotation of the basis and is used
 * most commonly. */
public enum NdCenters implements Function<Tensor, NdCenterInterface> {
  /** based on {@link Vector1Norm} */
  VECTOR_1_NORM {
    @Override
    public NdCenterInterface apply(Tensor center) {
      return new NdCenterBase(center) {
        @Override
        public Scalar distance(Tensor point) {
          return Vector1Norm.between(point, center);
        }
      };
    }
  },
  /** Euclidean distance function
   * 
   * based on {@link Vector2Norm} */
  VECTOR_2_NORM {
    @Override
    public NdCenterInterface apply(Tensor center) {
      return new NdCenterBase(center) {
        @Override
        public Scalar distance(Tensor point) {
          return Vector2Norm.between(point, center);
        }
      };
    }
  },
  /** based on {@link VectorInfinityNorm} */
  VECTOR_INFINITY_NORM {
    @Override
    public NdCenterInterface apply(Tensor center) {
      return new NdCenterBase(center) {
        @Override
        public Scalar distance(Tensor point) {
          return VectorInfinityNorm.between(point, center);
        }
      };
    }
  };
}
