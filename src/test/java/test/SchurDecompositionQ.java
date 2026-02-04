package test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.LowerTriangularize;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.qr.SchurDecomposition;

public enum SchurDecompositionQ {
  ;
  public static SchurDecomposition of(Tensor matrix) {
    SchurDecomposition schurDecomposition = SchurDecomposition.of(matrix);
    Tensor t = schurDecomposition.getT();
    Tensor p = schurDecomposition.getUnitary();
    Tensor lower = LowerTriangularize.of(t, -2);
    // IO.println(Pretty.of(lower.map(Round._3)));
    Tolerance.CHOP.requireAllZero(lower);
    UnitaryMatrixQ.INSTANCE.requireMember(p);
    Tensor result = Dot.of(p, t, ConjugateTranspose.of(p));
    Tolerance.CHOP.requireClose(matrix, result);
    return schurDecomposition;
  }
}
