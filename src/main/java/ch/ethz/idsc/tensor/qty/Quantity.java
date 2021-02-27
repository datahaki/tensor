// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.Objects;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.api.AbsInterface;
import ch.ethz.idsc.tensor.api.ArcTanInterface;
import ch.ethz.idsc.tensor.api.ArgInterface;
import ch.ethz.idsc.tensor.api.ComplexEmbedding;
import ch.ethz.idsc.tensor.api.ConjugateInterface;
import ch.ethz.idsc.tensor.api.PowerInterface;
import ch.ethz.idsc.tensor.api.RoundingInterface;
import ch.ethz.idsc.tensor.api.SignInterface;
import ch.ethz.idsc.tensor.api.SqrtInterface;
import ch.ethz.idsc.tensor.ext.ObjectFormat;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.StringScalar;

/** {@link Quantity} represents a magnitude and unit.
 * <pre>
 * Mathematica::Quantity[8, "Kilograms"^2*"Meters"]
 * Tensor::Quantity.of(8, "kg^2*m")
 * </pre>
 * 
 * <p>The implementation is consistent with Mathematica:
 * The NumberQ relations for {@link Quantity} evaluate to
 * <pre>
 * NumberQ[Quantity[3, "Meters"]] == False
 * MachineNumberQ[Quantity[3.123, "Meters"]] == False
 * </pre>
 * 
 * <p>The sum of two quantities is well defined whenever the units are identical.
 * Two quantities with different units are added if one of the values equals to
 * zero. In that case, the result carries the unit of the non-zero input quantity.
 * 
 * <p>Addition is symmetric and associative. In particular, if both magnitudes
 * are zero but the units don't match, then the result is unitless.
 * 
 * <p>For export and import of tensors with scalars of type {@link Quantity} use
 * {@link ObjectFormat} and {@link CsvFormat}.
 * 
 * <p>Two quantities are comparable only if they have the same unit. Otherwise an
 * exception is thrown.
 * 
 * <p>Different units should mapped to a common unit system before carrying out
 * operations.
 * <pre>
 * Scalar a = Quantity.of(200, "g");
 * Scalar b = Quantity.of(1, "kg");
 * Total.of(Tensors.of(a, b).map(UnitSystem.SI())) == 6/5[kg]
 * </pre>
 * whereas <code>a.add(b)</code> throws an Exception.
 * 
 * <p>Invoking {@link #number()} on an instance of {@link Quantity} throws an
 * Exception. The preferred method to extract the value part from a quantity is
 * {@link QuantityMagnitude}. In rare cases the tensor library resorts to
 * {@link Unprotect#withoutUnit(Scalar)}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Quantity.html">Quantity</a> */
public interface Quantity extends Scalar, //
    AbsInterface, ArcTanInterface, ArgInterface, Comparable<Scalar>, ComplexEmbedding, //
    ConjugateInterface, PowerInterface, RoundingInterface, SignInterface, SqrtInterface {
  static final char UNIT_OPENING_BRACKET = '[';
  static final char UNIT_CLOSING_BRACKET = ']';

  /** Hint: function does not check parameters for null, although
   * null as input is likely to cause problems subsequently.
   * 
   * @param value
   * @param unit for instance Unit.of("m*s^-1")
   * @return
   * @throws Exception if value is instance of {@code Quantity} or {@link StringScalar}
   * @throws Exception if either parameter equals null */
  static Scalar of(Scalar value, Unit unit) {
    if (value instanceof Quantity || //
        value instanceof StringScalar)
      throw TensorRuntimeException.of(value);
    return QuantityImpl.of( //
        Objects.requireNonNull(value), //
        unit);
  }

  /** Hint: function does not check parameters for null, although
   * null as input is likely to cause problems subsequently.
   * 
   * @param value
   * @param string for instance "m*s^-2"
   * @return
   * @throws Exception if value is instance of {@code Quantity}
   * @throws Exception if string does not represent unit
   * @throws Exception if either parameter equals null */
  static Scalar of(Scalar value, String string) {
    return of(value, Unit.of(string));
  }

  /** creates quantity with number encoded as {@link RealScalar}
   * 
   * Hint: function does not check parameters for null, although
   * null as input is likely to cause problems subsequently.
   * 
   * @param number non-null
   * @param unit
   * @return
   * @throws Exception if parameter number equals null */
  static Scalar of(Number number, Unit unit) {
    return QuantityImpl.of(RealScalar.of(number), unit);
  }

  /** creates quantity with number encoded as {@link RealScalar}
   * 
   * @param number
   * @param string for instance "kg^3*m*s^-2"
   * @return
   * @throws Exception if string does not represent unit
   * @throws Exception if either parameter equals null */
  static Scalar of(Number number, String string) {
    return QuantityImpl.of(RealScalar.of(number), Unit.of(string));
  }

  /** Hint: Typically, the function is not called in the application layer.
   * 
   * Quote from Mathematica::QuantityMagnitude
   * "gives the amount of the specified quantity"
   * "gives the magnitude value of a Quantity"
   * 
   * @return value of quantity without unit
   * @see QuantityMagnitude */
  Scalar value();

  /** Hint: Typically, the function is not called in the application layer.
   * Instead, the unit should be retrieved by {@link QuantityUnit#of(Scalar)}.
   * 
   * @return unit of quantity
   * @see QuantityUnit */
  Unit unit();
}
