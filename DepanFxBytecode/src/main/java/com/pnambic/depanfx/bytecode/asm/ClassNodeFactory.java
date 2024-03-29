/*
 * Copyright 2009 The Depan Project Authors
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
package com.pnambic.depanfx.bytecode.asm;

import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.java.graph.ClassNode;
import com.pnambic.depanfx.java.graph.JavaNode;
import com.pnambic.depanfx.java.graph.JavaRelation;
import com.pnambic.depanfx.java.graph.PackageNode;
import com.pnambic.depanfx.java.graph.graphdata.ClassInfo;
import com.pnambic.depanfx.java.graph.graphdata.ClassInfo.ClassKind;

import org.springframework.asm.Type;

import java.io.File;

/**
 * There are lots of places where a some kind of type reference (i.e. strings)
 * need to be converted into a JavaElement.  This static namespace provides a
 * common bundle of methods for converted abbreviated type names into
 * fully-qualified type named that are suitable for node ids.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver </a>
 */
public class ClassNodeFactory {

  /**
   * {@link DependenciesListener} called when a dependency is found.
   */
  private final DepanFxGraphModelBuilder builder;

  /**
   * File element for source code.
   */
  private final DocumentNode fileNode;

  public ClassNodeFactory(
      DepanFxGraphModelBuilder builder, DocumentNode fileNode) {
    this.builder = builder;
    this.fileNode = fileNode;
  }

  /**
   * Convert a bytecode type descriptor into a {@link TypeElement} from the
   * {@code DepanJava} plug-in with the proper id.  As a simple example,
   * "Ljava/lang/String;" becomes "java.lang.String".
   * @param builder 
   * @param desc {@codeString} descriptor for a {@link Type}
   *
   * @return {@code TypeElement} suitable for addition to a dependency graph
   */
  public ClassNode fromDescriptor(String desc) {
    Type type = Type.getType(desc);
    return prepareClassNode(type);
  }

  /**
   * Convert a type named as an interface into a {@link InterfaceElement}
   * from the {@code DepanJava} plug-in with the proper id.
   *
   * @param interfaceName name of a supported interface, often an abbreviated
   *     name of a type
   * @return {@code InterfaceElement} suitable for addition to a
   *     dependency graph
   */
  public ClassNode fromInterfaceName(String interfaceName) {
    Type type = Type.getObjectType(interfaceName);
    return prepareClassNode(type);
  }

  /**
   * Convert a type's internal name into a {@link TypeElement}
   * from the {@code DepanJava} plug-in with the proper id.
   *
   * @param desc descriptor for a {@link Type}
   * @return {@code TypeElement} suitable for addition to a dependency graph
   */
  public ClassNode fromInternalName(String internalName) {
    Type type = Type.getObjectType(internalName);
    return prepareClassNode(type);
  }

  /**
   * Create the dependency from a {@link JavaElement} to an annotation.
   */
  public void buildAnnotationDep(
      JavaNode target, String desc, boolean visible) {
    ClassNode annotationNode = fromDescriptor(desc);
    builder.addNodeInfo(annotationNode, ClassInfo.class,
        new ClassInfo(ClassKind.KIND_ANNOTATION));
    if (visible) {
      addEdge(target, annotationNode, JavaRelation.RUNTIME_ANNOTATION);
      return;
    }
    addEdge(target, annotationNode, JavaRelation.COMPILE_ANNOTATION);
  }

  private void addEdge(
      GraphNode head, GraphNode tail, GraphRelation relation) {
    builder.addEdge(
        new GraphEdge(builder.mapNode(head), builder.mapNode(tail), relation));
  }

  private ClassNode prepareClassNode(Type type) {
    String fqcn = getFullyQualifiedTypeName(type);
    ClassNode lookup = new ClassNode(fqcn);
    ClassNode result = (ClassNode) builder.mapNode(lookup);

    // Build the package and source file structure if the class node
    // is new to the builder.
    if (lookup == result ) {
      installPackageForClassNode(result);
    }
    return result;
  }

  /**
   * Convert a {@link Type} reference used in an interface to it fully-
   * qualified name.
   *
   * @param type {@code Type} used in an interface
   * @return fully qualified name for {@code Type}
   */
  private String getFullyQualifiedInterfaceName(Type type) {
    if (type.getSort() == Type.ARRAY) {
      return Type.getObjectType(type.getInternalName()).getClassName();
    }
    return type.getClassName();
  }

  /**
   * Convert class and field {@link Type} references to it's fully-qualified
   * name.  The class's implemented interfaces should get the fully-qualified
   * name via {@link #getFullyQualifiedInterfaceName(Type)}.
   *
   * @param type {@code Type} used in a reference
   * @return fully qualified name for {@code Type}
   */
  private static String getFullyQualifiedTypeName(Type type) {
    if (type.getSort() == Type.ARRAY) {
      return Type.getType(type.toString().replaceAll("^\\[*", ""))
          .getClassName();
    }
    return type.getClassName();
  }

  /**
   * Install a package hierarchy and a matching directory hierarchy for
   * the full path name of the type.
   */
  private void installPackageForClassNode(ClassNode classNode) {
    // TODO(leeca): Add short-circuit early exit if package is already defined.
    // This would avoid a fair bit of unnecessary object creation.
    String fqcn = classNode.getFQCN();
    if (fqcn.contains("$")) {
      return;
    }

    String typePath = fqcn.replace('.', '/');
    File packageFile = new File(typePath).getParentFile();
    if (null == packageFile) {
      PackageNode result =
          (PackageNode) builder.mapNode(new PackageNode("<unnamed>"));
      addEdge(result, classNode, JavaRelation.CLASS);
      return;
    }
    File treeFile = createTreeFile();
    PackageTreeBuilder packageBuilder = new PackageTreeBuilder(builder);
    PackageNode packageNode = packageBuilder.installPackageTree(packageFile, treeFile);
    addEdge(packageNode, classNode, JavaRelation.CLASS);
  }

  /**
   * Define the tree for the file node, even if it doesn't have a directory.
   * This can happen for class files at the top of the analysis tree, such as
   * classes in the unnamed package at the top of a Jar file.
   *
   * @return valid directory tree reference
   */
  private File createTreeFile() {
    File result = fileNode.getPath().toFile().getParentFile();
    if (null == result) {
      return new File("");
    }
    return result;
  }
}
