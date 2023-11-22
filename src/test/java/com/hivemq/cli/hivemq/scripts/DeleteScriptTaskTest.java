/*
 * Copyright 2019-present HiveMQ and the HiveMQ Community
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

package com.hivemq.cli.hivemq.scripts;

import com.hivemq.cli.commands.hivemq.datahub.OutputFormatter;
import com.hivemq.cli.openapi.ApiException;
import com.hivemq.cli.openapi.hivemq.DataHubScriptsApi;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DeleteScriptTaskTest {

    private final @NotNull DataHubScriptsApi scriptsApi = mock(DataHubScriptsApi.class);
    private final @NotNull OutputFormatter outputFormatter = mock();

    private static final @NotNull String SCRIPT_ID = "script-1";

    @Test
    void execute_validId_success() {
        final DeleteScriptTask task = new DeleteScriptTask(outputFormatter, scriptsApi, SCRIPT_ID);
        assertTrue(task.execute());
    }

    @Test
    void execute_apiException_printError() throws ApiException {
        final DeleteScriptTask task = new DeleteScriptTask(outputFormatter, scriptsApi, SCRIPT_ID);
        doThrow(ApiException.class).when(scriptsApi).deleteScript(any());
        assertFalse(task.execute());
        verify(outputFormatter, times(1)).printApiException(any(), any());
    }
}
