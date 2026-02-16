// code by lcm
package ch.alpine.tensor.ext.ref;

@FunctionalInterface
public interface ClassVisitor {
  /** @param jarfile
   * @param cls */
  void accept(String jarfile, Class<?> cls);
}
