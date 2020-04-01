// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.firebase.database.core;

import com.google.firebase.database.snapshot.ChildKey;
import com.google.firebase.database.snapshot.Node;
import java.util.ArrayList;

/** A DeferredValueProvider computes the value of a Node only when {@link #node()} is invoked. */
public class DeferredValueProvider implements ValueProvider {

  private final SyncTree syncTree;
  private final Path path;

  DeferredValueProvider(SyncTree syncTree, Path path) {
    this.syncTree = syncTree;
    this.path = path;
  }

  @Override
  public ValueProvider getImmediateChild(ChildKey childKey) {
    Path child = path.child(childKey);
    return new DeferredValueProvider(syncTree, child);
  }

  @Override
  public Node node() {
    return syncTree.calcCompleteEventCache(path, new ArrayList<>());
  }
}
