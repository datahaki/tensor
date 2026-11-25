// code by jph
package ch.alpine.tensor.red;

import java.util.EnumSet;
import java.util.IntSummaryStatistics;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Sqrt;

/** ScalarSummaryStatistics tracks the sum, minimum, and maximum in a single pass
 * over a stream of {@link Scalar}s. The stream may be processed in parallel.
 * 
 * <p>The scalars are required to be comparable. For instance, complex numbers do
 * not have a natural ordering, therefore the minimum is not well-defined.
 * ScalarSummaryStatistics does not operate on complex numbers or quaternions.
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
  private Scalar cnt = null;
  private Scalar avg = null;
  private Scalar m_2 = null;
  private Scalar min = null;
  private Scalar max = null;

  @Override // from Consumer
  public void accept(Scalar scalar) {
    if (isEmpty()) {
      cnt = scalar.one();
      avg = scalar;
      m_2 = AbsSquared.FUNCTION.apply(scalar.zero());
      min = scalar;
      max = scalar;
    } else {
      Scalar del = scalar.subtract(avg);
      cnt = cnt.add(scalar.one());
      avg = avg.add(del.divide(cnt));
      Scalar dl2 = scalar.subtract(avg);
      m_2 = m_2.add(del.multiply(dl2));
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
    if (other.isEmpty())
      return this;
    if (isEmpty())
      return other;
    Scalar com = cnt.add(other.cnt);
    Scalar del = other.avg.subtract(avg);
    avg = avg.add(del.multiply(other.cnt).divide(com));
    Scalar fac = Times.of(del, del, cnt, other.cnt).divide(com);
    m_2 = m_2.add(other.m_2).add(fac);
    min = Min.of(min, other.min);
    max = Max.of(max, other.max);
    cnt = com;
    return this;
  }

  private boolean isEmpty() {
    return Objects.isNull(cnt);
  }

  /** @return sum of scalars in stream or null if stream is empty */
  public Scalar getSum() {
    return isEmpty() //
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
  public Scalar getMean() {
    return avg;
  }

  /** @return
   * @throws Exception if stream has insufficient count
   * @see Variance */
  public Scalar getVariance() {
    return isEmpty() //
        ? null
        : m_2.divide(cnt.subtract(cnt.one()));
  }

  /** @return
   * @see StandardDeviation */
  public Scalar getStandardDeviation() {
    return Sqrt.FUNCTION.apply(getVariance());
  }

  /** @return clip[min, max], or null if stream is empty */
  public Clip getClip() {
    return Objects.isNull(min) //
        ? null
        : Clips.interval(getMin(), getMax());
  }

  @Override // from Object
  public String toString() {
    return "ScalarSummaryStatistics" + //
        "{min=" + getMin() + //
        ", max=" + getMax() + //
        ", average=" + getMean() + //
        ", var=" + getVariance() + "}";
  }

  enum ScalarSummaryStatisticsCollector implements //
      Collector<Scalar, ScalarSummaryStatistics, ScalarSummaryStatistics> {
    INSTANCE;

    @Override // from Collector
    public Supplier<ScalarSummaryStatistics> supplier() {
      return ScalarSummaryStatistics::new;
    }

    @Override // from Collector
    public BiConsumer<ScalarSummaryStatistics, Scalar> accumulator() {
      return ScalarSummaryStatistics::accept;
    }

    @Override // from Collector
    public BinaryOperator<ScalarSummaryStatistics> combiner() {
      return ScalarSummaryStatistics::combine;
    }

    @Override // from Collector
    public Function<ScalarSummaryStatistics, ScalarSummaryStatistics> finisher() {
      return Function.identity();
    }

    @Override // from Collector
    public Set<Characteristics> characteristics() {
      return EnumSet.of(
          // in order for concurrent we need to introduce "synchronized" to all methods
          // which results in overhead even in a single thread environment
          // Characteristics.CONCURRENT, // we don't understand the specs
          Characteristics.UNORDERED, //
          Characteristics.IDENTITY_FINISH);
    }
  }
}
