// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.itp.LanczosInterpolation;
import ch.alpine.tensor.sca.Clips;

/* package */ enum LanczosDemo {
  ;
  private static final int SEMI = 4;

  private static Tensor interp(Tensor row, Tensor sy) {
    Interpolation interpolation = LanczosInterpolation.of(row, SEMI);
    return Tensor.of(sy.stream().map(vy -> interpolation.get(Tensors.of(vy))));
  }

  public static void main(String[] args) throws IOException {
    Tensor images = Import.of(HomeDirectory.file("summary.png"));
    List<Integer> list = Dimensions.of(images);
    Interpolation interpolation = LanczosInterpolation.of(images, SEMI);
    float factor = 1.7f;
    int nx = Math.round(list.get(0) * factor);
    int ny = Math.round(list.get(1) * factor);
    Tensor sx = Subdivide.of(0, list.get(0) - 1, nx - 1);
    Tensor sy = Subdivide.of(0, list.get(1) - 1, ny - 1);
    Tensor result = Tensor.of(sx.stream().map(vx -> interpolation.get(Tensors.of(vx))));
    result = Tensor.of(result.stream().map(row -> interp(row, sy)));
    result = result.map(Clips.interval(0, 255));
    Export.of(HomeDirectory.file(String.format("castle%02d.png", Math.round(factor * 10))), result);
  }
}
