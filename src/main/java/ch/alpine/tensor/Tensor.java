// code by jph
package ch.alpine.tensor;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.fft.ListCorrelate;

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
 * <p>If a {@link Tensor} is a multidimensional array, then the dot product
 * {@link #dot(Tensor)} is supported.
 * 
 * <p>Example of a tensor with regular array structure:
 * <code>{{1, 2, 3}, {4, 5, 6}}</code> */
public interface Tensor extends Iterable<Tensor> {
  /** constant ALL is used in the function
   * <ul>
   * <li>{@link #get(int...)} to extract <em>all</em> elements from the respective dimension.
   * <li>{@link #set(Function, int...)} to reassign <em>all</em> elements from the respective dimension.
   * </ul>
   * 
   * <p>The value of ALL is deliberately <em>not</em> chosen to equal -1, since an index of -1
   * could likely be the result of a mistake in the application layer. */
  int ALL = 0xA110CA7E;
  /** curly opening bracket of vector */
  char OPENING_BRACKET = '{';
  /** curly closing bracket of vector */
  char CLOSING_BRACKET = '}';

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

  /** function returns a tensor with content equal to this tensor but with the
   * guarantee that its contents cannot be altered via member functions.
   * 
   * <p>If this tensor is already unmodifiable, the function simply returns this
   * instance. Therefore, the check whether a tensor is unmodifiable is simply
   * <pre>
   * tensor.unmodifiable() == tensor // equal by reference intended
   * </pre>
   * as implemented in {@link Tensors#isUnmodifiable(Tensor)}.
   * 
   * <p>The function is idemponent in regard to equality by reference, i.e.
   * <pre>
   * tensor.unmodifiable() == tensor.unmodifiable().unmodifiable() == ...
   * </pre>
   * 
   * <p>The operation never duplicates any content, but wraps the data container
   * for instance with Collections::unmodifiableList and overrides setters.
   * 
   * <p>Remarks:
   * <ul>
   * <li>This tensor remains modifiable
   * <li>modification is still possible via references to the original entries
   * </ul>
   * 
   * @return tensor with content equal to this tensor but with the guarantee
   * that its contents cannot be altered via member functions
   * @see Tensors#isUnmodifiable(Tensor) */
  Tensor unmodifiable();

  /** duplicate mutable content of this tensor into new instance.
   * Modifications to the copy do not affect the original instance.
   * A copy of an unmodifiable tensor is modifiable
   * 
   * <p>Remark: the call tensor.get(), i.e. tensor.get(List.of()) with
   * an empty index list also returns a complete copy() of the tensor instance.
   * 
   * @return clone of this */
  Tensor copy();

  /** Remark: the parameter i == Tensor.ALL is <b>not</b> permitted, because
   * get(ALL) should be implemented as copy(). Therefore, the function is <b>not</b>
   * a special case of the more general function {@link #get(int...)}.
   * 
   * @param i in the range 0 and length() - 1
   * @return copy of this[i], i.e. the i-th element of this tensor */
  Tensor get(int i);

  /** non-negative index[...] refer to the position in the tensor
   * 
   * <p>Special value:
   * <code>index[dim] == Tensor.ALL</code> refers to all entries of tensor dimension dim
   * 
   * @param index
   * @return copy of this[index[0], index[1], ..., All] */
  Tensor get(int... index);

  /** @param index
   * @return copy of this[index[0], index[1], ..., All]
   * @see #get(int...) */
  Tensor get(List<Integer> index);

  /** function is identical to
   * <pre>
   * Get(i) == (Scalar) get(i)
   * </pre>
   * 
   * Consequently, an exception is thrown, if there is no Scalar at the specified entry.
   * The function facilitates the extraction of scalars entries from vectors.
   * 
   * @param i in the range 0, 1, ..., length() - 1
   * @return (Scalar) get(i)
   * @throws Exception when the parameter is out of range, in particular, when this
   * instance is a {@link Scalar}. */
  Scalar Get(int i);

  /** function is identical to
   * <pre>
   * Get(i, j) == (Scalar) get(i, j)
   * </pre>
   * 
   * Consequently, an exception is thrown, if there is no Scalar at the specified entry.
   * 
   * Remark: The function was introduced to facilitate the extraction of entries
   * from matrices.
   * 
   * @param i in the range 0, 1, ..., length() - 1
   * @param j in the range 0, 1, ..., get(i).length() - 1
   * @return (Scalar) get(i, j)
   * @throws Exception when either parameter is out of range, in particular, when this
   * instance is a {@link Scalar}, or a vector. */
  Scalar Get(int i, int j);

  /** set copy of given tensor as element at location this[index[0], index[1], ...].
   * The operation is invalid if this tensor has been cast as unmodifiable.
   * 
   * <p>Tensor.ALL in the index array refers to all elements along that dimension.
   * 
   * <p>For instance,
   * <ul>
   * <li><code>matrix.set(scalar, 3, 4)</code> represents the assignment <code>matrix[3, 4]=scalar</code>
   * <li><code>matrix.set(row, 6)</code> represents the assignment <code>matrix[6, :]=row</code>
   * <li><code>matrix.set(col, Tensor.ALL, 5)</code> represents the assignment <code>matrix[:, 5]=col</code>
   * </ul>
   * 
   * @param tensor of which a copy replaces the existing element(s) at given index of this instance
   * @param index non-empty
   * @throws Exception if set() is invoked on an instance of {@link Scalar}, or index is empty
   * @throws Exception if this instance is unmodifiable */
  void set(Tensor tensor, int... index);

  /** set copy of given tensor as element at location this[index[0], index[1], ...].
   * 
   * @param tensor of which a copy replaces the existing element(s) at given index of this instance
   * @param index non-empty list
   * @throws Exception if index is empty
   * @throws Exception if this instance is unmodifiable
   * @see #set(Tensor, int...) */
  void set(Tensor tensor, List<Integer> index);

  /** replaces element x at index with <code>function.apply(x)</code>
   * The operation is invalid if this tensor has been cast as unmodifiable.
   * 
   * <p>Tensor.ALL in the index array refers to all elements along that dimension.
   * 
   * <p>set(...) allows to implement in-place operations such as <code>a += 3;</code>
   * 
   * <p>the operation may change the structure/dimensions/rank of the tensor. The example
   * below replaces the i-th row of given matrix with the sum of the vector entries.
   * <pre>
   * matrix.set(Total::ofVector, i);
   * </pre>
   * 
   * @param function
   * @param index non-empty
   * @throws Exception if set() is invoked on an instance of {@link Scalar}, or index is empty
   * @see #set(Tensor, int...) */
  <T extends Tensor> void set(Function<T, ? extends Tensor> function, int... index);

  /** @param function
   * @param index
   * @see #set(Function, int...) */
  <T extends Tensor> void set(Function<T, ? extends Tensor> function, List<Integer> index);

  /** appends a copy of input tensor to this instance
   * 
   * <p>The length() is incremented by 1.
   * 
   * <p>{@link #append(Tensor)} can be used to append to a sub-tensor of this instance
   * via {@link #set(Function, int...)}.
   * For example:
   * <pre>matrix.set(row -> row.append(tensor), index);</pre>
   * 
   * <p>the operation does not succeed for an unmodifiable instance of this.
   * An exception is thrown when append is invoked on a {@link Scalar}.
   * 
   * @param tensor to be appended to this
   * @return this
   * @throws Exception if given tensor is null
   * @throws Exception if this tensor is an instance of {@link Scalar}
   * @throws Exception if this tensor is {@link #unmodifiable()}
   * @see Tensors#reserve(int) */
  Tensor append(Tensor tensor);

  /** function is <em>not</em> Mathematica compliant:
   * <code>Length[3.14] == -1</code>
   * (Mathematica evaluates <code>Length[scalar] == 0</code>).
   * We deviate from this to avoid the ambiguity with length of an empty list:
   * <code>Length[{}] == 0</code>
   * 
   * <p>In order to check if a tensor is an empty vector use
   * <code>tensor.length() == 0</code>, or <code>Tensors.isEmpty(tensor)</code>.
   *
   * @return number of entries on the first level; {@link Scalar#LENGTH} for scalars */
  int length();

  /** @param fromIndex
   * @param toIndex
   * @return copy of sub tensor fromIndex inclusive to toIndex exclusive */
  Tensor extract(int fromIndex, int toIndex);

  /** negation of entries
   * 
   * @return tensor with all entries negated */
  Tensor negate();

  /** tensor addition
   * 
   * <p>addition is commutative: <code>a.add(b) equals b.add(a)</code>
   * 
   * @param tensor
   * @return this plus input tensor */
  Tensor add(Tensor tensor);

  /** tensor subtraction. Equivalent to <code>add(tensor.negate())</code>
   * 
   * @param tensor
   * @return this minus input tensor */
  Tensor subtract(Tensor tensor);

  /** scalar multiplication with given factor; scaling applies to all entries
   * 
   * @param scalar
   * @return tensor with elements of this tensor multiplied with given scalar */
  Tensor multiply(Scalar scalar);

  /** division of all scalars in this tensor by given scalar
   * 
   * <p>for scalar entries in double precision the function divide is numerically
   * more accurate than {@link #multiply(Scalar)} with the reciprocal.
   * 
   * @param scalar
   * @return tensor with elements of this tensor divided by given scalar */
  Tensor divide(Scalar scalar);

  /** dot product as in Mathematica
   * 
   * <p>The {@link Dimensions} of the dotted tensors reduce according to the pattern
   * <code>[n1, n2, n3, n4, n5] . [n5, n6, ..., n9] == [n1, n2, n3, n4, n6, ..., n9]</code>
   * 
   * <p>The implementation in the tensor library also support non-rectangular arrays
   * Example: {1, 2} . {{3, {4}}, {5, {6}}} == {13, {16}}
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

  /** the returned block consists of references to the elements in this tensor.
   * The function {@link #block(List, List)} is useful for applications such as
   * {@link ListCorrelate}.
   * 
   * The returned instance is typically not {@link Serializable}.
   * 
   * When block is called on unmodifiable tensor, then
   * the returned block consists of the references but is also unmodifiable.
   * 
   * Example:
   * <pre>
   * Tensor a = {1, 2, 3, 4, 5, 6};
   * Tensor b = a.block(List.of(2), List.of(3));
   * b.set(Array.zeros(3), Tensor.ALL);
   * </pre>
   * Afterwards, the tensor a == {1, 2, 0, 0, 0, 6}.
   * 
   * @param ofs location of return tensor in this tensor
   * @param len of return tensor
   * @return references to entries in block located at fromIndex of this tensor with given dimensions
   * @throws Exception if this tensor in unmodifiable and given dimensions are non-empty */
  Tensor block(List<Integer> ofs, List<Integer> len);

  /** For instance, if this tensor is the vector {0, 8, 1}, the function
   * stream() provides the three scalars 0, 8, 1 in a {@link Stream}.
   * 
   * <p>If this tensor is a matrix, the stream provides the references
   * to the rows of the matrix.
   * 
   * <p>If this tensor has been marked as unmodifiable, the elements of
   * the stream are unmodifiable as well.
   * 
   * @return stream over tensors contained in the list of this instance
   * @throws Exception if invoked on a {@link Scalar} instance, because
   * a scalar does not contain a list of tensors */
  Stream<Tensor> stream();

  /** iterator of list of entries.
   * The operation remove() is supported.
   * 
   * <p>If this tensor is unmodifiable, then
   * <ul>
   * <li>an entry provided by next() is unmodifiable, and
   * <li>remove() throws an Exception.
   * </ul>
   * 
   * @return references to entries in this tensor */
  @Override // from Iterable<Tensor>
  Iterator<Tensor> iterator();
}
