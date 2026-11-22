// code by jph
package ch.alpine.tensor.jet;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** maps a vector {a, b, c} to {a, b, c, 1}
 * 
 * throws exception for empty vector as input */
public enum AffineAppend implements TensorUnaryOperator {
  /** append multiplicative zero */
  M0 {
    @Override
    public Tensor apply(Tensor vector) {
      return Append.of(vector, vector.Get(0).one().zero());
    }
  },
  /** append multiplicative one */
  M1 {
    @Override
    public Tensor apply(Tensor vector) {
      return Append.of(vector, vector.Get(0).one());
    }
  },
  /** append additive zero */
  A0 {
    @Override
    public Tensor apply(Tensor vector) {
      return Append.of(vector, vector.Get(0).zero());
    }
  },
}
