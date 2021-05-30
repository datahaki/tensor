// code by Eric Simonton
// adapted by jph and clruch
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Integers;

/** the query {@link NdTreeMap#cluster(NdCenterInterface, int)}
 * can be used in parallel. */
public class NdTreeMap<V> implements NdMap<V>, Serializable {
  private final Tensor global_lBounds;
  private final Tensor global_uBounds;
  private final int maxDensity;
  // ---
  private final Node root;
  private int size;

  /** lbounds and ubounds are vectors of identical length
   * for instance if the points to be added are in the unit square then
   * <pre>
   * lbounds = {0, 0}
   * ubounds = {1, 1}
   * </pre>
   * 
   * @param lbounds smallest coordinates of points to be added
   * @param ubounds greatest coordinates of points to be added
   * @param maxDensity non-negative is the maximum queue size of leaf nodes, except
   * for leaf nodes with maxDepth, which have unlimited queue size. The special case
   * maxDensity == 0 implies that values will only be stored at nodes of max depth
   * @param maxDepth 16 is reasonable for most applications
   * @throws Exception if maxDensity is not strictly positive */
  public NdTreeMap(Tensor lbounds, Tensor ubounds, int maxDensity, int maxDepth) {
    VectorQ.require(lbounds);
    VectorQ.require(ubounds);
    if (lbounds.length() != ubounds.length())
      throw TensorRuntimeException.of(lbounds, ubounds);
    if (!IntStream.range(0, lbounds.length()).allMatch(index -> Scalars.lessEquals(lbounds.Get(index), ubounds.Get(index))))
      throw TensorRuntimeException.of(lbounds, ubounds);
    global_lBounds = lbounds.unmodifiable();
    global_uBounds = ubounds.unmodifiable();
    this.maxDensity = Integers.requirePositiveOrZero(maxDensity);
    root = new Node(Integers.requirePositive(maxDepth));
  }

  /** @param location vector with same length as lbounds and ubounds
   * @param value
   * @throws Exception if given location is not a vector of required length */
  @Override // from NdMap
  public void add(Tensor location, V value) {
    add(new NdPair<>(VectorQ.requireLength(location, global_lBounds.length()), value));
  }

  private void add(NdPair<V> ndPair) {
    root.add(ndPair, new NdBounds(global_lBounds, global_uBounds));
    ++size;
  }

  @Override // from NdMap
  public int size() {
    return size;
  }

  @Override // from NdMap
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override // from NdMap
  public Collection<NdMatch<V>> cluster(NdCenterInterface ndCenterInterface, int limit) {
    return cluster(new BoundedNdCluster<>(ndCenterInterface, limit));
  }

  @Override // from NdMap
  public Collection<NdMatch<V>> cluster(NdCluster<V> ndCluster) {
    root.addToCluster(ndCluster, new NdBounds(global_lBounds, global_uBounds));
    return ndCluster.collection();
  }

  /** function returns the queue size of leaf nodes in the tree.
   * use the function to determine if the tree is well balanced.
   * 
   * <p>Example use:
   * <pre>
   * Tally.sorted(Flatten.of(ndTreeMap.binSize()))
   * </pre>
   * 
   * @return */
  public Tensor binSize() {
    return root.binSize();
  }

  private class Node implements Serializable {
    private final int depth;
    private Node lChild;
    private Node rChild;
    /** queue is set to null when node transform from leaf node to interior node */
    private Queue<NdPair<V>> queue = new ArrayDeque<>();

    private Node(int depth) {
      // check is for validation of implementation
      this.depth = Integers.requirePositive(depth);
    }

    private Node createChild() {
      return new Node(depth - 1);
    }

    private boolean isInternal() {
      return Objects.isNull(queue);
    }

    private Tensor binSize() {
      return isInternal() //
          ? Tensors.of( //
              Objects.isNull(lChild) ? RealScalar.ZERO : lChild.binSize(), //
              Objects.isNull(rChild) ? RealScalar.ZERO : rChild.binSize())
          : RealScalar.of(queue.size());
    }

    private int dimension() {
      return depth % global_lBounds.length();
    }

    private void add(final NdPair<V> ndPair, NdBounds ndBounds) {
      if (isInternal()) {
        Tensor location = ndPair.location();
        int dimension = dimension();
        Scalar mean = ndBounds.mean(dimension);
        if (Scalars.lessThan(location.Get(dimension), mean)) {
          ndBounds.uBounds.set(mean, dimension);
          if (Objects.isNull(lChild))
            lChild = createChild();
          lChild.add(ndPair, ndBounds);
          return;
        }
        ndBounds.lBounds.set(mean, dimension);
        if (Objects.isNull(rChild))
          rChild = createChild();
        rChild.add(ndPair, ndBounds);
      } else //
      if (queue.size() < maxDensity)
        queue.add(ndPair);
      else //
      if (depth == 1)
        queue.add(ndPair);
      // the original code removed a node from the queue: return queue.poll();
      // in our opinion this behavior is undesired.
      // at the lowest depth we grow the queue indefinitely, instead.
      else {
        int dimension = dimension();
        Scalar mean = ndBounds.mean(dimension);
        for (NdPair<V> entry : queue)
          if (Scalars.lessThan(entry.location().Get(dimension), mean)) {
            if (Objects.isNull(lChild))
              lChild = createChild();
            lChild.queue.add(entry);
          } else {
            if (Objects.isNull(rChild))
              rChild = createChild();
            rChild.queue.add(entry);
          }
        queue = null;
        add(ndPair, ndBounds);
      }
    }

    private void addToCluster(NdCluster<V> ndCluster, NdBounds ndBounds) {
      if (isInternal()) {
        final int dimension = dimension();
        Scalar mean = ndBounds.mean(dimension);
        boolean lFirst = Scalars.lessThan(ndCluster.center_Get(dimension), mean);
        addChildToCluster(ndCluster, ndBounds, mean, lFirst);
        addChildToCluster(ndCluster, ndBounds, mean, !lFirst);
      } else
        queue.forEach(ndCluster::consider); // number of function calls to #consider
    }

    private void addChildToCluster(NdCluster<V> ndCluster, NdBounds ndBounds, Scalar median, boolean left) {
      final int dimension = dimension();
      if (left) {
        if (Objects.isNull(lChild))
          return;
        Scalar copy = ndBounds.uBounds.Get(dimension);
        ndBounds.uBounds.set(median, dimension);
        if (ndCluster.isViable(ndBounds))
          lChild.addToCluster(ndCluster, ndBounds);
        ndBounds.uBounds.set(copy, dimension);
      } else {
        if (Objects.isNull(rChild))
          return;
        Scalar copy = ndBounds.lBounds.Get(dimension);
        ndBounds.lBounds.set(median, dimension);
        if (ndCluster.isViable(ndBounds))
          rChild.addToCluster(ndCluster, ndBounds);
        ndBounds.lBounds.set(copy, dimension);
      }
    }
  }

  /***************************************************/
  @Override // from Object
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    print(stringBuilder, root);
    return stringBuilder.toString();
  }

  private void print(StringBuilder stringBuilder, Node node) {
    String v = " ".repeat(root.depth - node.depth);
    if (Objects.isNull(node.queue)) {
      stringBuilder.append(v + "<empty>\n");
      if (Objects.nonNull(node.lChild))
        print(stringBuilder, node.lChild);
      if (Objects.nonNull(node.rChild))
        print(stringBuilder, node.rChild);
    } else {
      stringBuilder.append(v + Integer.toString(node.queue.size()) + "\n");
      for (NdPair<V> entry : node.queue)
        stringBuilder.append(v + entry.location().toString() + " " + entry.value() + "\n");
    }
  }
}
