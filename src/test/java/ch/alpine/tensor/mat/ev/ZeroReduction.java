// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.EnumSet;
import java.util.IntSummaryStatistics;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.red.MinMax;

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
public final class ZeroReduction implements Consumer<Scalar> {
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
  public static Collector<Scalar, ZeroReduction, ZeroReduction> collector() {
    return ZeroReductionCollector.INSTANCE;
  }

  // ---
  private Scalar cnt = null;

  @Override // from Consumer
  public void accept(Scalar scalar) {
    cnt = scalar.zero();
  }

  /** Quote from {@link Collector}:
   * "function which combines two partial results into a combined result"
   * 
   * @param other
   * @return */
  public ZeroReduction combine(ZeroReduction other) {
    return this;
  }

  /** @return sum of scalars in stream or null if stream is empty */
  public Scalar getSum() {
    return cnt;
  }

  @Override // from Object
  public String toString() {
    return "ZeroReduction" + //
        "{count=" + cnt;
  }

  /* package */ enum ZeroReductionCollector implements //
      Collector<Scalar, ZeroReduction, ZeroReduction> {
    INSTANCE;

    @Override // from Collector
    public Supplier<ZeroReduction> supplier() {
      return ZeroReduction::new;
    }

    @Override // from Collector
    public BiConsumer<ZeroReduction, Scalar> accumulator() {
      return ZeroReduction::accept;
    }

    @Override // from Collector
    public BinaryOperator<ZeroReduction> combiner() {
      return ZeroReduction::combine;
    }

    @Override // from Collector
    public Function<ZeroReduction, ZeroReduction> finisher() {
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
