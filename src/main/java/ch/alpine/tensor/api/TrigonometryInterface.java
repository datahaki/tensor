// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Cosh;
import ch.alpine.tensor.sca.Sin;
import ch.alpine.tensor.sca.Sinh;

/** functions used in {@link Sin}, {@link Sinh}, {@link Cos}, and {@link Cosh}.
 * Scalar types that implement {@link TrigonometryInterface} include
 * {@link RealScalar} and {@link ComplexScalar} */
public interface TrigonometryInterface {
  /** @return cos of this */
  Scalar cos();

  /** @return cosh of this */
  Scalar cosh();

  /** @return sin of this */
  Scalar sin();

  /** @return sinh of this */
  Scalar sinh();
}
