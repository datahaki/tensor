// code by jph
package ch.alpine.tensor.lie;

import java.util.Objects;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ConjugateInterface;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.sca.exp.ExpInterface;
import ch.alpine.tensor.sca.exp.LogInterface;
import ch.alpine.tensor.sca.pow.PowerInterface;
import ch.alpine.tensor.sca.pow.SqrtInterface;
import ch.alpine.tensor.sca.tri.TrigonometryInterface;

/** Quote from Mathematica:
 * Quaternion objects will not automatically simplify to Complex or Real numbers.
 * 
 * <p>Important in the tensor library:
 * not all algorithms are tested for {@link Quaternion} input.
 * The consistent handling of the non-commutativity of the multiplication
 * may require significant modifications of the existing algorithms.
 * 
 * <p>The tensor library does not provide parsing a string to a quaternion.
 * 
 * <p>Mathematica does not serve as a role model for quaternions. Their
 * corresponding functionality appears cumbersome and limited.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Quaternion.html">Quaternion</a> */
// TODO Mathematica::Quaternion supports Round
public interface Quaternion extends Scalar, //
    AbsInterface, ConjugateInterface, ExpInterface, LogInterface, PowerInterface, //
    SignInterface, SqrtInterface, TrigonometryInterface {
  Quaternion ZERO = of(0, 0, 0, 0);
  Quaternion ONE = of(1, 0, 0, 0);

  /** Hint: As in Mathematica, "Quaternion objects will not automatically
   * simplify to Complex or Real numbers."
   * 
   * @param w real part
   * @param xyz vector of length 3
   * @return instance of Quaternion with components (w, x, y, z)
   * @throws Exception if given xyz is not a vector of length 3 */
  static Quaternion of(Scalar w, Tensor xyz) {
    return new QuaternionImpl(Objects.requireNonNull(w), VectorQ.requireLength(xyz, 3).copy());
  }

  /** Hint: As in Mathematica, "Quaternion objects will not automatically
   * simplify to Complex or Real numbers."
   * 
   * @param w real part
   * @param x
   * @param y
   * @param z
   * @return instance of Quaternion with components (w, x, y, z) */
  static Quaternion of(Scalar w, Scalar x, Scalar y, Scalar z) {
    return new QuaternionImpl( //
        Objects.requireNonNull(w), //
        Tensors.of(Objects.requireNonNull(x), Objects.requireNonNull(y), Objects.requireNonNull(z)));
  }

  /** Hint: As in Mathematica, "Quaternion objects will not automatically
   * simplify to Complex or Real numbers."
   * 
   * @param w real part
   * @param x
   * @param y
   * @param z
   * @return instance of Quaternion with components (w, x, y, z) */
  static Quaternion of(Number w, Number x, Number y, Number z) {
    return new QuaternionImpl(RealScalar.of(w), Tensors.vector(x, y, z));
  }

  @Override // from Scalar
  Quaternion multiply(Scalar scalar);

  @Override // from Scalar
  Quaternion negate();

  @Override // from Scalar
  Quaternion divide(Scalar scalar);

  @Override // from Scalar
  Quaternion under(Scalar scalar);

  @Override // from Scalar
  Quaternion reciprocal();

  @Override // from ConjugateInterface
  Quaternion conjugate();

  @Override // from ExpInterface
  Quaternion exp();

  @Override // from LogInterface
  Quaternion log();

  @Override // from PowerInterface
  Quaternion power(Scalar exponent);

  @Override // from SignInterface
  Quaternion sign();

  @Override // from SqrtInterface
  Quaternion sqrt();

  /** @return real part */
  Scalar w();

  /** @return unmodifiable vector of length 3 with coefficients {x, y, z} */
  Tensor xyz();
}
