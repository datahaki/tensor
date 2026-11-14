// code by jph
package ch.alpine.tensor.red;

import java.util.EnumSet;
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
import ch.alpine.tensor.Tensor;

class MeanSummaryStatistics implements Consumer<Tensor> {
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
  public static Collector<Tensor, MeanSummaryStatistics, MeanSummaryStatistics> collector() {
    return MeanSummaryStatisticsCollector.INSTANCE;
  }

  // ---
  private long count = 0;
  private Scalar cnt = null;
  private Tensor avg = null;

  @Override // from Consumer
  public void accept(Tensor tensor) {
    ++count;
    if (Objects.isNull(cnt)) {
      cnt = EqualsReduce.one(tensor);
      avg = tensor;
    } else {
      cnt = cnt.add(cnt.one());
      // LinearBinaryAverage.INSTANCE.split(avg, tensor, cnt.reciprocal());
      avg = avg.add(tensor.subtract(avg).divide(cnt));
    }
  }

  /** Quote from {@link Collector}:
   * "function which combines two partial results into a combined result"
   * 
   * @param other
   * @return */
  public MeanSummaryStatistics combine(MeanSummaryStatistics other) {
    if (0 == other.count)
      return this;
    if (0 == count)
      return other;
    count += other.count;
    Scalar com = cnt.add(other.cnt);
    Tensor del = other.avg.subtract(avg);
    avg = avg.add(del.multiply(other.cnt).divide(com));
    cnt = com;
    return this;
  }

  /** @return average of scalars in stream or null if stream is empty
   * @throws Exception if scalar type does not support division by {@link RealScalar} */
  public Tensor getMean() {
    return Objects.requireNonNull(avg);
  }

  /** @return number of scalars in stream */
  public long getCount() {
    return count;
  }

  @Override // from Object
  public String toString() {
    return "ScalarSummaryStatistics" + //
        "{count=" + getCount() + //
        ", mean=" + getMean() + "}";
  }

  enum MeanSummaryStatisticsCollector implements //
      Collector<Tensor, MeanSummaryStatistics, MeanSummaryStatistics> {
    INSTANCE;

    @Override // from Collector
    public Supplier<MeanSummaryStatistics> supplier() {
      return MeanSummaryStatistics::new;
    }

    @Override // from Collector
    public BiConsumer<MeanSummaryStatistics, Tensor> accumulator() {
      return MeanSummaryStatistics::accept;
    }

    @Override // from Collector
    public BinaryOperator<MeanSummaryStatistics> combiner() {
      return MeanSummaryStatistics::combine;
    }

    @Override // from Collector
    public Function<MeanSummaryStatistics, MeanSummaryStatistics> finisher() {
      return Function.identity();
    }

    @Override // from Collector
    public Set<Characteristics> characteristics() {
      return EnumSet.of(
          // Characteristics.CONCURRENT, // we don't understand the specs
          Characteristics.UNORDERED, //
          Characteristics.IDENTITY_FINISH);
    }
  }
}
