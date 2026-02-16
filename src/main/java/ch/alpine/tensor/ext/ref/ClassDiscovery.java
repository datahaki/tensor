// code by lcm
// adapted by jph
package ch.alpine.tensor.ext.ref;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassDiscovery {
  /** @param classpath
   * @param classVisitor */
  public static void execute(String classpath, ClassVisitor classVisitor) {
    new ClassDiscovery(classpath, classVisitor).findClasses();
  }

  // ---
  private final String classpath;
  private final ClassVisitor classVisitor;

  private ClassDiscovery(String classpath, ClassVisitor classVisitor) {
    this.classpath = classpath;
    this.classVisitor = classVisitor;
  }

  private void visitDirectory(URLClassLoader cldr, String classpath_entry, File dir, String visiting_classpath) {
    if (!dir.canRead())
      return;
    for (File file : dir.listFiles()) {
      if (!file.canRead())
        continue;
      String fname = file.getName();
      if (file.isDirectory()) {
        // found a directory. recursively traverse the directory and
        // search for .class files
        if (fname.contains("."))
          continue;
        // Modified by Jan in order to enable nested packages
        String vc = visiting_classpath.isEmpty() ? fname : visiting_classpath + "." + fname;
        visitDirectory(cldr, classpath_entry, file, vc);
      } else //
      if (file.isFile() && fname.endsWith(".class")) {
        // found a .class file. Construct its full classname and pass
        // it to the class visitor
        // Modified by Jan in order to enable nested packages
        String className = visiting_classpath + "." + fname.substring(0, fname.length() - 6);
        try {
          Class<?> cls = cldr.loadClass(className);
          if (Objects.nonNull(cls))
            classVisitor.accept(classpath_entry, cls);
        } catch (Throwable ex) {
          // ---
        }
      }
    }
  }

  /** Given a colon-delimited list of jar files, iterate over the classes in them. */
  private void findClasses() {
    final String ps = File.pathSeparator;
    String[] items = classpath.split(ps);
    // Create a class loader that has access to the whole class path.
    URL[] urls = new URL[items.length];
    try {
      for (int index = 0; index < items.length; ++index)
        urls[index] = new File(items[index]).toURI().toURL();
    } catch (IOException ioException) {
      IO.println("ClassDiscoverer ERR: " + ioException);
      return;
    }
    try (URLClassLoader urlClassLoader = new URLClassLoader(urls)) {
      for (String item : items)
        if (item.endsWith(".jar")) {
          try (JarFile jarFile = new JarFile(item)) {
            for (Enumeration<JarEntry> enumeration = jarFile.entries(); enumeration.hasMoreElements();) {
              JarEntry jarEntry = enumeration.nextElement();
              String name = jarEntry.getName();
              // skip private classes?
              if (name.endsWith(".class")) {
                // convert the path into a class name
                String className = name.substring(0, name.length() - 6);
                className = className.replace('/', '.');
                className = className.replace('\\', '.');
                // try loading that class
                try {
                  Class<?> cls = urlClassLoader.loadClass(className);
                  if (Objects.isNull(cls))
                    continue;
                  classVisitor.accept(item, cls);
                } catch (Throwable throwable) {
                  // ---
                }
              }
            }
          } catch (IOException ioException) {
            IO.println("Error extracting " + item);
          }
        } else {
          File file = new File(item);
          if (!file.isDirectory())
            continue;
          visitDirectory(urlClassLoader, item, file, "");
        }
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }
}
