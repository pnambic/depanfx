/*
 * Copyright 2017 The Depan Project Authors
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
package com.pnambic.depanfx.bytecode;

import com.pnambic.depanfx.bytecode.asm.ClassDepLister;
import com.pnambic.depanfx.bytecode.asm.FieldDepLister;
import com.pnambic.depanfx.bytecode.asm.MethodDepLister;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.java.graph.MethodNode;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;

/**
 * Abstract Factory for Java bytecode analysis elements.
 *
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public interface AsmFactory {

  /**
   * Provide the ASM OpCode Level for this factory.
   *
   * Mostly used within constructors for generated visitors.
   */
  int getApiLevel();

  /**
   * Provide a reusable {@link FieldVisitor} instantce.
   */
  FieldVisitor getGenericFieldVisitor();

  /**
   * Provide a new {@link ClassVisitor} instance.
   */
  ClassVisitor buildClassVisitor(
      DepanFxGraphModelBuilder builder, DocumentNode docNode);

  /**
   * Provide a new {@link MethodVisitor} instance.
   */
  MethodVisitor buildMethodVisitor(
      DepanFxGraphModelBuilder builder, MethodNode methodNode);

  /////////////////////////////////////
  // Provides the standard factory methods.

  public static class Simple implements AsmFactory {

    private final int asmLevel;

    private final FieldDepLister fieldLister;

    public Simple(int asmLevel) {
      this.asmLevel = asmLevel;
      this.fieldLister = new FieldDepLister(asmLevel);
    }

    @Override
    public int getApiLevel() {
      return asmLevel;
    }

    @Override
    public FieldVisitor getGenericFieldVisitor() {
      return fieldLister;
    }

    @Override
    public ClassVisitor buildClassVisitor(
        DepanFxGraphModelBuilder builder, DocumentNode docNode) {
      return new ClassDepLister(this, builder, docNode);
    }

    @Override
    public MethodVisitor buildMethodVisitor(
        DepanFxGraphModelBuilder builder, MethodNode methodNode) {
      return new MethodDepLister(this, builder, methodNode);
    }
  }

  /////////////////////////////////////
  // ASM Levels

  public static final AsmFactory ASM4_FACTORY = new Simple(Opcodes.ASM4);

  public static final AsmFactory ASM5_FACTORY = new Simple(Opcodes.ASM4);

  public static final AsmFactory ASM9_FACTORY = new Simple(Opcodes.ASM9);
}
