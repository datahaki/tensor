// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ abstract class InfluenceMatrixBase implements InfluenceMatrix {
  @Override
  public final Tensor leverages() {
    return Diagonal.of(matrix());
  }

  @Override
  public final Tensor leverages_sqrt() {
    return leverages().map(Sqrt.FUNCTION);
  }

  @Override
  public final Tensor residualMaker() {
    AtomicInteger atomicInteger = new AtomicInteger();
    // I-X^+.X is projector on ker X
    return Tensor.of(matrix().stream() //
        .map(Tensor::negate) // copy
        .map(row -> {
          row.set(RealScalar.ONE::add, atomicInteger.getAndIncrement());
          return row; // by ref
        }));
  }

  @Override
  public final Tensor kernel(Tensor vector) {
    return vector.subtract(image(vector));
  }
}
