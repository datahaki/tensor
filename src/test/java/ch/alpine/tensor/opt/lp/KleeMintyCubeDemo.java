// code by jph
package ch.alpine.tensor.opt.lp;

import ch.alpine.tensor.Tensor;

enum KleeMintyCubeDemo {
  ;
  public static void main(String[] args) {
    KleeMintyCube kmc = KleeMintyCube.of(3);
    // kmc.show();
    Tensor x = // LinearProgramming.maxLessEquals(kmc.c, kmc.m, kmc.b);
        LinearProgramming.of(kmc.linearProgram);
    System.out.println("LP" + x + " <- solution");
  }
}
