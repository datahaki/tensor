// code by jph
package ch.ethz.idsc.tensor.api;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Cosh;
import ch.ethz.idsc.tensor.sca.Sin;
import ch.ethz.idsc.tensor.sca.Sinh;

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
