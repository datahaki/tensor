// code by jph
package ch.alpine.tensor.mat.ev;

/** https://en.wikipedia.org/wiki/Givens_rotation */
/* package */ interface GivensRotation {
  /** multiplication from both sides
   * 
   * @param p
   * @param q */
  void transform(int p, int q);

  /** multiplication from left
   * 
   * @param p
   * @param q */
  void dot(int p, int q);
}
