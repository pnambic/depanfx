package com.pnambic.depanfx.perspective.chooser;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DepanFxResourceFilter {

    private final String description;

    private final List<String> extensions;

    private final List<Class<?>> types;

    public DepanFxResourceFilter(
        String description, List<String> extensions, List<Class<?>> types) {
      this.description = description;
      this.extensions = extensions;
      this.types = types;
    }

    public String getDescription() {
      return description;
    }

    public List<PathMatcher> getPathMatchers() {
      List<PathMatcher> result = new ArrayList<>(extensions.size());
      FileSystem fileSys = FileSystems.getDefault();

      extensions.stream()
          .map(ext -> fileSys.getPathMatcher("glob:" + ext))
          .forEach(result::add);
      return result;
    }

    public boolean matchDocument(Object content) {
      return types.stream()
          .filter(t -> t.isAssignableFrom(content.getClass()))
          .findFirst()
          .isPresent();
    }

    public static DepanFxResourceFilter buildResourceFilter(
        String label, String rsrcExt, List<Class<?>> rsrcTypes) {
      String matchGlob = "*." + rsrcExt;

      return new DepanFxResourceFilter(
          label + "(" + matchGlob + ")",
          Collections.singletonList(matchGlob),
          rsrcTypes);
    }

    public static DepanFxResourceFilter buildResourceFilter(
        String label, String rsrcExt, Class<?> rsrcType) {

      return buildResourceFilter(label, rsrcExt,
          Collections.singletonList(rsrcType));
    }
}
