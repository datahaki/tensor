// code by jph
package test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.BasisTransform;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.UpperTriangularize;
import ch.alpine.tensor.mat.qr.HessenbergDecomposition;

public enum HessenbergDecompositionQ {
  ;
  public static void check(Tensor matrix, HessenbergDecomposition hessenbergDecomposition) {
    Tensor p = hessenbergDecomposition.getUnitary();
    UnitaryMatrixQ.INSTANCE.requireMember(p);
    Tensor h = hessenbergDecomposition.getH();
    Tolerance.CHOP.requireClose(UpperTriangularize.of(h, -1), h);
    Tensor result = Dot.of(p, h, ConjugateTranspose.of(p));
    Tolerance.CHOP.requireClose(matrix, result);
    Tolerance.CHOP.requireClose(h, BasisTransform.of(matrix, 1, p));
  }
}
