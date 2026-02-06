// code by jph
package ch.alpine.tensor.io;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.ObjectFormat;
import ch.alpine.tensor.qty.Quantity;

/** comma separated values format
 * 
 * <p>The csv format cannot reliably encode the {@link Dimensions}
 * of tensors. For instance, csv does not distinguish between
 * vectors and matrices with dimensions [n x 1] or [1 x n].
 * 
 * <p>If possible, only use {@link CsvFormat} for export of
 * vectors or matrices to other applications such as MATLAB.
 * {@link MatlabExport} preserves dimensions of multidimensional arrays.
 * 
 * <p>Careful: ensure that decimal numbers adhere to the java format.
 * The letter for the exponent has to be capitalized:
 * <ul>
 * <li>{@code 1.23E-45} valid numeric expression, imported as {@link DoubleScalar}
 * <li>{@code 1.23e-45} non numeric, imported as {@link StringScalar}
 * </ul>
 * 
 * <p>MATLAB::dlmwrite creates CSV files in the required decimal format
 * <pre>
 * dlmwrite(filename, matrix, 'precision', '%E');
 * dlmwrite(filename, matrix, '-append', 'precision', '%E');
 * </pre>
 * Reference: https://www.mathworks.com/help/matlab/ref/dlmwrite.html
 * 
 * <p>For export of matrices to Mathematica, {@link Put} is
 * the preferred option. However, the csv format may produce smaller
 * files. Mathematica::Import of csv files requires the table entries
 * to be decimal numbers. In particular, exact fractions, e.g. 5/7,
 * are imported to string expressions "5/7". The scalar operator
 * {@link CsvFormat#strict()} can be used to map the entries to decimal
 * expressions prior to export.
 * 
 * <p>Within the realm of Java, use {@link ObjectFormat}
 * to store and reload tensors, and do not use csv format.
 *
 * @see XsvFormat
 * @see Export */
public enum CsvFormat {
  ;
  /** the scalar operator attempts to guarantee that the CSV import in Mathematica
   * as numeric values.
   * 
   * <p>Scalars of type
   * <ul>
   * <li>{@link RationalScalar} are converted to {@link DoubleScalar} unless the
   * fraction has denominator == 1.
   * <li>{@link StringScalar} is enclosed in quotes if necessary. The result must
   * not contain any other quotes character.
   * <li>{@link ComplexScalar}, or {@link Quantity} are not allowed.
   * </ul>
   * 
   * <p>Example use:
   * <pre>
   * Export.of(Path.of("name.csv"), tensor.map(CsvFormat.strict()));
   * </pre> */
  public static ScalarUnaryOperator strict() {
    return CsvHelper.FUNCTION;
  }
}
