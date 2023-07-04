// code by jph
package ch.alpine.tensor.io;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** the extraction of primitive data types from a {@link Tensor}
 * only works for tensors with {@link Scalar} entries
 * that all support the operation {@link Scalar#number()} */
public enum Primitives {
  ;
  /** @param vector
   * @return stream of all scalars in tensor mapped to {@link Number} */
  public static Stream<Number> toNumberStream(Tensor vector) {
    return vector.stream().map(Scalar.class::cast).map(Scalar::number);
  }
  // ---

  /** @param vector
   * @return */
  public static DoubleStream toDoubleStream(Tensor vector) {
    return toNumberStream(vector).mapToDouble(Number::doubleValue);
  }

  /** @param vector
   * @return list of double values of all scalars in given vector */
  public static List<Double> toListDouble(Tensor vector) {
    return toNumberStream(vector).map(Number::doubleValue).collect(Collectors.toList());
  }

  /** @param vector
   * @return array of double values of all scalars in tensor */
  public static double[] toDoubleArray(Tensor vector) {
    return toDoubleStream(vector).toArray();
  }

  /** @param tensor
   * @return 2-dimensional array of double's with first dimension equal to tensor.length() */
  public static double[][] toDoubleArray2D(Tensor tensor) {
    double[][] array = new double[tensor.length()][];
    int index = -1;
    for (Tensor entry : tensor)
      array[++index] = toDoubleArray(entry);
    return array;
  }

  /** @param vector
   * @return */
  public static DoubleBuffer toDoubleBuffer(Tensor vector) {
    DoubleBuffer doubleBuffer = DoubleBuffer.allocate(vector.length());
    toDoubleStream(vector).forEach(doubleBuffer::put);
    ((java.nio.Buffer) doubleBuffer).flip();
    return doubleBuffer;
  }

  // ---
  /** @param vector
   * @return list of double values of all scalars in tensor */
  public static List<Float> toListFloat(Tensor vector) {
    return toNumberStream(vector).map(Number::floatValue).collect(Collectors.toList());
  }

  /** @param vector
   * @return array of double values of all scalars in tensor */
  public static float[] toFloatArray(Tensor vector) {
    return toFloatBuffer(vector).array();
  }

  /** @param tensor
   * @return 2-dimensional array of float's with first dimension equal to tensor.length() */
  public static float[][] toFloatArray2D(Tensor tensor) {
    float[][] array = new float[tensor.length()][];
    int index = -1;
    for (Tensor entry : tensor)
      array[++index] = toFloatArray(entry);
    return array;
  }

  /** @param vector
   * @return */
  public static FloatBuffer toFloatBuffer(Tensor vector) {
    FloatBuffer floatBuffer = FloatBuffer.allocate(vector.length());
    toNumberStream(vector).map(Number::floatValue).forEach(floatBuffer::put);
    ((java.nio.Buffer) floatBuffer).flip();
    return floatBuffer;
  }

  // ---
  public static LongStream toLongStream(Tensor vector) {
    return toNumberStream(vector).mapToLong(Number::longValue);
  }

  /** does not perform rounding, but uses Scalar::number().longValue()
   * 
   * @param vector
   * @return list of long values of all scalars in given vector */
  public static List<Long> toListLong(Tensor vector) {
    return toNumberStream(vector).map(Number::longValue).collect(Collectors.toList());
  }

  /** does not perform rounding, but uses Scalar::number().longValue()
   * 
   * @param vector
   * @return array of long values of all scalars in tensor */
  public static long[] toLongArray(Tensor vector) {
    return toLongStream(vector).toArray();
  }

  /** @param vector
   * @return */
  public static LongBuffer toLongBuffer(Tensor vector) {
    LongBuffer longBuffer = LongBuffer.allocate(vector.length());
    toLongStream(vector).forEach(longBuffer::put);
    ((java.nio.Buffer) longBuffer).flip();
    return longBuffer;
  }

  // ---
  public static IntStream toIntStream(Tensor vector) {
    return toNumberStream(vector).mapToInt(Number::intValue);
  }

  /** does not perform rounding, but uses Scalar::number().intValue()
   * 
   * @param vector
   * @return list of int values of all scalars in tensor */
  public static List<Integer> toListInteger(Tensor vector) {
    return toNumberStream(vector).map(Number::intValue).collect(Collectors.toList());
  }

  /** does not perform rounding, but uses Scalar::number().intValue()
   * 
   * @param tensor
   * @return array of int values of all scalars in tensor */
  public static int[] toIntArray(Tensor tensor) {
    return toIntStream(tensor).toArray();
  }

  /** does not perform rounding, but uses Scalar::number().intValue()
   * 
   * @param tensor
   * @return 2-dimensional array of int's with first dimension equal to tensor.length() */
  public static int[][] toIntArray2D(Tensor tensor) {
    int[][] array = new int[tensor.length()][];
    int index = -1;
    for (Tensor entry : tensor)
      array[++index] = toIntArray(entry);
    return array;
  }

  /** @param tensor
   * @return */
  public static IntBuffer toIntBuffer(Tensor tensor) {
    IntBuffer intBuffer = IntBuffer.allocate(tensor.length());
    toIntStream(tensor).forEach(intBuffer::put);
    ((java.nio.Buffer) intBuffer).flip();
    return intBuffer;
  }

  // ---
  /** @param tensor
   * @return array of byte values of all scalars in given tensor */
  public static byte[] toByteArray(Tensor tensor) {
    return toByteBuffer(tensor).array();
  }

  /** @param tensor
   * @return byte buffer containing byte values of all scalars in given tensor */
  public static ByteBuffer toByteBuffer(Tensor tensor) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(tensor.length());
    toNumberStream(tensor).forEach(number -> byteBuffer.put(number.byteValue()));
    ((java.nio.Buffer) byteBuffer).flip();
    return byteBuffer;
  }
}
