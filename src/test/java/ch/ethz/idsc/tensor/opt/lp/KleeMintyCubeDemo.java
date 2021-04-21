// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Tensor;

enum KleeMintyCubeDemo {
  ;
  public static void main(String[] args) {
    KleeMintyCube kmc = new KleeMintyCube(3);
    // kmc.show();
    Tensor x = // LinearProgramming.maxLessEquals(kmc.c, kmc.m, kmc.b);
        LinearProgramming.of(kmc.linearProgram);
    System.out.println("LP" + x + " <- solution");
  }
}
