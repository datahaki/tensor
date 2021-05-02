// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.Pretty;

/* package */ enum PrettyDemo {
  ;
  public static void main(String[] args) {
    {
      Tensor m = Tensors.of( //
          Tensors.vector(1, 2, 3), //
          Tensors.vector(4, 5) //
      );
      System.out.println(Pretty.of(m));
    }
    System.out.println("---");
    {
      Tensor m = Tensors.of(Tensors.of(RationalScalar.of(3, 2), RationalScalar.of(-23, 2444), RationalScalar.of(31231, 2)),
          Tensors.vectorDouble(2.3, 0.3, -0.2));
      System.out.println(Pretty.of(m));
    }
    System.out.println("---");
    {
      Tensor m = Tensors.of( //
          Tensors.of(Tensors.vector(2, -3, 4), Tensors.vector(2.3, 0.3, -0.2)), //
          Tensors.of(Tensors.vector(2, -3, 4), Tensors.vector(-2.3, 0.3, -0.2)));
      System.out.println(Pretty.of(m));
    }
    System.out.println("---");
    {
      Tensor m = Tensors.of( //
          Tensors.of(RationalScalar.of(3, 2), Tensors.vector(2.3, 0.3, -0.2)), //
          Tensors.of(Tensors.vector(2, -3, 4), Tensors.vector(-2.3, 0.3, -0.2)));
      System.out.println(Pretty.of(m));
    }
    System.out.println("---");
    {
      Tensor m = Tensors.of( //
          Tensors.of(Tensors.vector(33.2), RationalScalar.of(3, 2), Tensors.vector(2.3, 0.3, -.2)), //
          Tensors.of(Tensors.vector(2, -3, 4), Tensors.vector(-2.3, 0.3, -0.2)));
      System.out.println(Pretty.of(m));
    }
    System.out.println("---");
    {
      System.out.println(Pretty.of(DoubleScalar.NEGATIVE_INFINITY));
    }
    System.out.println("---");
    {
      System.out.println(Pretty.of(Tensors.vectorDouble(0.2, 0.3, Double.NEGATIVE_INFINITY)));
    }
  }
}
