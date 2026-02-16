// code by jph
package ch.alpine.tensor.ext.ref;

/* package */ enum StaticHelper {
  ;
  public static boolean isInSubpackageOf(Class<?> clazz, String basePackage) {
    Package pkg = clazz.getPackage();
    if (pkg == null)
      return false;
    String string = pkg.getName();
    return string.equals(basePackage) || string.startsWith(basePackage + ".");
  }
}
