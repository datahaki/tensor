// code by jph
package ch.alpine.tensor;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.io.TableBuilder;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;

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
    return Tensor.of(Arrays.stream(tensors));
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
    throw new Throw(tensor);
  }

  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * @param tensor
   * @return Scalar.LENGTH if given tensor is a vector, or else estimate of Dimensions[tensor].get(1)
   * based on first entry of tensor
   * @throws Exception if tensor is a scalar */
  public static int dimension1Hint(Tensor tensor) {
    return tensor.stream() //
        .findFirst() //
        .map(Tensor::length) //
        .orElse(Scalar.LENGTH);
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
   * @param tensor
   * @return whether scalar entries are quantities of identical unit */
  public static boolean isUnitUnique(Tensor tensor) {
    return Flatten.scalars(tensor) //
        .map(QuantityUnit::of) //
        .distinct() //
        .skip(1) //
        .findAny() //
        .isEmpty();
  }

  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * @param scalar
   * @return */
  public static Scalar negateUnit(Scalar scalar) {
    return scalar instanceof Quantity quantity //
        ? Quantity.of(quantity.value(), quantity.unit().negate())
        : Objects.requireNonNull(scalar);
  }

  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * @param scalar
   * @return */
  public static Scalar zero_negateUnit(Scalar scalar) {
    return negateUnit(scalar.zero());
  }

  // ---
  /** THE USE OF THIS FUNCTION IN THE APPLICATION LAYER IS NOT RECOMMENDED !
   * 
   * useful in the test scope to access a resource as a file
   * 
   * @param string
   * @return
   * @throws Exception if string does not correspond to a resource file, or directory */
  public static File file(String string) {
    URL url = Unprotect.class.getResource(string);
    if (Objects.nonNull(url)) {
      File file = new File(url.getFile());
      if (file.exists())
        return file;
    }
    throw new IllegalArgumentException(string);
  }

  /** @param file
   * @param tensor */
  public static void _export(File file, Tensor tensor) {
    try {
      Export.of(file, tensor);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  /** @param file
   * @return */
  public static Tensor _import(File file) {
    try {
      return Import.of(file);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }
}
