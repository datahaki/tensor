// code by jph
package ch.ethz.idsc.tensor.qty;

/** auxiliary functions and operators for {@link Unit} */
public enum UnitQ {
  ;
  /** Remark: equivalent to check
   * <pre>
   * unit.map().isEmpty()
   * </pre>
   * 
   * @param unit
   * @return true if given unit is dimension-less
   * @throws Exception if given unit is null */
  public static boolean isOne(Unit unit) {
    return unit.equals(Unit.ONE);
  }
}
