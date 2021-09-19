// code by Eric Simonton
// adapted by jph and clruch
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
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
   * for instance if the points to be added are in the unit cube then
   * <pre>
   * lbounds = {0, 0, 0}
   * ubounds = {1, 1, 1}
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
    StaticHelper.require(lbounds, ubounds);
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
  public void visit(NdVisitor<V> ndVisitor) {
    root.visit(ndVisitor, new NdBounds(global_lBounds, global_uBounds));
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
  // TODO this should be obsolete when using visitor
  @Deprecated
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

    @Deprecated
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
        queue.clear();
        queue = null;
        add(ndPair, ndBounds);
      }
    }

    private void visit(NdVisitor<V> ndVisitor, NdBounds ndBounds) {
      if (isInternal()) {
        final int dimension = dimension();
        Scalar mean = ndBounds.mean(dimension);
        boolean lFirst = ndVisitor.leftFirst(ndBounds, dimension, mean);
        visit(ndVisitor, ndBounds, mean, lFirst);
        visit(ndVisitor, ndBounds, mean, !lFirst);
      } else
        queue.forEach(ndVisitor::consider); // number of function calls to #consider
    }

    private void visit(NdVisitor<V> ndVisitor, NdBounds ndBounds, Scalar median, boolean left) {
      final int dimension = dimension();
      if (left) {
        if (Objects.isNull(lChild))
          return;
        Scalar copy = ndBounds.uBounds.Get(dimension);
        ndBounds.uBounds.set(median, dimension);
        if (ndVisitor.isViable(ndBounds))
          lChild.visit(ndVisitor, ndBounds);
        ndBounds.uBounds.set(copy, dimension);
      } else {
        if (Objects.isNull(rChild))
          return;
        Scalar copy = ndBounds.lBounds.Get(dimension);
        ndBounds.lBounds.set(median, dimension);
        if (ndVisitor.isViable(ndBounds))
          rChild.visit(ndVisitor, ndBounds);
        ndBounds.lBounds.set(copy, dimension);
      }
    }
  }

  /***************************************************/
  @Override // from Object
  public String toString() {
    NdPrint<V> ndPrint = new NdPrint<>();
    visit(ndPrint);
    return ndPrint.toString();
  }
}
