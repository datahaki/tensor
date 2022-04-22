// code by jph
package ch.alpine.tensor;

import java.util.Objects;

import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ArgInterface;
import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.api.ConjugateInterface;
import ch.alpine.tensor.api.MultiplexScalar;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.ExpInterface;
import ch.alpine.tensor.sca.exp.LogInterface;
import ch.alpine.tensor.sca.pow.PowerInterface;
import ch.alpine.tensor.sca.pow.SqrtInterface;
import ch.alpine.tensor.sca.tri.ArcTanInterface;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.TrigonometryInterface;

/** complex number
 * 
 * <p>function {@link #number()} is not supported
 * 
 * <p>interface {@link Comparable} is not implemented */
public interface ComplexScalar extends Scalar, //
    AbsInterface, ArcTanInterface, ArgInterface, ComplexEmbedding, ConjugateInterface, //
    ExpInterface, LogInterface, PowerInterface, SignInterface, //
    SqrtInterface, TrigonometryInterface {
  /** complex number I == 0+1*I */
  Scalar I = of(0, 1);

  /** Hint: the function {@link #of(Scalar, Scalar)} can be used to combine two
   * tensors, one as real and the other as imaginary part into a single tensor
   * with complex scalars as entries.
   * <pre>
   * Entrywise.with(ComplexScalar::of).apply(r, i);
   * </pre>
   * 
   * @param re
   * @param im
   * @return scalar with re as real part and im as imaginary part
   * @throws Exception if re or im are {@link ComplexScalar} */
  static Scalar of(Scalar re, Scalar im) {
    if (re instanceof MultiplexScalar || im instanceof MultiplexScalar)
      throw TensorRuntimeException.of(re, im);
    return ComplexScalarImpl.of(Objects.requireNonNull(re), im);
  }

  /** @param re
   * @param im
   * @return scalar with re as real part and im as imaginary part */
  static Scalar of(Number re, Number im) {
    return ComplexScalarImpl.of(RealScalar.of(re), RealScalar.of(im));
  }

  /** @param abs radius, may be instance of {@link Quantity}
   * @param arg angle
   * @return complex scalar with polar coordinates abs and arg */
  static Scalar fromPolar(Scalar abs, Scalar arg) {
    return Sign.requirePositiveOrZero(abs).multiply(unit(arg));
  }

  /** @param abs radius
   * @param arg angle
   * @return complex scalar with polar coordinates abs and arg */
  static Scalar fromPolar(Number abs, Number arg) {
    return fromPolar(RealScalar.of(abs), RealScalar.of(arg));
  }

  /** @param arg angle
   * @return Exp[I * angle], i.e. complex number on unit circle with given argument */
  static Scalar unit(Scalar arg) {
    if (arg instanceof ComplexScalar)
      throw TensorRuntimeException.of(arg);
    return ComplexScalarImpl.of(Cos.FUNCTION.apply(arg), Sin.FUNCTION.apply(arg));
  }
}
