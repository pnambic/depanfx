/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.java.context;

import java.util.Objects;

public class MemberNodeId extends JavaNodeId {

  private final String fqcn;

  private final String memberName;

  public MemberNodeId(
      JavaNodeKindId nodeKindId, String fqcn, String memberName) {
    super(nodeKindId);
    this.fqcn = fqcn;
    this.memberName = memberName;
  }

  public String getFQCN() {
    return fqcn;
  }

  public String getMemberName() {
    return memberName;
  }

  @Override
  public String getNodeKey() {
    return fqcn + '.' + memberName;
  }

  @Override
  public int hashCode() {
    return hashCodeHelper(Objects.hash(fqcn, memberName));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MemberNodeId)) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    MemberNodeId other = (MemberNodeId) obj;
    return Objects.equals(fqcn, other.fqcn)
        && Objects.equals(memberName, other.memberName);
  }
}
