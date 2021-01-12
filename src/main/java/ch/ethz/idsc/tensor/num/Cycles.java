// code by jph
package ch.ethz.idsc.tensor.num;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.red.Tally;
import ch.ethz.idsc.tensor.sca.Sign;

/** immutable
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Cycles.html">Cycles</a> */
public class Cycles implements Serializable {
  private static final long serialVersionUID = 4308121452267187557L;
  private static final Cycles IDENTITY = new Cycles(Collections.emptyNavigableMap());

  /** Example:
   * <pre>
   * tensor == {{1, 20}, {4, 10, 19, 6, 18}, {5, 9}, {7, 14, 13}}
   * </pre>
   * 
   * @param tensor
   * @return */
  public static Cycles of(Tensor tensor) {
    return new Cycles(map(check(tensor)));
  }

  /** @param string for instance "{{1, 20}, {4, 10, 19, 6, 18}, {5, 9}, {7, 14, 13}}"
   * @return */
  public static Cycles of(String string) {
    return of(Tensors.fromString(string));
  }

  /** @return identity permutation corresponding to Cycles[{}] */
  public static Cycles identity() {
    return IDENTITY;
  }

  /***************************************************/
  /** @param tensor {{}, {2, 1}, {3}}
   * @return {{2, 1}}
   * @throws Exception if an entry is negative, or not an integer, or appears more than once */
  private static Stream<Tensor> check(Tensor tensor) {
    Map<Scalar, Long> map = Tally.of(tensor.stream() //
        .flatMap(Tensor::stream) //
        .map(Scalar.class::cast));
    boolean value = map.keySet().stream() //
        .allMatch(scalar -> IntegerQ.of(scalar) && Sign.isPositiveOrZero(scalar));
    boolean allMatch = map.values().stream().allMatch(tally -> 1 == tally);
    if (value && allMatch)
      return tensor.stream().filter(cycle -> 1 < cycle.length()); //
    throw TensorRuntimeException.of(tensor);
  }

  private static NavigableMap<Integer, Integer> map(Stream<Tensor> stream) {
    NavigableMap<Integer, Integer> navigableMap = new TreeMap<>();
    stream.forEach(cycle -> {
      int prev = Scalars.intValueExact(Last.of(cycle).Get());
      for (Tensor next : cycle)
        navigableMap.put(prev, prev = Scalars.intValueExact(next.Get()));
    });
    return navigableMap;
  }

  /***************************************************/
  private final NavigableMap<Integer, Integer> navigableMap;

  private Cycles(NavigableMap<Integer, Integer> navigableMap) {
    this.navigableMap = navigableMap;
  }

  private void cycleInterate(Consumer<Tensor> consumer) {
    Set<Integer> set = new HashSet<>();
    for (Entry<Integer, Integer> entry : navigableMap.entrySet()) {
      int seed = entry.getKey();
      int next = entry.getValue();
      if (!set.contains(seed)) {
        List<Integer> list = new ArrayList<>();
        while (set.add(seed)) {
          list.add(seed);
          next = navigableMap.get(seed = next);
        }
        Integers.requirePositive(list.size());
        consumer.accept(Tensors.vector(list));
      }
    }
  }

  /* package */ static final Collector<Entry<Integer, Integer>, ?, NavigableMap<Integer, Integer>> COLLECTOR = //
      Collectors.toMap(Entry::getValue, Entry::getKey, (v1, v2) -> null, TreeMap::new);

  /** Hint: InversePermutation in Mathematica
   * 
   * @return */
  public Cycles inverse() {
    return new Cycles(navigableMap.entrySet().stream().collect(COLLECTOR));
  }

  /** Hint: PermutationProduct in Mathematica
   * 
   * @param cycles
   * @return */
  public Cycles combine(Cycles cycles) {
    Map<Integer, Integer> b_map = cycles.navigableMap;
    NavigableMap<Integer, Integer> result = new TreeMap<>();
    Set<Integer> set = new HashSet<>();
    for (Entry<Integer, Integer> entry : navigableMap.entrySet()) {
      int seed = entry.getKey();
      set.add(seed);
      int next = entry.getValue();
      int dest = b_map.containsKey(next) ? b_map.get(next) : next;
      if (seed != dest)
        result.put(seed, dest);
    }
    for (Entry<Integer, Integer> entry : b_map.entrySet()) {
      int seed = entry.getKey();
      if (set.add(seed))
        result.put(seed, entry.getValue());
    }
    return new Cycles(result);
  }

  /** @param exponent
   * @return
   * @throws Exception if exponent is not an integer */
  public Cycles power(Scalar exponent) {
    return power(Scalars.bigIntegerValueExact(exponent));
  }

  /** Hint: PermutationPower in Mathematica
   * 
   * @param bigInteger
   * @return */
  public Cycles power(BigInteger bigInteger) {
    Builder<Tensor> builder = Stream.builder();
    cycleInterate(cycle -> StaticHelper.CYCLES_POWER.raise(new Cycles(map(Stream.of(cycle))), //
        bigInteger.mod(BigInteger.valueOf(cycle.length()))).cycleInterate(builder));
    return new Cycles(map(builder.build())); // most efficient?
  }

  /** @return map without singletons, i.e. no trivial associations a -> a exist */
  public NavigableMap<Integer, Integer> navigableMap() {
    return Collections.unmodifiableNavigableMap(navigableMap);
  }

  /** @return for instance {{1, 20}, {4, 10, 19, 6, 18}, {5, 9}, {7, 14, 13}} */
  public Tensor toTensor() {
    Builder<Tensor> builder = Stream.builder();
    cycleInterate(builder);
    return Tensor.of(builder.build());
  }

  /***************************************************/
  @Override // from Object
  public boolean equals(Object object) {
    if (object instanceof Cycles) {
      Cycles cycles = (Cycles) object;
      return navigableMap.equals(cycles.navigableMap);
    }
    return false;
  }

  @Override // from Object
  public int hashCode() {
    return navigableMap.hashCode();
  }

  @Override // from Object
  public String toString() {
    return toTensor().toString();
  }
}
