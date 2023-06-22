// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.ReIm;

/* package */ record ComplexWrap(Scalar[][] matrixT) {
  public void set(int i, int idx, Scalar z) {
    ReIm reIm = new ReIm(z);
    matrixT[i][idx - 1] = reIm.re();
    matrixT[i][idx] = reIm.im();
  }
}
