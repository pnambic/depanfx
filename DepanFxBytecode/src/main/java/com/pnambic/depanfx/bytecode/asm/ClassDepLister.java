/*
 * Copyright 2007 The Depan Project Authors
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

import com.pnambic.depanfx.bytecode.AsmFactory;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.java.graph.ClassNode;
import com.pnambic.depanfx.java.graph.FieldNode;
import com.pnambic.depanfx.java.graph.JavaRelation;
import com.pnambic.depanfx.java.graph.MemberNode;
import com.pnambic.depanfx.java.graph.MethodNode;
import com.pnambic.depanfx.java.graph.graphdata.ClassInfo;
import com.pnambic.depanfx.java.graph.graphdata.ClassInfo.ClassKind;
import com.pnambic.depanfx.java.graph.graphdata.FieldInfo;
import com.pnambic.depanfx.java.graph.graphdata.MethodInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.ModuleVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.asm.TypePath;

/**
 * Implements a visitor of the ASM package, to find the dependencies in a class
 * file and build the dependency tree. A single {@link ClassDepLister} is used
 * for each file found in the explored directory or jar file.
 *
 * To build the dependencies tree, it calls the methods of a
 * {@link DependenciesListener}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ClassDepLister extends ClassVisitor {

  private static final Logger LOG =
      LoggerFactory.getLogger(ClassDepLister.class);

  private final AsmFactory asmFactory;

  /**
   * {@link DependenciesListener} called when a dependency is found.
   */
  private final DepanFxGraphModelBuilder builder;

  /**
   * File element for source code.
   */
  private final DocumentNode fileNode;

  /**
   * Construct classes, with all their inferred accouterments
   * (e.g. containing packages and source files).
   */
  private final ClassNodeFactory classBuilder;

  /**
   * class currently read class. (typically the class A when the file A.java is
   * read.
   */
  private ClassNode mainClass = null;

  /**
   * constructor for new {@link ClassDepLister}.
   *
   * @param asmFactory provide bytecode level visitors
   * @param builder {@link DependenciesListener} implementing callbacks
   * @param fileNode node for the .class file containing this class
   */
  public ClassDepLister(
      AsmFactory asmFactory,
      DepanFxGraphModelBuilder builder,
      DocumentNode fileNode) {
    super(asmFactory.getApiLevel());
    this.asmFactory = asmFactory;
    this.builder = builder;
    this.fileNode = fileNode;

    this.classBuilder = new ClassNodeFactory(builder, fileNode);
  }

  @Override
  public void visit(int version, int access, String name, String signature,
      String superName, String[] interfaces) {
    ClassKind classKind = getClassKind(access);
    if (classKind == ClassKind.KIND_MODULE) {
      mainClass =  new ClassNode("module-info");
      addClassInfo(mainClass, ClassKind.KIND_MODULE);
      return;
    }

    // Visit normal classes
    mainClass = classBuilder.fromInternalName(name);
    addClassInfo(mainClass, classKind);

    ClassNode baseClass = classBuilder.fromInternalName(superName);
    addEdge(baseClass, mainClass, JavaRelation.EXTENDS);

    for (String s : interfaces) {
      ClassNode interfaceNode =
          (ClassNode) builder.mapNode(classBuilder.fromInterfaceName(s));
      builder.addNodeInfo(interfaceNode, ClassInfo.class,
          new ClassInfo(ClassKind.KIND_INTERFACE));
      addEdge(interfaceNode, mainClass, JavaRelation.IMPLEMENTS);
    }
    checkAnonymousType(name);
  }

  @Override
  public ModuleVisitor visitModule(String name, int access, String version) {
    return asmFactory.buildModuleVisitor(builder, classBuilder, name, access);
  }

  @Override
  public FieldVisitor visitField(
      int access, String name, String desc, String signature, Object value) {
    MemberNode fieldNode = new FieldNode(mainClass.getFQCN(), name);

    ClassNode typeNode = classBuilder.fromDescriptor(desc);
    builder.addNodeInfo(fieldNode, FieldInfo.class,
        new FieldInfo(typeNode));

    // simple className
    addEdge(mainClass, typeNode, JavaRelation.TYPE);

    // field
    JavaRelation memberRelation = getMemberRelation(
        access, JavaRelation.STATIC_FIELD, JavaRelation.MEMBER_FIELD);
    addEdge(mainClass, fieldNode, memberRelation);

    // generic types in signature
    // TODO(ycoppel): how to get types in generics ? (signature)

    return asmFactory.getGenericFieldVisitor();
  }

  @Override
  public void visitInnerClass(
      String name, String outerName, String innerName, int access) {
    if ((null == outerName) || (null == innerName)) {
      LOG.warn("visitInnerClass() {} - {} - {} - {} @ {}",
          name, outerName, innerName, access, mainClass);
      return;
    }
    ClassNode inner = classBuilder.fromInternalName(name);
    if (inner.equals(mainClass)) {
      // the visitInnerClass callback is called twice: once when visiting the
      // outer class (A in A$B), and once when visiting the A$B class. we
      // shortcut the second case so we don't add the dependency twice.
      return;
    }

    addClassInfo(inner, getClassKind(access));
    ClassNode parent = classBuilder.fromInternalName(outerName);
    addEdge(parent, inner, JavaRelation.INNER_TYPE);
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    classBuilder.buildAnnotationDep(mainClass, desc, visible);
    return null;
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(
      int typeRef, TypePath typePath, String desc, boolean visible) {
    classBuilder.buildAnnotationDep(mainClass, desc, visible);
    return null;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc,
      String signature, String[] exceptions) {

    // the method itself
    MethodNode methodNode = new MethodNode(mainClass.getFQCN(), name);
    builder.addNodeInfo(
        methodNode, MethodInfo.class, new MethodInfo(signature));

    GraphRelation methodRelation = getMemberRelation(
        access, JavaRelation.STATIC_METHOD, JavaRelation.MEMBER_METHOD);
    addEdge(mainClass, methodNode, methodRelation);

    // arguments dependencies
    for (Type t : Type.getArgumentTypes(desc)) {
      addEdge(
          methodNode,
          classBuilder.fromDescriptor(t.getDescriptor()),
          JavaRelation.TYPE);
    }

    // return-type dependency
    ClassNode typeNode =
        classBuilder.fromDescriptor(Type.getReturnType(desc).getDescriptor());
    addEdge(mainClass, typeNode, JavaRelation.READ);

    return asmFactory.buildMethodVisitor(builder, classBuilder, methodNode);
  }

  @Override
  public void visitOuterClass(String owner, String name, String desc) {
    // nothing to do. We use the visitInnerClass callback instead.
  }

  /**
   * Create a source element using the full path-name of the source file.
   * If the mainClass is not a top-level class, assume it is an inner
   * class and its immediate container is not a source file.
   */
  @Override
  public void visitSource(String source, String debug) {
    if (mainClass.getFQCN().contains("$")) {
      // this is an inner class. We use the visitInnerClass() callback instead.
      return;
    }

    // Link the main class to it's containing file
    addEdge(fileNode, mainClass, JavaRelation.CLASSFILE);
  }

  /**
   * Check if the given internal name is an anonymous class. If so, generate
   * the appropriate dependencies.
   *
   * @param name
   */
  private void checkAnonymousType(String name) {
    // anonymous classes names contains a $ followed by a digit
    if (name.contains("$")) {
      String superClass = nextSuperClass(name);

      // recursively check: maybe name is not an anonymous class, but is
      // an innerclass contained into an anonymous class.
      checkAnonymousType(superClass);

      // A digit must follow the $ in the name.
      if (Character.isDigit(name.charAt(name.lastIndexOf('$') + 1))) {
        ClassNode superType = classBuilder.fromInternalName(superClass);
        addEdge(superType, mainClass, JavaRelation.ANONYMOUS_TYPE);
      }
    }
  }

  private String nextSuperClass(String name) {
    int truncateIndex = name.lastIndexOf('$');

    // Handle Kotlin generated classes with multiple dollar sequences.
    while (name.charAt(truncateIndex - 1) == '$') {
      truncateIndex--;
    }
    return name.substring(0, truncateIndex);
  }

  private void addEdge(
      GraphNode head, GraphNode tail, GraphRelation relation) {
    builder.addEdge(
        new GraphEdge(builder.mapNode(head), builder.mapNode(tail), relation));
  }

  private void addClassInfo(ClassNode classNode, ClassKind classKind) {
    builder.addNodeInfo(classNode, ClassInfo.class, new ClassInfo(classKind));
  }

  private ClassKind getClassKind(int access) {
    if (hasOpcode(Opcodes.ACC_INTERFACE, access)) {
      return ClassKind.KIND_INTERFACE;
    }
    if (hasOpcode(Opcodes.ACC_ENUM, access)) {
      return ClassKind.KIND_ENUM;
    }
    if (hasOpcode(Opcodes.ACC_ANNOTATION, access)) {
      return ClassKind.KIND_ANNOTATION;
    }
    if (hasOpcode(Opcodes.ACC_MODULE, access)) {
      return ClassKind.KIND_MODULE;
    }
    if (hasOpcode(Opcodes.ACC_RECORD, access)) {
      return ClassKind.KIND_MODULE;
    }
    return ClassKind.KIND_CLASS;
  }

  private JavaRelation getMemberRelation(
      int access, JavaRelation staticRelation, JavaRelation memberRelation) {
    if (hasOpcode(Opcodes.ACC_STATIC, access)) {
      return staticRelation;
    }
    return memberRelation;
  }

  private boolean hasOpcode(int opCode, int access) {
    return (opCode & access) != 0;
  }
}
