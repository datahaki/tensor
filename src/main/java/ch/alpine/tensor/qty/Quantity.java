// code by jph
package ch.alpine.tensor.qty;

import java.util.Objects;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ArgInterface;
import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.api.ConjugateInterface;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.ext.ObjectFormat;
import ch.alpine.tensor.io.CsvFormat;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.sca.pow.PowerInterface;
import ch.alpine.tensor.sca.pow.SqrtInterface;
import ch.alpine.tensor.sca.tri.ArcTanInterface;

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
 * <p>The sum of two quantities is well-defined whenever the units are identical.
 * Addition is symmetric. Addition is associative subject to numerical precision.
 * 
 * <p>For export and import of tensors with scalars of type {@link Quantity} use
 * {@link ObjectFormat} and {@link CsvFormat}.
 * 
 * <p>Two quantities are comparable only if they have the same unit. Otherwise, an
 * exception is thrown.
 * 
 * <p>Different units should be mapped to a common unit system before carrying out
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
    ConjugateInterface, PowerInterface, SignInterface, SqrtInterface {
  char UNIT_OPENING_BRACKET = '[';
  char UNIT_CLOSING_BRACKET = ']';

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
      throw new Throw(value);
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
