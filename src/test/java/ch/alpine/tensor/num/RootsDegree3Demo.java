// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.nrm.VectorInfinityNorm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;

/* package */ enum RootsDegree3Demo {
  ;
  public static void main(String[] args) {
    Distribution distribution = NormalDistribution.standard();
    Tensor errors = Tensors.empty();
    for (int count = 0; count < 100; ++count) {
      Tensor coeffs = RandomVariate.of(distribution, 4);
      Tensor r0 = RootsDegree3Full.of(coeffs);
      Tensor r1 = RootsDegree3.of(coeffs);
      ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
      errors.append(Tensors.of( //
          VectorInfinityNorm.of(r0.map(scalarUnaryOperator)), //
          VectorInfinityNorm.of(r1.map(scalarUnaryOperator))));
    }
    Tensor total = errors.stream().reduce(Tensor::add).get();
    System.out.println(Pretty.of(errors));
    System.out.println(total);
  }
}
