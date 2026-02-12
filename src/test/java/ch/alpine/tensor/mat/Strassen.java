// code by jph
package ch.alpine.tensor.mat;

import java.util.Arrays;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ArrayFlatten;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.ext.Integers;

/** matrix multiplication by Strassen
 * 
 * Reference:
 * "Algorithmik" by Uwe Schoening
 * 
 * tested for size = 512 x 512
 * 
 * n<32
 * 22.04
 * 18.73
 * 
 * n<16
 * 22.34
 * 18.20 */
public class Strassen implements TensorBinaryOperator {
  private final int threshold;

  /** @param threshold for example 16 */
  public Strassen(int threshold) {
    this.threshold = Integers.requirePositive(threshold);
  }

  @Override
  public Tensor apply(Tensor a, Tensor b) {
    return recur(SquareMatrixQ.INSTANCE.require(a), SquareMatrixQ.INSTANCE.require(b));
  }

  private Tensor recur(Tensor a, Tensor b) {
    int n = a.length();
    if (n < threshold || (n % 2) != 0)
      return a.dot(b);
    // ---
    int m = n / 2;
    List<Integer> dims = Arrays.asList(m, m);
    Tensor a11 = a.block(Arrays.asList(0, 0), dims);
    Tensor a12 = a.block(Arrays.asList(0, m), dims);
    Tensor a21 = a.block(Arrays.asList(m, 0), dims);
    Tensor a22 = a.block(dims, dims);
    Tensor b11 = b.block(Arrays.asList(0, 0), dims);
    Tensor b12 = b.block(Arrays.asList(0, m), dims);
    Tensor b21 = b.block(Arrays.asList(m, 0), dims);
    Tensor b22 = b.block(dims, dims);
    // ---
    Tensor p1 = recur(a12.subtract(a22), b21.add(b22));
    Tensor p2 = recur(a11.add(a22), b11.add(b22));
    Tensor p3 = recur(a11.subtract(a21), b11.add(b12));
    Tensor p4 = recur(a11.add(a12), b22);
    Tensor p5 = recur(a11, b12.subtract(b22));
    Tensor p6 = recur(a22, b21.subtract(b11));
    Tensor p7 = recur(a21.add(a22), b11);
    // ---
    return ArrayFlatten.of(new Tensor[][] { //
        { p1.add(p2).subtract(p4).add(p6), p4.add(p5) }, //
        { p6.add(p7), p2.subtract(p3).add(p5).subtract(p7) } });
  }
}
