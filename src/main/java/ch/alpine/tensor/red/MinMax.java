// code by jph
package ch.alpine.tensor.red;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collector;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** minMax tracks the minimum, maximum, as well as the number of elements in a single
 * pass over a stream of {@link Scalar}s. The stream may be processed in parallel.
 * 
 * <p>The scalars are required to be comparable. For instance, complex numbers do
 * not have a natural ordering, therefore the minimum/maximum are not well defined.
 * MinMax does not operate on complex numbers or quaterions.
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
public class MinMax implements Consumer<Scalar> {
  /** Example:
   * <pre>
   * MinMax minMax = Tensors.vector(1, 4, 2, 8, 3, 10)
   * .stream().parallel().map(Scalar.class::cast).collect(MinMax.collector());
   * minMax.getMin() == 1
   * minMax.getMax() == 10
   * </pre>
   * 
   * @return */
  public static Collector<Scalar, MinMax, MinMax> collector() {
    return MinMaxCollector.INSTANCE;
  }

  // ---
  private long count = 0;
  private Scalar min = null;
  private Scalar max = null;

  @Override // from Consumer
  public void accept(Scalar scalar) {
    ++count;
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
    if (0 == other.count)
      return this;
    if (0 == count)
      return other;
    count += other.count;
    min = Min.of(min, other.min);
    max = Max.of(max, other.max);
    return this;
  }

  /** @return min of scalars in stream or null if stream is empty */
  public Scalar getMin() {
    return min;
  }

  /** @return max of scalars in stream or null if stream is empty */
  public Scalar getMax() {
    return max;
  }

  /** @return number of scalars in stream */
  public long getCount() {
    return count;
  }

  /** @return clip[min, max], or null if stream is empty */
  public Clip getClip() {
    return 0 < count //
        ? Clips.interval(getMin(), getMax())
        : null;
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("MinMax", min, max);
  }
}
