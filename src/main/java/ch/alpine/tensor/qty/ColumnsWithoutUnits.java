// code by jph
package ch.alpine.tensor.qty;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.MatrixQ;

/** The class separates the units from the scalar entries in a given matrix.
 * The result is a matrix without {@link Quantity} as entries. */
public class ColumnsWithoutUnits {
  private static final UnitSystem EMPTY = SimpleUnitSystem.from(Collections.emptyMap());

  /** @param matrix
   * @param unitSystem
   * @return
   * @throws Exception if given matrix is not an array of dimensions n x m
   * @throws Exception if the scalars from a column have incompatible units
   * with respect to given unit system
   * @see MatrixQ */
  public static ColumnsWithoutUnits of(Tensor matrix, UnitSystem unitSystem) {
    return new ColumnsWithoutUnits(matrix, unitSystem);
  }

  /** @param matrix
   * @return
   * @throws Exception if given matrix is not an array of dimensions n x m
   * @throws Exception if the scalars from a column have different units
   * @see MatrixQ */
  public static ColumnsWithoutUnits of(Tensor matrix) {
    return new ColumnsWithoutUnits(matrix, EMPTY);
  }

  // ---
  private final List<Unit> units;
  private final List<ScalarUnaryOperator> magnitudes;
  private final Tensor result;

  private ColumnsWithoutUnits(Tensor matrix, UnitSystem unitSystem) {
    units = matrix.get(0).stream() //
        .map(Scalar.class::cast) //
        .map(unitSystem) //
        .map(QuantityUnit::of) //
        .toList();
    QuantityMagnitude quantityMagnitude = new QuantityMagnitude(unitSystem);
    magnitudes = units.stream().map(quantityMagnitude::in).toList();
    result = Tensor.of(matrix.stream().map(this::row));
  }

  private Tensor row(Tensor vector) {
    Integers.requireEquals(vector.length(), magnitudes.size());
    return Tensor.of(IntStream.range(0, vector.length()) //
        .mapToObj(index -> magnitudes.get(index).apply(vector.Get(index))));
  }

  /** @return list of units that were used to determine the magnitude of
   * each scalar in the respective column. the list has size equal to the
   * number of columns of the input matrix */
  public List<Unit> units() {
    return units;
  }

  /** @return matrix with same dimensions as input matrix */
  public Tensor result() {
    return result;
  }
}
