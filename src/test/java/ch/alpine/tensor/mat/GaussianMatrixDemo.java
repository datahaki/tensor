// code by jph
package ch.alpine.tensor.mat;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ArrayPlot;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.Export;

/* package */ enum GaussianMatrixDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = GaussianMatrix.of(255);
    Export.of(HomeDirectory.Pictures(GaussianMatrix.class.getSimpleName() + ".png"), //
        ArrayPlot.of(tensor, ColorDataGradients.PARULA));
  }
}
