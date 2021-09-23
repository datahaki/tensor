// code by Eric Simonton
// adapted by jph and clruch
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Integers;

/** the query {@link NdTreeMap#cluster(NdCenterInterface, int)}
 * can be used in parallel. */
public class NdTreeMap<V> implements NdMap<V>, Serializable {
  private static final int LEAF_SIZE_DEFAULT = 8;
  /** parameter only relevant if more than maxDensity identical key locations
   * 2^20 == 1048576 */
  private static final int MAX_DEPTH = 20;

  /** @param lbounds vector
   * @param ubounds vector
   * @param leafSizeMax strictly positive
   * @return */
  public static <V> NdMap<V> of(Tensor lbounds, Tensor ubounds, int leafSizeMax) {
    return new NdTreeMap<>(lbounds, ubounds, leafSizeMax);
  }

  /** @param lbounds
   * @param ubounds
   * @return */
  public static <V> NdMap<V> of(Tensor lbounds, Tensor ubounds) {
    return of(lbounds, ubounds, LEAF_SIZE_DEFAULT);
  }

  // ==================================================
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
   * @throws Exception if maxDensity is strictly positive */
  private NdTreeMap(Tensor lbounds, Tensor ubounds, int maxDensity) {
    StaticHelper.require(lbounds, ubounds);
    global_lBounds = lbounds.unmodifiable();
    global_uBounds = ubounds.unmodifiable();
    this.maxDensity = Integers.requirePositive(maxDensity);
    root = new Node(0);
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

  private class Node implements Serializable {
    private final int depth;
    private Node lChild;
    private Node rChild;
    /** queue is set to null when node transform from leaf node to interior node */
    private Queue<NdPair<V>> queue = new ArrayDeque<>();

    private Node(int depth) {
      this.depth = depth;
    }

    private Node createChild() {
      return new Node(depth + 1);
    }

    /** @return whether node is interior node */
    private boolean isInterior() {
      return Objects.isNull(queue);
    }

    private int dimension() {
      return depth % global_lBounds.length();
    }

    private void add(NdPair<V> ndPair, NdBounds ndBounds) {
      if (isInterior()) {
        Tensor location = ndPair.location();
        int dimension = dimension();
        Scalar median = ndBounds.median(dimension);
        if (Scalars.lessThan(location.Get(dimension), median)) {
          ndBounds.uBounds.set(median, dimension);
          if (Objects.isNull(lChild)) {
            lChild = createChild();
            lChild.queue.add(ndPair);
          } else
            lChild.add(ndPair, ndBounds);
        } else {
          ndBounds.lBounds.set(median, dimension);
          if (Objects.isNull(rChild)) {
            rChild = createChild();
            rChild.queue.add(ndPair);
          } else
            rChild.add(ndPair, ndBounds);
        }
      } else { // queue != null
        if (queue.size() < maxDensity || depth == MAX_DEPTH)
          queue.add(ndPair);
        else { // split queue into left and right
          int dimension = dimension();
          Scalar median = ndBounds.median(dimension);
          for (NdPair<V> entry : queue)
            if (Scalars.lessThan(entry.location().Get(dimension), median)) {
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
    }

    private void visit(NdVisitor<V> ndVisitor, NdBounds ndBounds) {
      if (isInterior()) {
        int dimension = dimension();
        Scalar median = ndBounds.median(dimension);
        boolean leftFirst = ndVisitor.push_leftFirst(dimension, median);
        if (leftFirst) {
          visit_L(ndVisitor, ndBounds, median);
          visit_R(ndVisitor, ndBounds, median);
        } else {
          visit_R(ndVisitor, ndBounds, median);
          visit_L(ndVisitor, ndBounds, median);
        }
        ndVisitor.pop();
      } else
        queue.forEach(ndVisitor::consider); // number of function calls to #consider
    }

    private void visit_L(NdVisitor<V> ndVisitor, NdBounds ndBounds, Scalar median) {
      if (Objects.nonNull(lChild)) {
        int dimension = dimension();
        Scalar copy = ndBounds.uBounds.Get(dimension);
        ndBounds.uBounds.set(median, dimension);
        if (ndVisitor.isViable(ndBounds))
          lChild.visit(ndVisitor, ndBounds);
        ndBounds.uBounds.set(copy, dimension);
      }
    }

    private void visit_R(NdVisitor<V> ndVisitor, NdBounds ndBounds, Scalar median) {
      if (Objects.nonNull(rChild)) {
        int dimension = dimension();
        Scalar copy = ndBounds.lBounds.Get(dimension);
        ndBounds.lBounds.set(median, dimension);
        if (ndVisitor.isViable(ndBounds))
          rChild.visit(ndVisitor, ndBounds);
        ndBounds.lBounds.set(copy, dimension);
      }
    }
  }

  @Override // from Object
  public String toString() {
    NdStringBuilder<V> ndStringBuilder = new NdStringBuilder<>();
    visit(ndStringBuilder);
    return ndStringBuilder.toString();
  }
}
