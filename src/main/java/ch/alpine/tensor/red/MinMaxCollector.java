// code by jph
package ch.alpine.tensor.red;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import ch.alpine.tensor.Scalar;

/* package */ enum MinMaxCollector implements Collector<Scalar, MinMax, MinMax> {
  INSTANCE;

  @Override // from Collector
  public Supplier<MinMax> supplier() {
    return MinMax::new;
  }

  @Override // from Collector
  public BiConsumer<MinMax, Scalar> accumulator() {
    return MinMax::accept;
  }

  @Override // from Collector
  public BinaryOperator<MinMax> combiner() {
    return MinMax::combine;
  }

  @Override // from Collector
  public Function<MinMax, MinMax> finisher() {
    return Function.identity();
  }

  @Override // from Collector
  public Set<Characteristics> characteristics() {
    return EnumSet.of(Characteristics.CONCURRENT, Characteristics.IDENTITY_FINISH);
  }
}
