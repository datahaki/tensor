// code by jph
package ch.alpine.tensor.ext.ref;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public record ImplementationDiscovery<T>(Class<T> cls) {
  public static boolean isInSubpackageOf(Class<?> clazz, String basePackage) {
    Package pkg = clazz.getPackage();
    if (pkg == null)
      return false;
    String pkgName = pkg.getName();
    return pkgName.equals(basePackage) || pkgName.startsWith(basePackage + ".");
  }

  public List<T> getInstances(String basePackage) {
    List<T> collection = new LinkedList<>();
    for (Class<?> implementation : getImplementations(basePackage)) {
      List<T> list = (List<T>) getInstances(implementation);
      collection.addAll(list);
    }
    return collection;
  }

  private List<Class<?>> getImplementations(String basePackage) {
    List<Class<?>> list = new LinkedList<>();
    ClassVisitor classVisitor = new ClassVisitor() {
      @Override
      public void accept(String jarfile, Class<?> impl) {
        if (isInSubpackageOf(impl, basePackage))
          if (cls.isAssignableFrom(impl))
            list.add(impl);
      }
    };
    ClassDiscovery.execute(ClassPaths.getDefault(), classVisitor);
    return list;
  }

  private List<T> getInstances(Class<?> implementation) {
    List<T> list = new LinkedList<>();
    for (Field field : implementation.getDeclaredFields())
      if (Modifier.isStatic(field.getModifiers()))
        try {
          field.setAccessible(true); // mandatory
          Object object = field.get(null);
          if (cls.isInstance(object)) {
            list.add(cls.cast(object));
          }
        } catch (Exception e) {
          System.err.println("error " + e.getMessage());
        }
    // ---
    if (implementation.isEnum()) {
      // enum constants are handled as fields above
    } else //
    if (implementation.isInterface()) {
      // ---
    } else //
    if (implementation.isRecord()) {
      // ---
    } else //
    if (implementation.isAnonymousClass()) {
      // ---
    } else //
      try {
        Constructor<?> constructor = implementation.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object object = constructor.newInstance();
        list.add(cls.cast(object));
      } catch (Exception exception) {
        // default constructor may not exist
      }
    return list;
  }
}
