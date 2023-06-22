// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;

/** Density-based spatial clustering of applications with noise
 * 
 * References:
 * "A density-based algorithm for discovering clusters in large spatial
 * databases with noise", by Ester, Kriegel, Sander, Xu, 1996
 * 
 * <a href="https://en.wikipedia.org/wiki/DBSCAN">Wikipedia</a>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/method/DBSCAN.html">DBSCAN</a> */
public enum Dbscan {
  ;
  public static final int NOISE = -1;

  /** @param points
   * @param function for instance NdCenters.VECTOR_2_NORM
   * @param radius non-negative
   * @param minPts positive
   * @return array of labels for each point, or Dbscan.NOISE == -1 to indicate that respective point is noise */
  public static Integer[] of(Tensor points, Function<Tensor, NdCenterInterface> function, Scalar radius, int minPts) {
    Integers.requirePositive(minPts);
    NdMap<Integer> ndMap = NdTreeMap.of(CoordinateBounds.of(points));
    AtomicInteger atomicInteger = new AtomicInteger();
    points.forEach(point -> ndMap.insert(point, atomicInteger.getAndIncrement()));
    // ---
    Integer[] labels = new Integer[points.length()];
    int index = 0;
    int cluster = -1;
    for (Tensor p : points) {
      if (Objects.isNull(labels[index])) { // else "Previously processed in inner loop"
        Collection<NdMatch<Integer>> c_p = NdCollectRadius.of(ndMap, function.apply(p), radius);
        if (c_p.size() < minPts) // "Density check"
          labels[index] = NOISE; // "Label as Noise"
        else {
          ++cluster; // "next cluster label"
          labels[index] = cluster; // "Label initial point"
          Set<Integer> set = c_p.stream().map(NdMatch::value).collect(Collectors.toSet());
          set.remove(index);
          while (!set.isEmpty()) {
            int index_q = set.iterator().next(); // "Process every seed point Q"
            set.remove(index_q);
            Integer label_q = labels[index_q];
            if (Objects.nonNull(label_q) && label_q.equals(NOISE))
              labels[index_q] = cluster; // "Change Noise to border point"
            if (Objects.isNull(labels[index_q])) { // else "Previously processed (e.g., border point)"
              labels[index_q] = cluster; // "Label neighbor"
              Tensor q = points.get(index_q);
              Collection<NdMatch<Integer>> c_q = NdCollectRadius.of(ndMap, function.apply(q), radius);
              if (minPts <= c_q.size()) // "Density check (if Q is a core point)"
                c_q.stream().map(NdMatch::value).forEach(set::add); // "Add new neighbors to seed set"
            }
          }
        }
      }
      ++index;
    }
    return labels;
  }
}
