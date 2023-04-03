// code by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.UpperTriangularize;

enum TestHelper {
  ;
  public static final void check(Tensor matrix, HessenbergDecomposition hessenbergDecomposition) {
    Tensor p = hessenbergDecomposition.getUnitary();
    UnitaryMatrixQ.require(p);
    Tensor h = hessenbergDecomposition.getH();
    Tolerance.CHOP.requireClose(UpperTriangularize.of(h, -1), h);
    Tensor result = Dot.of(p, h, ConjugateTranspose.of(p));
    Tolerance.CHOP.requireClose(matrix, result);
  }
}
