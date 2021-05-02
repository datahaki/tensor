// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.Parallelize;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.sca.Clip;

/* package */ abstract class BivariateEvaluation {
  private final Clip clipX;
  private final Clip clipY;

  public BivariateEvaluation(Clip clipX, Clip clipY) {
    this.clipX = clipX;
    this.clipY = clipY;
  }

  public Tensor image(int resolution) {
    Tensor re = Subdivide.increasing(clipX, resolution - 1);
    Tensor im = Subdivide.increasing(clipY, resolution - 1);
    return Parallelize.matrix((i, j) -> function(re.Get(j), im.Get(i)), resolution, resolution);
  }

  protected abstract Scalar function(Scalar re, Scalar im);
}
