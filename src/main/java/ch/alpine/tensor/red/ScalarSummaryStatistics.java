// code by jph
package ch.alpine.tensor.red;

import java.util.IntSummaryStatistics;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collector;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** ScalarSummaryStatistics tracks the sum, minimum, and maximum in a single pass
 * over a stream of {@link Scalar}s. The stream may be processed in parallel.
 * 
 * <p>The scalars are required to be comparable. For instance, complex numbers do
 * not have a natural ordering, therefore the minimum is not well defined.
 * ScalarSummaryStatistics does not operate on complex numbers or quaterions.
 * 
 * <p>The string expression of an instance of ScalarSummaryStatistics is of the form
 * <pre>
 * ScalarSummaryStatistics{count=4, sum=24[s], min=3[s], average=6[s], max=11[s]}
 * </pre>
 * 
 * <p>inspired by {@link IntSummaryStatistics}
 * 
 * @see MinMax */
public final class ScalarSummaryStatistics implements Consumer<Scalar> {
  /** Example:
   * <pre>
   * ScalarSummaryStatistics scalarSummaryStatistics = Tensors.vector(4, 2, 10, 8, 1, 3)
   * .stream().parallel().map(Scalar.class::cast).collect(ScalarSummaryStatistics.collector());
   * scalarSummaryStatistics.getMin() == 1
   * scalarSummaryStatistics.getMax() == 10
   * scalarSummaryStatistics.getSum() == 28
   * </pre>
   * 
   * @return */
  public static Collector<Scalar, ScalarSummaryStatistics, ScalarSummaryStatistics> collector() {
    return ScalarSummaryStatisticsCollector.INSTANCE;
  }

  // ---
  private long count = 0;
  private Scalar cnt = null;
  private Scalar avg = null;
  private Scalar min = null;
  private Scalar max = null;

  @Override // from Consumer
  public void accept(Scalar scalar) {
    ++count;
    if (Objects.isNull(min)) {
      cnt = scalar.one();
      avg = scalar;
      min = scalar;
      max = scalar;
    } else {
      cnt = cnt.add(scalar.one());
      avg = avg.add(scalar.subtract(avg).divide(cnt));
      min = Min.of(min, scalar);
      max = Max.of(max, scalar);
    }
  }

  /** Quote from {@link Collector}:
   * "function which combines two partial results into a combined result"
   * 
   * @param other
   * @return */
  public ScalarSummaryStatistics combine(ScalarSummaryStatistics other) {
    if (0 == other.count)
      return this;
    if (0 == count)
      return other;
    count += other.count;
    cnt = cnt.add(other.cnt);
    avg = avg.add(other.avg.subtract(avg).multiply(other.cnt).divide(cnt));
    min = Min.of(min, other.min);
    max = Max.of(max, other.max);
    return this;
  }

  /** @return sum of scalars in stream or null if stream is empty */
  public Scalar getSum() {
    return Objects.isNull(avg) //
        ? null
        : avg.multiply(cnt);
  }

  /** @return min of scalars in stream or null if stream is empty */
  public Scalar getMin() {
    return min;
  }

  /** @return max of scalars in stream or null if stream is empty */
  public Scalar getMax() {
    return max;
  }

  /** @return average of scalars in stream or null if stream is empty
   * @throws Exception if scalar type does not support division by {@link RealScalar} */
  public Scalar getAverage() {
    return avg;
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
    return "ScalarSummaryStatistics" + //
        "{count=" + getCount() + //
        ", min=" + getMin() + //
        ", average=" + getAverage() + //
        ", max=" + getMax() + "}";
  }
}
