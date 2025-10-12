/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.launcher;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * Transform a DepanFX launch specification into a form suitable
 * for passing to the Java executable as an argument file.
 * The DepanFX launch specification provides arguments from
 * after Windows command line expansion.
 * This tidies up the specification file to one argument per line,
 * and adjusts the escaping to match Java argument file expectations.
 * {@link https://docs.oracle.com/en/java/javase/17/docs/specs/man/java.html#java-command-line-argument-files}
 */
public class DepanFxArgFile {

  public static final String SEMICOLON_JOIN_STRING = ";";

  public static final String COMMENT_PREFIX = "#";

  public static final String CLASSPATH_FLAG = "-classpath";

  public static final String MODULEPATH_FLAG = "--module-path";

  public static final String ECHO_ON_LINE = "ECHO is on.";

  public static final String ARG_DASH = "-";

  private enum LineMode {
    ARG_LINES,
    CLASSPATH_LINES,
    MODULEPATH_LINES;
  };

  private static List<String> args = new ArrayList<>();

  private static List<String> classJars = new ArrayList<>();

  private static List<String> moduleJars = new ArrayList<>();

  private static LineMode lineMode = LineMode.ARG_LINES;

  /////////////////////////////////////
  // Input methods

  private static void parseSpec(Path specFile)
      throws IOException {
    try (Stream<String> specLines = Files.lines(specFile)) {
      specLines
          .map(String::trim)
          .filter(line -> !line.isEmpty())
          .filter(line -> !line.startsWith(COMMENT_PREFIX))
          .filter(line -> !ECHO_ON_LINE.equals(line))
          .forEach(line -> parseSpecLine(line));
    }
  }

  private static void parseSpecLine(String specLine) {
    if (CLASSPATH_FLAG.equals(specLine)) {
      lineMode = LineMode.CLASSPATH_LINES;
      args.add(specLine);
      return;
    }
    if (MODULEPATH_FLAG.equals(specLine)) {
      lineMode = LineMode.MODULEPATH_LINES;
      args.add(specLine);
      return;
    }
    if (specLine.startsWith(ARG_DASH)) {
      lineMode = LineMode.ARG_LINES;
      addArgLine(specLine);
      return;
    }
    switch(lineMode) {
    case CLASSPATH_LINES:
      classJars.add(specLine);
      return;
    case MODULEPATH_LINES:
      moduleJars.add(specLine);
      return;
    case ARG_LINES:
    default:
    }
    lineMode = LineMode.ARG_LINES;
    addArgLine(specLine);
  }

  private static void addArgLine(String argLine) {
    WindowsLineTokenizer tokens = new WindowsLineTokenizer(argLine);
    while (tokens.hasToken()) {
      args.add(tokens.getToken());
      tokens.advToken();
    }
  }

  /////////////////////////////////////
  // Output methods

  private static List<String> buildArgs() {
    List<String> result = new ArrayList<>();
    for (String line : args) {
      addArg(result, line);
      if (CLASSPATH_FLAG.equals(line)) {
        result.add(buildFiles(classJars));
        continue;
      }
      if (MODULEPATH_FLAG.equals(line)) {
        result.add(buildFiles(moduleJars));
        continue;
      }
    }

    return result;
  }

  private static void addArg(List<String> result, String arg) {
    result.add(escapeToArgFile(arg));
  }

  private static String buildFiles(List<String> jars) {
    StringJoiner join = new StringJoiner(SEMICOLON_JOIN_STRING);
    jars.stream()
        .map(DepanFxArgFile::escapeToArgFile)
        .forEach(join::add);
    return join.toString();
  }

  private static String escapeToArgFile(String arg) {
    String result = arg.replace("\\", "\\\\");;
    return result;
  }

  /////////////////////////////////////
  // Main

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.err.println("Error: incorrect number of parameters.");
      System.err.println("Usage: java "
          + DepanFxArgFile.class.getName()
          + " <argument-spec> <argument-args>");
      System.exit(1);
    }

    parseSpec(Paths.get(args[0]));
    List<String> argLines = buildArgs();
    Files.write(Paths.get(args[1]), argLines);
  }
}
