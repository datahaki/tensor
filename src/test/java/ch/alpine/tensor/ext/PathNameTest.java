// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

class PathNameTest {
  final String USER_NAME = System.getProperty("user.name");

  @Test
  void testDoesNotCreate() {
    Path path = HomeDirectory.path("doesnotexist");
    assumeTrue(USER_NAME.equals("datahaki"));
    assertFalse(Files.exists(path));
    assertFalse(Files.isDirectory(path));
    Path sub = HomeDirectory.path("doesnotexist", "sub");
    assertFalse(Files.exists(path));
    assertFalse(Files.isDirectory(path));
    assertThrows(Exception.class, () -> Files.list(sub));
  }

  @Test
  void testFile() {
    assertEquals(PathName.of(HomeDirectory.path(".git")).title(), ".git");
    assertEquals(PathName.of(HomeDirectory.path("a.git")).title(), "a");
  }

  @Test
  void testOnlyFile() {
    PathName pathName = PathName.of(Path.of("asd.git"));
    assertEquals(pathName.title(), "asd");
    assertNull(pathName.parent());
  }

  @DisabledOnOs(OS.WINDOWS)
  @Test
  void testWhitespace() {
    assertEquals(PathName.of(HomeDirectory.path("a.git ")).title(), "a");
  }

  @Test
  void testString() {
    assertEquals(PathName.of(Path.of(".git")).title(), ".git");
    assertEquals(PathName.of(Path.of("a.git")).title(), "a");
  }

  @Test
  void testString2() {
    assertEquals(PathName.of(Path.of("/asd/.git")).title(), ".git");
    assertEquals(PathName.of(Path.of("/asd/a.git")).title(), "a");
    assertEquals(PathName.of(Path.of("asd/.git")).title(), ".git");
    assertEquals(PathName.of(Path.of("asd/a.git")).title(), "a");
  }

  @Test
  void testExample() {
    assertEquals(PathName.of(Path.of("/home/user/Documents")).title(), "Documents");
    assertEquals(PathName.of(Path.of("/home/user/info.txt")).title(), "info");
    assertEquals(PathName.of(Path.of("/home/user/info.txt.gz")).title(), "info.txt");
  }

  @Test
  void testFile1() {
    assertEquals(PathName.of(HomeDirectory.path(".git")).extension(), "");
    assertEquals(PathName.of(HomeDirectory.path("a.git")).extension(), "git");
  }

  @DisabledOnOs(OS.WINDOWS)
  @Test
  void testWhitespace2() {
    assertEquals(PathName.of(HomeDirectory.path("a.git ")).extension(), "git ");
  }

  @Test
  void testString3() {
    assertEquals(PathName.of(Path.of(".git")).extension(), "");
    assertEquals(PathName.of(Path.of("a.git")).extension(), "git");
  }

  @Test
  void testString4() {
    assertEquals(PathName.of(Path.of("/asd/.git")).extension(), "");
    assertEquals(PathName.of(Path.of("/asd/a.git")).extension(), "git");
    assertEquals(PathName.of(Path.of("asd/.git")).extension(), "");
    assertEquals(PathName.of(Path.of("asd/a.git")).extension(), "git");
  }

  @Test
  void testFileHomeDir() {
    PathName filename = PathName.of(HomeDirectory.path("some.properties"));
    assertEquals(filename.title(), "some");
    assertEquals(filename.extension(), "properties");
    assertEquals(filename.withExtension("txt"), HomeDirectory.path("some.txt"));
    assertThrows(Exception.class, () -> filename.withExtension(null));
  }

  @Test
  void testDirectory() {
    PathName filename = PathName.of(HomeDirectory.Documents.resolve());
    assertEquals(filename.extension(), "");
  }

  @Test
  void testHiddenDirectory() {
    PathName pathName = PathName.of(HomeDirectory.Documents.resolve(".git"));
    assertEquals(pathName.extension(), "");
    assertEquals(pathName.title(), ".git");
  }

  @Test
  void testHash() {
    PathName p1 = PathName.of(HomeDirectory.Pictures.resolve("scene.png"));
    PathName p2 = PathName.of(HomeDirectory.Pictures.resolve("scene.png"));
    assertNotSame(p1, p2);
    assertEquals(p1, p2);
    assertEquals(p1.hashCode(), p2.hashCode());
    Set<PathName> set = new HashSet<>();
    set.add(p1);
    set.add(p2);
    assertEquals(set.size(), 1);
    NavigableSet<Object> treeSet = new TreeSet<>();
    assertThrows(Exception.class, () -> treeSet.add(p1));
  }
}
