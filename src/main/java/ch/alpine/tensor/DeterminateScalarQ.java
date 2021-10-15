// code by jph
package ch.alpine.tensor;

/** not Infinity or NaN.
 * 
 * <p>check is useful after division by a numeric value equal or close to zero */
public enum DeterminateScalarQ {
  ;
  /** @param scalar
   * @return whether scalar is in exact precision or a machine number but not Infinity or NaN */
  public static boolean of(Scalar scalar) {
    if (scalar instanceof ComplexScalar) {
      ComplexScalar complexScalar = (ComplexScalar) scalar;
      return _of(complexScalar.real()) //
          && _of(complexScalar.imag());
    }
    return _of(Unprotect.withoutUnit(scalar));
  }

  /** @param scalar
   * @return */
  private static boolean _of(Scalar scalar) {
    return MachineNumberQ.of(scalar) //
        || ExactScalarQ.of(scalar);
  }

  /** @param scalar
   * @return given scalar
   * @throws Exception if given scalar does not satisfy the predicate {@link DeterminateScalarQ} */
  public static Scalar require(Scalar scalar) {
    if (of(scalar))
      return scalar;
    throw TensorRuntimeException.of(scalar);
  }
}
