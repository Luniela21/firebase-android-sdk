// Copyright 2018 Google LLC
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

package com.google.firebase.firestore.testutil;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.auth.CredentialsProvider;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.util.Listener;

/** A Credentials Provider that always returns an empty token */
public class EmptyCredentialsProvider extends CredentialsProvider<User> {

  @Override
  public Task<String> getToken() {
    return Tasks.forResult(null);
  }

  @Override
  public void invalidateToken() {}

  @Override
  public void setChangeListener(Listener<User> changeListener) {
    changeListener.onValue(User.UNAUTHENTICATED);
  }

  @Override
  public void removeChangeListener() {}
}
