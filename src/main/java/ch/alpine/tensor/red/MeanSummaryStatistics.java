// code by jph
package ch.alpine.tensor.red;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
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
import ch.alpine.tensor.io.MathematicaFormat;

public class MeanSummaryStatistics implements Consumer<Tensor> {
  /** Example:
   * <pre>
   * MeanSummaryStatistics meanSummaryStatistics = Tensors.vector(4, 2, 10, 8, 1, 3)
   * .stream().parallel().collect(MeanSummaryStatistics.collector());
   * </pre>
   * 
   * @return */
  public static Collector<Tensor, MeanSummaryStatistics, MeanSummaryStatistics> collector() {
    return MeanSummaryStatisticsCollector.INSTANCE;
  }

  // ---
  private Scalar cnt = null;
  private Tensor avg = null;

  private boolean isEmpty() {
    return Objects.isNull(cnt);
  }

  @Override // from Consumer
  public void accept(Tensor tensor) {
    if (isEmpty()) {
      cnt = EqualsReduce.one(tensor);
      avg = tensor.copy();
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
    if (other.isEmpty())
      return this;
    if (isEmpty())
      return other;
    Scalar com = cnt.add(other.cnt);
    Tensor del = other.avg.subtract(avg);
    avg = avg.add(del.multiply(other.cnt).divide(com));
    cnt = com;
    return this;
  }

  /** @return average of scalars in stream or null if stream is empty
   * @throws Exception if scalar type does not support division by {@link RealScalar} */
  public Optional<Tensor> getMean() {
    return Optional.ofNullable(avg).map(Tensor::copy);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("MeanSummaryStatistics", getMean());
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
