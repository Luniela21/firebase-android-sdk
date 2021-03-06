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

package com.google.firebase.heartbeatinfo;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DefaultHeartBeatInfoTest {
  private String testSdk = "fire-test";
  private ExecutorService executor;
  private TestOnCompleteListener<Void> storeOnCompleteListener;
  private TestOnCompleteListener<List<HeartBeatResult>> getOnCompleteListener;
  private HeartBeatInfoStorage storage = mock(HeartBeatInfoStorage.class);
  private final Set<HeartBeatConsumer> logSources =
      new HashSet<HeartBeatConsumer>() {
        {
          add(new HeartBeatConsumer() {});
        }
      };
  private DefaultHeartBeatInfo heartBeatInfo;

  @Before
  public void setUp() {
    executor = new ThreadPoolExecutor(0, 1, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    storeOnCompleteListener = new TestOnCompleteListener<>();
    getOnCompleteListener = new TestOnCompleteListener<>();
    heartBeatInfo = new DefaultHeartBeatInfo(() -> storage, logSources, executor);
  }

  @Test
  public void getHeartBeatCode_noHeartBeat() {
    when(storage.shouldSendSdkHeartBeat(anyString(), anyLong())).thenReturn(Boolean.FALSE);
    heartBeatInfo.getHeartBeatCode(testSdk);
    assertThat(heartBeatInfo.getHeartBeatCode(testSdk).getCode()).isEqualTo(0);
  }

  @Test
  public void getHeartBeatCode_sdkHeartBeat() {
    when(storage.shouldSendSdkHeartBeat(anyString(), anyLong())).thenReturn(Boolean.TRUE);
    when(storage.shouldSendGlobalHeartBeat(anyLong())).thenReturn(Boolean.FALSE);
    heartBeatInfo.getHeartBeatCode(testSdk);
    assertThat(heartBeatInfo.getHeartBeatCode(testSdk).getCode()).isEqualTo(1);
  }

  @Test
  public void getHeartBeatCode_globalHeartBeat() {
    when(storage.shouldSendSdkHeartBeat(anyString(), anyLong())).thenReturn(Boolean.FALSE);
    when(storage.shouldSendGlobalHeartBeat(anyLong())).thenReturn(Boolean.TRUE);
    heartBeatInfo.getHeartBeatCode(testSdk);
    assertThat(heartBeatInfo.getHeartBeatCode(testSdk).getCode()).isEqualTo(2);
  }

  @Test
  public void getHeartBeatCode_combinedHeartBeat() {
    when(storage.shouldSendSdkHeartBeat(anyString(), anyLong())).thenReturn(Boolean.TRUE);
    when(storage.shouldSendGlobalHeartBeat(anyLong())).thenReturn(Boolean.TRUE);
    heartBeatInfo.getHeartBeatCode(testSdk);
    assertThat(heartBeatInfo.getHeartBeatCode(testSdk).getCode()).isEqualTo(3);
  }
}
