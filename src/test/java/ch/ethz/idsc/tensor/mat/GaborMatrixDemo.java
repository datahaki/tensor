// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;

/* package */ enum GaborMatrixDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = GaborMatrix.of(255, Tensors.vector(0.1, 0.2), RealScalar.of(0.3));
    Export.of(HomeDirectory.Pictures(GaborMatrix.class.getSimpleName() + ".png"), //
        ArrayPlot.of(tensor, ColorDataGradients.CLASSIC));
  }
}
