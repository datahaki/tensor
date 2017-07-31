// code by jph
package ch.ethz.idsc.tensor;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.alg.Dimensions;

/** A {@link Tensor} is a scalar, or a list of tensors.
 * 
 * <p>In particular, a {@link Tensor} does not have to be an array.
 * Example structure: <code>{0, {1, 2}, 3, {{4, 5}, 6}}</code>
 * 
 * <p>This generality allows to combine tensors
 * <pre>
 * Tensor state = {x, y, theta}
 * Tensor action = {steer, speed}
 * </pre>
 * into one tensor
 * <pre>
 * Tensor stateAction = {state, action}
 * </pre>
 * 
 * <p>If a {@link Tensor} is a multi-dimensional array, then the dot product is supported.
 * Example of a tensor with regular array structure:
 * <code>{{1, 2, 3}, {4, 5, 6}}</code> */
public interface Tensor extends Iterable<Tensor>, Serializable {
  /** constant ALL is used in the function {@link Tensor#get(Integer...)}
   * to extract <em>all</em> elements from the respective dimension.
   * 
   * The value of ALL is deliberately not chosen to equal -1, since an index of -1
   * could likely be the result of a mistake in the application layer.
   * 
   * Constant ALL <em>cannot</em> be used in {@link Tensor#set(Tensor, Integer...)} */
  static final int ALL = 0xA110CA7E;
  /** curly opening bracket of vector */
  static final char OPENING_BRACKET = '{';
  /** curly closing bracket of vector */
  static final char CLOSING_BRACKET = '}';

  /** constructs a tensor that holds the tensors of the input stream.
   * 
   * <p>for instance,
   * <ul>
   * <li>if the stream consists of {@link Scalar}s, the return value represents a vector,
   * <li>if the stream consists of vectors, the return value represents a matrix.
   * <li>if the stream consists of matrices, the return value represents a tensor with rank 3.
   * <li>etc.
   * </ul>
   * 
   * @param stream of tensors to form the first level of the return value
   * @return tensor that holds the tensors of the input stream */
  static Tensor of(Stream<? extends Tensor> stream) {
    return new TensorImpl(stream.map(Tensor.class::cast).collect(Collectors.toList()));
  }

  /** The operation doesn't duplicate data, but wraps the data container
   * with Collections::unmodifiableList and overrides setters.
   * <ul>
   * <li>modification is still possible via references to the entries
   * <li>This tensor remains modifiable
   * </ul>
   * 
   * @return new immutable instance of this tensor */
  Tensor unmodifiable();

  /** duplicate mutable content of this tensor into new instance
   * 
   * @return clone of this */
  Tensor copy();

  /** non-negative index[...] refer to the position in the tensor
   * 
   * Special value:
   * <code>index[dim] == Tensor.ALL</code> refers to all entries of tensor dimension dim
   * 
   * @param index
   * @return copy of this[index[0], index[1], ..., All] */
  Tensor get(Integer... index);

  /** same as function get(...) except that return type is cast to {@link Scalar}.
   * An exception is thrown, if there is no Scalar at the specified index location.
   * 
   * @param index
   * @return {@link Scalar} at this[index[0],index[1],...] */
  Scalar Get(Integer... index);

  /** @param index
   * @return copy of this[index[0],index[1],...,All] */
  Tensor get(List<Integer> index);

  /** set tensor as element at location this[index[0], index[1], ...].
   * The operation is invalid if this tensor has been cast as unmodifiable.
   * 
   * <p>For instance,
   * <ul>
   * <li><code>matrix.set(scalar, 3, 4)</code> represents the assignment <code>matrix[3, 4]=scalar</code>
   * <li><code>matrix.set(row, 6)</code> represents the assignment <code>matrix[6, :]=row</code>
   * </ul>
   * 
   * @param tensor
   * @param index
   * @throws Exception if set() is invoked on an instance of {@link Scalar} */
  void set(Tensor tensor, Integer... index);

  /** replaces element x at index with <code>function.apply(x)</code>
   * The operation is invalid if this tensor has been cast as unmodifiable.
   * 
   * <p>set(...) allows to implement in-place operations such as <code>a += 3;</code>
   * 
   * <p>the operation may change the structure/dimensions/rank of the tensor.
   * 
   * @param function
   * @param index
   * @throws Exception if set() is invoked on an instance of {@link Scalar}
   * @see Tensor#set(Tensor, Integer...) */
  <T extends Tensor> void set(Function<T, ? extends Tensor> function, Integer... index);

  /** appends a copy of input tensor to this instance.
   * The length() is incremented by 1.
   * 
   * <p>append(...) can be used to append to a sub-tensor of this instance via
   * {@link Tensor#set(Function, Integer...)}.
   * For example:
   * <pre>matrix.set(entry -> entry.append(tensor), index);</pre>
   * 
   * <p>the operation does not succeed for an unmodifiable instance of this.
   * An exception is thrown when append is invoked on a {@link Scalar}.
   * 
   * @param tensor to be appended to this
   * @return this */
  Tensor append(Tensor tensor);

  /** function is <em>not</em> Mathematica compliant:
   * <code>Length[3.14] == -1</code>
   * (Mathematica evaluates <code>Length[scalar] == 0</code>).
   * We deviate from this to avoid the ambiguity with length of an empty list:
   * <code>Length[{}] == 0</code>
   * 
   * <p>In order to check if a tensor is an empty vector use <code>tensor.length() == 0</code>.
   * 
   * @return number of entries on the first level; -1 for {@link Scalar}s */
  int length();

  /** <p>function is equivalent to the checks
   * <ul>
   * <li><code>length() == Scalar.LENGTH</code>
   * <li><code>this instanceof Scalar</code>
   * </ul>
   * 
   * @return true if this instanceof {@link Scalar} */
  boolean isScalar();

  /** stream access to the entries at given level of this tensor.
   * entries at given level can be tensors or scalars.
   * 
   * For the input <code>level == -1</code>, the return stream consists
   * of all {@link Scalar}s in this tensor.
   * 
   * If this tensor has been marked as unmodifiable, the elements of
   * the stream are unmodifiable as well.
   * 
   * @param level
   * @return non-parallel stream, the user may invoke .parallel() */
  Stream<Tensor> flatten(int level);

  /** @param fromIndex
   * @param toIndex
   * @return copy of sub tensor fromIndex inclusive to toIndex exclusive */
  Tensor extract(int fromIndex, int toIndex);

  /** extract block of this tensor located at offset with dimensions
   * 
   * @param fromIndex location of return tensor in this tensor
   * @param dimensions of return tensor
   * @return copy of block located at fromIndex of this tensor with given dimensions */
  Tensor block(List<Integer> fromIndex, List<Integer> dimensions);

  /** negation of entries
   * 
   * @return tensor with all entries negated */
  Tensor negate();

  /** tensor addition
   * 
   * addition is commutative: <code>a.add(b) equals b.add(a)</code>
   * 
   * @param tensor
   * @return this plus input tensor */
  Tensor add(Tensor tensor);

  /** tensor subtraction. Equivalent to <code>add(tensor.negate())</code>
   * 
   * @param tensor
   * @return this minus input tensor */
  Tensor subtract(Tensor tensor);

  /** point-wise multiplication of this with tensor.
   * 
   * <p>{@link Dimensions} of <em>this</em> have to match the <em>onset</em> of dimensions of tensor.
   * Tensor::multiply is used on remaining entries in dimensions of tensors exceeding
   * dimensions of this.
   * 
   * <p>For instance,
   * <ul>
   * <li><code>Dimensions.of(this) = [4, 3]</code>, and
   * <li><code>Dimensions.of(tensor) = [4, 3, 5, 2]</code> is feasible.
   * </ul>
   * 
   * <p>pmul is consistent with Mathematica, for instance
   * <pre>
   * {a, b} {{1, 2, 3}, {4, 5, 6}} == {{a, 2 a, 3 a}, {4 b, 5 b, 6 b}}
   * Dimensions[Array[1 &, {2, 3}] Array[1 &, {2, 3, 4}]] == {2, 3, 4}
   * </pre>
   * 
   * @param tensor
   * @return this element-wise multiply input tensor. */
  Tensor pmul(Tensor tensor);

  /** scalar multiplication, i.e. scaling, of tensor entries
   * 
   * @param scalar
   * @return tensor with elements of this tensor multiplied with scalar */
  Tensor multiply(Scalar scalar);

  /** dot product as in Mathematica
   * 
   * <p>The {@link Dimensions} of the dotted tensors reduce according to the pattern
   * <code>[n1,n2,n3,n4,n5] . [n5,n6,...,n9] == [n1,n2,n3,n4,n6,...,n9]</code>
   * 
   * @param tensor
   * @return dot product between this and input tensor */
  Tensor dot(Tensor tensor);

  /** applies function to all entries
   * 
   * @param function
   * @return new tensor with {@link Scalar} entries replaced by
   * function evaluation of {@link Scalar} entries */
  Tensor map(Function<Scalar, ? extends Tensor> function);

  /** if this tensor is unmodifiable, references to entries are also unmodifiable.
   * 
   * @return references to entries in this tensor */
  @Override // from Iterable<Tensor>
  Iterator<Tensor> iterator();
}