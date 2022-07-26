// code by jph
package ch.alpine.tensor.lie.r2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

/* package */ enum CirclePoint {
  INSTANCE;

  private final Map<Scalar, Tensor> map = new HashMap<>();

  CirclePoint() {
    map.put(RationalScalar.of(0, 12), Tensors.vector(+1, +0));
    map.put(RationalScalar.of(1, 12), Tensors.of(Cos.FUNCTION.apply(Pi.VALUE.multiply(RationalScalar.of(1, 6))), RationalScalar.HALF));
    map.put(RationalScalar.of(2, 12), Tensors.of(RationalScalar.HALF, Sin.FUNCTION.apply(Pi.VALUE.multiply(RationalScalar.of(2, 6)))));
    map.put(RationalScalar.of(3, 12), Tensors.vector(+0, +1));
    map.put(RationalScalar.of(4, 12), Tensors.of(RationalScalar.HALF.negate(), Sin.FUNCTION.apply(Pi.VALUE.multiply(RationalScalar.of(4, 6)))));
    map.put(RationalScalar.of(5, 12), Tensors.of(Cos.FUNCTION.apply(Pi.VALUE.multiply(RationalScalar.of(5, 6))), RationalScalar.HALF));
    map.put(RationalScalar.of(6, 12), Tensors.vector(-1, +0));
    map.put(RationalScalar.of(7, 12), Tensors.of(Cos.FUNCTION.apply(Pi.VALUE.multiply(RationalScalar.of(7, 6))), RationalScalar.HALF.negate()));
    map.put(RationalScalar.of(8, 12), Tensors.of(RationalScalar.HALF.negate(), Sin.FUNCTION.apply(Pi.VALUE.multiply(RationalScalar.of(8, 6)))));
    map.put(RationalScalar.of(9, 12), Tensors.vector(+0, -1));
    map.put(RationalScalar.of(10, 12), Tensors.of(RationalScalar.HALF, Sin.FUNCTION.apply(Pi.VALUE.multiply(RationalScalar.of(10, 6)))));
    map.put(RationalScalar.of(11, 12), Tensors.of(Cos.FUNCTION.apply(Pi.VALUE.multiply(RationalScalar.of(11, 6))), RationalScalar.HALF.negate()));
  }

  /** @param scalar
   * @return */
  public Optional<Tensor> turns(Scalar scalar) {
    return Optional.ofNullable(map.get(scalar)).map(Tensor::copy);
  }
}
