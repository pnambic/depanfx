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
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.java.graph.ClassNode;
import com.pnambic.depanfx.java.graph.FieldNode;
import com.pnambic.depanfx.java.graph.JavaRelation;
import com.pnambic.depanfx.java.graph.MemberNode;
import com.pnambic.depanfx.java.graph.MethodNode;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.TypePath;

/**
 * Implements a visitor of the ASM package, to find the dependencies in a method
 * and build the dependency tree. A single {@link MethodDepLister} is used for
 * each method found in a class.
 *
 * To build the dependencies tree, it calls the methods of a
 * {@link DependenciesListener}.
 *
 * Don't need to override visitLocalVariable() since a local variable must have
 * been instantiated at least once... we should already have its dependency set.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class MethodDepLister extends MethodVisitor {

  private final AsmFactory asmFactory;

  private DepanFxGraphModelBuilder builder;

  private MethodNode methodNode;

  public MethodDepLister(
      AsmFactory asmFactory,
      DepanFxGraphModelBuilder builder,
      MethodNode methodNode) {
    super(asmFactory.getApiLevel());
    this.asmFactory = asmFactory;
    this.builder = builder;
    this.methodNode = methodNode;
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    TypeNameUtil.buildAnnotationDep(builder, methodNode, desc, visible);
    return null;
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(
      int typeRef, TypePath typePath, String desc, boolean visible) {
    TypeNameUtil.buildAnnotationDep(builder, methodNode, desc, visible);
    return null;
  }

  @Override
  public void visitFieldInsn(
      int opcode, String owner, String name, String desc) {
    ClassNode ownerClassNode = TypeNameUtil.fromInternalName(owner);
    MemberNode readFieldNode = new FieldNode(ownerClassNode.getFQCN(), name);
    addEdge(ownerClassNode, readFieldNode, getFieldRelation(opcode));
    addEdge(methodNode, readFieldNode, JavaRelation.READ);
  }

  @Override // ASM-5
  public void visitMethodInsn(
      int opcode, String owner, String name, String desc, boolean itf) {
    ClassNode ownerClassNode = TypeNameUtil.fromInternalName(owner);
    MethodNode calledMethodNode = new MethodNode(
        ownerClassNode.getFQCN(), name);

    addEdge(ownerClassNode, calledMethodNode, getMethodRelation(opcode));
    addEdge(methodNode, calledMethodNode, JavaRelation.CALL);
  }

  @Override // ASM-4
  public void visitMethodInsn(
      int opcode, String owner, String name, String desc) {
    MethodNode calledMethodNode = new MethodNode(
        TypeNameUtil.fromInternalName(owner).getFQCN(), name);
    addEdge(methodNode, calledMethodNode, JavaRelation.CALL);
  }

  @Override
  public void visitTypeInsn(int opcode, String type) {
    addEdge(
        methodNode, TypeNameUtil.fromInternalName(type), JavaRelation.TYPE);
  }

  @Override
  public void visitTryCatchBlock(
      Label start, Label end, Label handler, String type) {

    // No type indicates a finally block, and adds no dependency
    if (null == type) {
      return;
    }
    addEdge(methodNode, TypeNameUtil.fromInternalName(type),
        JavaRelation.ERROR_HANDLING);
  }

  private JavaRelation getFieldRelation(int opCode) {
    if (Opcodes.GETSTATIC == opCode) {
      return JavaRelation.STATIC_FIELD;
    }
    return JavaRelation.MEMBER_FIELD;
  }

  private JavaRelation getMethodRelation(int opCode) {
    if (Opcodes.H_INVOKESTATIC == opCode) {
      return JavaRelation.STATIC_METHOD;
    }
    return JavaRelation.MEMBER_METHOD;
  }

  private void addEdge(
      GraphNode head, GraphNode tail, GraphRelation relation) {
    builder.addEdge(
        new GraphEdge(builder.mapNode(head), builder.mapNode(tail), relation));
  }
}
