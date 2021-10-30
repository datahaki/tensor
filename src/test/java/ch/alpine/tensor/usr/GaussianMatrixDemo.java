// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.mat.GaussianMatrix;

/* package */ enum GaussianMatrixDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = GaussianMatrix.of(255);
    Export.of(HomeDirectory.Pictures(GaussianMatrix.class.getSimpleName() + ".png"), //
        Raster.of(tensor, ColorDataGradients.PARULA));
  }
}
