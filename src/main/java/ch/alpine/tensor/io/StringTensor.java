// code by jph
package ch.alpine.tensor.io;

import ch.alpine.tensor.Slash;
import ch.alpine.tensor.Tensor;

/** auxiliary functions related to {@link StringScalar} */
public enum StringTensor {
  ;
  /** StringTensor.vector("IDSC", "ETH-Z", "ch") gives the vector of
   * three {@link StringScalar}s {"IDSC", "ETH-Z", "ch"}.
   * 
   * One application is to create a row as column header for export in
   * {@link CsvFormat}.
   * 
   * @param strings
   * @return */
  public static Tensor vector(String... strings) {
    return Slash.of(strings, StringScalar::of);
  }
}
