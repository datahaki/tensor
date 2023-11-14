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
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** minMax tracks the minimum, and the maximum of elements in a single
 * pass over a stream of {@link Scalar}s.
 * 
 * <p>The scalars are required to be comparable. For instance, complex numbers do
 * not have a natural ordering, therefore the minimum/maximum are not well-defined.
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
   * function name is chosen analogous to {@link Collectors#toList()}
   * 
   * @return collector that returns clip that contains min and max of scalars
   * in a stream, or null is the stream has no elements */
  public static Collector<Scalar, ?, Clip> toClip() {
    return Collectors.collectingAndThen(MinMaxCollector.INSTANCE, MinMax::clip);
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

  /** @return clip[min, max], or null if stream is empty */
  public Clip clip() {
    return Objects.isNull(min) //
        ? null
        : Clips.interval(min, max);
  }

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
      return EnumSet.of(
          // Characteristics.CONCURRENT, // we don't understand the specs
          Characteristics.UNORDERED, //
          Characteristics.IDENTITY_FINISH);
    }
  }
}
