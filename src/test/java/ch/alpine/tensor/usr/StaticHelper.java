// code by jph
package ch.alpine.tensor.usr;

import java.io.File;
import java.io.IOException;

import ch.alpine.tensor.Parallelize;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.img.TensorArrayPlot;
import ch.alpine.tensor.io.Export;

/* package */ enum StaticHelper {
  ;
  private static final int GALLERY_RES = 720; // 128 + 64;

  public static File file(Class<?> cls) {
    File DIRECTORY = HomeDirectory.file("Projects", "latex", "images", "tensor");
    return new File(DIRECTORY, cls.getSimpleName() + ".png");
  }

  public static void export(BivariateEvaluation bivariateEvaluation, Class<?> cls, ColorDataGradient colorDataGradient) throws IOException {
    Export.of(file(cls), TensorArrayPlot.of(StaticHelper.image(bivariateEvaluation, GALLERY_RES), colorDataGradient));
  }

  public static Tensor image(BivariateEvaluation bivariateEvaluation, int resolution) {
    Tensor re = Subdivide.increasing(bivariateEvaluation.clipX(), resolution - 1);
    Tensor im = Subdivide.increasing(bivariateEvaluation.clipY(), resolution - 1);
    return Parallelize.matrix((i, j) -> bivariateEvaluation.apply(re.Get(j), im.Get(i)), resolution, resolution);
  }
}
