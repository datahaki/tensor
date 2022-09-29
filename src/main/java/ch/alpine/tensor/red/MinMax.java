// code by jph
package ch.alpine.tensor.red;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** minMax tracks the minimum, and the maximum of elements in a single
 * pass over a stream of {@link Scalar}s.
 * 
 * <p>The scalars are required to be comparable. For instance, complex numbers do
 * not have a natural ordering, therefore the minimum/maximum are not well defined.
 * MinMax does not operate on complex numbers, or quaternions.
 * 
 * <p>The string expression of an instance of MinMax is of the form
 * <pre>
 * MinMax[min, max]
 * </pre>
 * 
 * MinMax is a reduced version of {@link ScalarSummaryStatistics}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MinMax.html">MinMax</a>
 * 
 * @see ScalarSummaryStatistics */
public final class MinMax implements Consumer<Scalar> {
  /** Examples:
   * <pre>
   * Clip clip = Tensors.vector(4, 2, 10, 8, 1, 3)
   * .stream().map(Scalar.class::cast).collect(MinMax.toClip());
   * clip == Clip[1, 10]
   * </pre>
   * 
   * <pre>
   * MinMax minMax = Tensors.vector(4, 2, 10, 8, 1, 3)
   * .stream().parallel().map(Scalar.class::cast).collect(MinMax.collector());
   * minMax.min() == 1
   * minMax.max() == 10
   * </pre>
   * 
   * @return */
  public static Collector<Scalar, ?, MinMax> collector() {
    return MinMaxCollector.INSTANCE;
  }

  /** @return clip that contains min and max of scalars in a stream,
   * or null is the stream has no elements. */
  public static Collector<Scalar, ?, Clip> toClip() {
    return Collectors.collectingAndThen(collector(), MinMax::clip);
  }

  // ---
  private Scalar min = null;
  private Scalar max = null;

  /* package */ MinMax() {
    // ---
  }

  @Override // from Consumer
  public void accept(Scalar scalar) {
    if (Objects.isNull(min)) {
      min = scalar;
      max = scalar;
    } else {
      min = Min.of(min, scalar);
      max = Max.of(max, scalar);
    }
  }

  /** Quote from {@link Collector}:
   * "function which combines two partial results into a combined result"
   * 
   * @param other
   * @return */
  public MinMax combine(MinMax other) {
    if (Objects.isNull(other.min))
      return this;
    if (Objects.isNull(min))
      return other;
    min = Min.of(min, other.min);
    max = Max.of(max, other.max);
    return this;
  }

  /** @return min of scalars in stream or null if stream is empty */
  public Scalar min() {
    return min;
  }

  /** @return max of scalars in stream or null if stream is empty */
  public Scalar max() {
    return max;
  }

  /** @return clip[min, max], or null if stream is empty */
  public Clip clip() {
    return Objects.isNull(min) //
        ? null
        : Clips.interval(min, max);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("MinMax", min, max);
  }
}
