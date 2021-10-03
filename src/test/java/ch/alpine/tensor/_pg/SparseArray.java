// code by jph
package ch.alpine.tensor._pg;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** API EXPERIMENTAL */
/* package */ class SparseArray implements Tensor {
  @Override
  public Tensor unmodifiable() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor copy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor get(int i) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor get(int... index) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor get(List<Integer> index) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Scalar Get(int i) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Scalar Get(int i, int j) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void set(Tensor tensor, int... index) {
    // TODO Auto-generated method stub
  }

  @Override
  public void set(Tensor tensor, List<Integer> index) {
    // TODO Auto-generated method stub
  }

  @Override
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, int... index) {
    // TODO Auto-generated method stub
  }

  @Override
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, List<Integer> index) {
    // TODO Auto-generated method stub
  }

  @Override
  public Tensor append(Tensor tensor) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int length() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Tensor extract(int fromIndex, int toIndex) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor negate() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor add(Tensor tensor) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor subtract(Tensor tensor) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor pmul(Tensor tensor) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor multiply(Scalar scalar) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor divide(Scalar scalar) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor dot(Tensor tensor) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor map(Function<Scalar, ? extends Tensor> function) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor block(List<Integer> fromIndex, List<Integer> dimensions) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Stream<Tensor> stream() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Stream<Tensor> flatten(int level) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterator<Tensor> iterator() {
    // TODO Auto-generated method stub
    return null;
  }
}
