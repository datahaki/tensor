// code by jph
package ch.ethz.idsc.tensor.num;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.Integers;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Tally;
import ch.ethz.idsc.tensor.sca.Sign;

/** immutable
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Cycles.html">Cycles</a> */
public class Cycles implements Serializable {
  private static final Cycles IDENTITY = new Cycles(Collections.emptyMap());

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
  private static Map<Integer, Integer> map(Tensor tensor) {
    Map<Integer, Integer> map = new HashMap<>();
    for (Tensor cycle : tensor) {
      int prev = parse(Last.of(cycle).Get());
      Iterator<Tensor> iterator = cycle.iterator();
      while (iterator.hasNext())
        map.put(prev, prev = parse(iterator.next().Get()));
    }
    return map;
  }

  private static int parse(Scalar scalar) {
    return Integers.requirePositiveOrZero(Scalars.intValueExact(scalar));
  }

  private static Tensor check(Tensor tensor) {
    Map<Scalar, Long> map = Tally.of(tensor.stream() //
        .flatMap(Tensor::stream) //
        .map(Scalar.class::cast));
    boolean value = map.keySet().stream() //
        .allMatch(scalar -> IntegerQ.of(scalar) && Sign.isPositiveOrZero(scalar));
    boolean allMatch = map.values().stream() //
        .allMatch(tally -> 1 == tally);
    if (value && allMatch)
      return Tensor.of(tensor.stream() //
          .filter(cycle -> 1 < cycle.length())); //
    throw TensorRuntimeException.of(tensor);
  }

  /***************************************************/
  private final Map<Integer, Integer> map;

  private Cycles(Map<Integer, Integer> map) {
    this.map = map;
  }

  /** @return map without singletons, i.e. no trivial associations a -> a exist */
  public Map<Integer, Integer> map() {
    return Collections.unmodifiableMap(map);
  }

  private void cycleInterate(Consumer<Tensor> consumer) {
    Set<Integer> set = new HashSet<>();
    for (Entry<Integer, Integer> entry : map.entrySet()) {
      int seed = entry.getKey();
      int next = entry.getValue();
      if (!set.contains(seed)) {
        List<Integer> list = new ArrayList<>();
        while (set.add(seed)) {
          list.add(seed);
          next = map.get(seed = next);
        }
        Integers.requirePositive(list.size());
        consumer.accept(Tensors.vector(list));
      }
    }
  }

  /** Hint: InversePermutation in Mathematica
   * 
   * @return */
  public Cycles inverse() {
    return new Cycles(map.entrySet().stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey)));
  }

  /** Hint: PermutationProduct in Mathematica
   * 
   * @param cycles
   * @return */
  public Cycles combine(Cycles cycles) {
    Map<Integer, Integer> b_map = cycles.map;
    Map<Integer, Integer> result = new HashMap<>();
    Set<Integer> set = new HashSet<>();
    for (Entry<Integer, Integer> entry : map.entrySet()) {
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

  private static final BinaryPower<Cycles> BINARY_POWER = new BinaryPower<>(CyclesGroup.INSTANCE);

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
    Tensor cycles = Tensors.empty();
    cycleInterate(cycle -> BINARY_POWER.raise(new Cycles(map(Tensors.of(cycle))), //
        bigInteger.mod(BigInteger.valueOf(cycle.length()))).cycleInterate(cycles::append));
    return new Cycles(map(cycles)); // most efficient?
  }

  private static Tensor minFirst(Tensor vector) {
    return RotateLeft.of(vector, ArgMin.of(vector));
  }

  private static final Comparator<Tensor> COMPARATOR = //
      (cycle1, cycle2) -> Scalars.compare(cycle1.Get(0), cycle2.Get(0));

  /** @return for instance {{1, 20}, {4, 10, 19, 6, 18}, {5, 9}, {7, 14, 13}} */
  public Tensor toTensor() {
    Tensor cycles = Tensors.empty();
    cycleInterate(cycles::append);
    return Tensor.of(cycles.stream().map(Cycles::minFirst).sorted(COMPARATOR));
  }

  /***************************************************/
  @Override // from Object
  public boolean equals(Object object) {
    if (object instanceof Cycles) {
      Cycles cycles = (Cycles) object;
      return map.equals(cycles.map);
    }
    return false;
  }

  @Override // from Object
  public int hashCode() {
    return map.hashCode();
  }

  @Override // from Object
  public String toString() {
    return toTensor().toString();
  }
}
