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
import ch.alpine.tensor.ext.Integers;

/** the permitted region for points to be added to a {@link NdTreeMap}
 * is the axis align bounding box provided at time of instantiation.
 * 
 * {@link NdTreeMap#visit(NdVisitor)} can be used in parallel. */
public class NdTreeMap<V> implements NdMap<V>, Serializable {
  private static final int LEAF_SIZE_DEFAULT = 8;
  /** parameter only relevant if more than maxDensity identical key locations
   * 2^20 == 1048576 */
  private static final int MAX_DEPTH = 20;

  /** @param ndBox axis aligned bounding box that contains the points to be added
   * @param leafSizeMax non-negative is the maximum queue size of leaf nodes, except
   * for leaf nodes with maxDepth, which have unlimited queue size. The special case
   * maxDensity == 0 implies that values will only be stored at nodes of max depth */
  public static <V> NdMap<V> of(NdBox ndBox, int leafSizeMax) {
    return new NdTreeMap<>(ndBox, leafSizeMax);
  }

  /** @param ndBox axis aligned bounding box that contains the points to be added
   * @return */
  public static <V> NdMap<V> of(NdBox ndBox) {
    return of(ndBox, LEAF_SIZE_DEFAULT);
  }

  // ---
  private final NdBox ndBoxGlobal;
  private final int maxDensity;
  private final Node root;
  private int size;

  private NdTreeMap(NdBox ndBox, int maxDensity) {
    this.ndBoxGlobal = Objects.requireNonNull(ndBox);
    this.maxDensity = Integers.requirePositive(maxDensity);
    root = new Node(0);
  }

  @Override // from NdMap
  public void insert(Tensor location, V value) {
    root.add(new NdEntry<>(ndBoxGlobal.requireInside(location), value), ndBoxGlobal);
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
    root.visit(ndVisitor, ndBoxGlobal);
  }

  @Override // from Object
  public String toString() {
    NdStringBuilder<V> ndStringBuilder = new NdStringBuilder<>();
    visit(ndStringBuilder);
    return ndStringBuilder.toString();
  }

  private class Node implements Serializable {
    private final int depth;
    private Node lChild;
    private Node rChild;
    /** queue is set to null when node transform from leaf node to interior node */
    private Queue<NdEntry<V>> queue = new ArrayDeque<>();

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
      return depth % ndBoxGlobal.dimensions();
    }

    private void add(NdEntry<V> ndEntry, NdBox ndBox) {
      if (isInterior()) {
        Tensor location = ndEntry.location();
        int dimension = dimension();
        if (Scalars.lessThan(location.Get(dimension), ndBox.median(dimension))) {
          if (Objects.isNull(lChild)) {
            lChild = createChild();
            lChild.queue.add(ndEntry);
          } else
            lChild.add(ndEntry, ndBox.splitLo(dimension));
        } else {
          if (Objects.isNull(rChild)) {
            rChild = createChild();
            rChild.queue.add(ndEntry);
          } else
            rChild.add(ndEntry, ndBox.splitHi(dimension));
        }
      } else { // queue != null
        if (queue.size() < maxDensity || depth == MAX_DEPTH)
          queue.add(ndEntry);
        else { // split queue into left and right
          int dimension = dimension();
          for (NdEntry<V> entry : queue)
            if (Scalars.lessThan(entry.location().Get(dimension), ndBox.median(dimension))) {
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
          add(ndEntry, ndBox);
        }
      }
    }

    private void visit(NdVisitor<V> ndVisitor, NdBox ndBox) {
      if (isInterior()) {
        int dimension = dimension();
        Scalar median = ndBox.median(dimension);
        boolean leftFirst = ndVisitor.push_firstLo(dimension, median);
        if (leftFirst) {
          visitLo(ndVisitor, ndBox);
          visitHi(ndVisitor, ndBox);
        } else {
          visitHi(ndVisitor, ndBox);
          visitLo(ndVisitor, ndBox);
        }
        ndVisitor.pop();
      } else
        queue.forEach(ndVisitor::consider);
    }

    private void visitLo(NdVisitor<V> ndVisitor, NdBox ndBox) {
      if (Objects.nonNull(lChild)) {
        NdBox splitLo = ndBox.splitLo(dimension());
        if (ndVisitor.isViable(splitLo))
          lChild.visit(ndVisitor, splitLo);
      }
    }

    private void visitHi(NdVisitor<V> ndVisitor, NdBox ndBox) {
      if (Objects.nonNull(rChild)) {
        NdBox splitHi = ndBox.splitHi(dimension());
        if (ndVisitor.isViable(splitHi))
          rChild.visit(ndVisitor, splitHi);
      }
    }
  }
}
