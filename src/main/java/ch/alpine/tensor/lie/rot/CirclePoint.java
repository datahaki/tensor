// code by jph
package ch.alpine.tensor.lie.rot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ch.alpine.tensor.Rational;
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
    map.put(Rational.of(0, 12), Tensors.vector(+1, +0));
    map.put(Rational.of(1, 12), Tensors.of(Cos.FUNCTION.apply(Pi.VALUE.multiply(Rational.of(1, 6))), Rational.HALF));
    map.put(Rational.of(2, 12), Tensors.of(Rational.HALF, Sin.FUNCTION.apply(Pi.VALUE.multiply(Rational.of(2, 6)))));
    map.put(Rational.of(3, 12), Tensors.vector(+0, +1));
    map.put(Rational.of(4, 12), Tensors.of(Rational.HALF.negate(), Sin.FUNCTION.apply(Pi.VALUE.multiply(Rational.of(4, 6)))));
    map.put(Rational.of(5, 12), Tensors.of(Cos.FUNCTION.apply(Pi.VALUE.multiply(Rational.of(5, 6))), Rational.HALF));
    map.put(Rational.of(6, 12), Tensors.vector(-1, +0));
    map.put(Rational.of(7, 12), Tensors.of(Cos.FUNCTION.apply(Pi.VALUE.multiply(Rational.of(7, 6))), Rational.HALF.negate()));
    map.put(Rational.of(8, 12), Tensors.of(Rational.HALF.negate(), Sin.FUNCTION.apply(Pi.VALUE.multiply(Rational.of(8, 6)))));
    map.put(Rational.of(9, 12), Tensors.vector(+0, -1));
    map.put(Rational.of(10, 12), Tensors.of(Rational.HALF, Sin.FUNCTION.apply(Pi.VALUE.multiply(Rational.of(10, 6)))));
    map.put(Rational.of(11, 12), Tensors.of(Cos.FUNCTION.apply(Pi.VALUE.multiply(Rational.of(11, 6))), Rational.HALF.negate()));
  }

  /** @param scalar
   * @return */
  public Optional<Tensor> turns(Scalar scalar) {
    return Optional.ofNullable(map.get(scalar)).map(Tensor::copy);
  }
}
