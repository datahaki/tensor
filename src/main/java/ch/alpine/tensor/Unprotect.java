// code by jph
package ch.alpine.tensor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.io.TableBuilder;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;

/** Notice:
 * 
 * <b>THE USE OF 'UNPROTECT' IN THE APPLICATION LAYER IS NOT RECOMMENDED !</b>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Unprotect.html">Unprotect</a> */
public enum Unprotect {
  ;
  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * @param list non-null
   * @return tensor backed by given list
   * @throws Exception if given list is null
   * @see TableBuilder */
  public static Tensor using(List<Tensor> list) {
    return new TensorImpl(Objects.requireNonNull(list));
  }

  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * @param tensors
   * @return It That Must Not Be Described
   * @see Tensors#of(Tensor...) */
  public static Tensor byRef(Tensor... tensors) {
    return Tensor.of(Stream.of(tensors));
  }

  // ---
  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * @param tensor
   * @return Scalar.LENGTH if given tensor is a vector, or else Dimensions[tensor].get(1)
   * @throws Exception if tensor is a scalar, or first level entries do not have regular length */
  public static int dimension1(Tensor tensor) {
    int length = dimension1Hint(tensor);
    if (tensor.stream().skip(1).allMatch(entry -> entry.length() == length))
      return length;
    throw TensorRuntimeException.of(tensor);
  }

  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * @param tensor
   * @return Scalar.LENGTH if given tensor is a vector, or else estimate of Dimensions[tensor].get(1)
   * based on first entry of tensor
   * @throws Exception if tensor is a scalar */
  public static int dimension1Hint(Tensor tensor) {
    return tensor.stream().findFirst().map(Tensor::length).orElse(Scalar.LENGTH);
  }

  // ---
  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * Examples:
   * <pre>
   * Unprotect.withoutUnit(3.1415926) == 3.1415926
   * Unprotect.withoutUnit(1 + 2 * I) == 1 + 2 * I
   * Unprotect.withoutUnit(Quantity[3 / 4, "km*h^-1"]) == 3 / 4
   * </pre>
   * 
   * @param scalar non-null
   * @return
   * @throws Exception if given scalar is null */
  public static Scalar withoutUnit(Scalar scalar) {
    return scalar instanceof Quantity quantity //
        ? quantity.value()
        : Objects.requireNonNull(scalar);
  }

  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * Examples:
   * <pre>
   * Unprotect.negateUnit(0[m^2]) gives 0[m^-2]
   * </pre>
   * 
   * Remark: see BenIsraelCohen.UnitNegate
   * 
   * @param scalar with zero value
   * @return
   * @throws Exception if scalar does not satisfy {@link Scalars#isZero(Scalar)} */
  public static Scalar negateUnit(Scalar scalar) {
    return scalar instanceof Quantity quantity //
        ? Quantity.of(Scalars.requireZero(quantity.value()), quantity.unit().negate())
        : Scalars.requireZero(scalar);
  }

  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * @param tensor
   * @return whether scalar entries are quantities of identical unit */
  public static boolean isUnitUnique(Tensor tensor) {
    return tensor.flatten(-1) //
        .map(Scalar.class::cast) //
        .map(QuantityUnit::of) //
        .distinct() //
        .skip(1) //
        .findAny() //
        .isEmpty();
  }

  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * @param tensor
   * @return unique unit of quantities in given tensor
   * @throws Exception if quantities consist of mixed units */
  public static Unit getUnitUnique(Tensor tensor) {
    List<Unit> list = tensor.flatten(-1) //
        .map(Scalar.class::cast) //
        .map(QuantityUnit::of) //
        .distinct() //
        .limit(2) //
        .collect(Collectors.toList());
    if (list.size() == 1)
      return list.get(0);
    throw new IllegalArgumentException(list.toString());
  }
}
