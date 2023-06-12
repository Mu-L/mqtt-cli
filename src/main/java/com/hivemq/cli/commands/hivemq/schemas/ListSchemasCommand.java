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

package com.hivemq.cli.commands.hivemq.schemas;

import com.hivemq.cli.MqttCLIMain;
import com.hivemq.cli.commands.hivemq.datagovernance.DataGovernanceOptions;
import com.hivemq.cli.commands.hivemq.datagovernance.OutputFormatter;
import com.hivemq.cli.converters.SchemaTypeConverter;
import com.hivemq.cli.hivemq.schemas.ListSchemasTask;
import com.hivemq.cli.openapi.hivemq.SchemasApi;
import com.hivemq.cli.rest.HiveMQRestService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "list",
                     description = "List all existing schemas",
                     sortOptions = false,
                     synopsisHeading = "%n@|bold Usage:|@  ",
                     descriptionHeading = "%n",
                     optionListHeading = "%n@|bold Options:|@%n",
                     commandListHeading = "%n@|bold Commands:|@%n",
                     versionProvider = MqttCLIMain.CLIVersionProvider.class,
                     mixinStandardHelpOptions = true)
public class ListSchemasCommand implements Callable<Integer> {

    @SuppressWarnings("unused")
    @CommandLine.Option(names = {"-t", "--type"},
                        converter = SchemaTypeConverter.class,
                        description = "Filter by schema type")
    private @Nullable String @Nullable [] schemaTypes;

    @SuppressWarnings("unused")
    @CommandLine.Option(names = {"-i", "--id"}, description = "Filter by schema id")
    private @Nullable String @Nullable [] schemaIds;

    @CommandLine.Mixin
    private final @NotNull DataGovernanceOptions dataGovernanceOptions = new DataGovernanceOptions();

    private final @NotNull OutputFormatter outputFormatter;
    private final @NotNull HiveMQRestService hiveMQRestService;

    @Inject
    public ListSchemasCommand(
            final @NotNull HiveMQRestService hiveMQRestService, final @NotNull OutputFormatter outputFormatter) {
        this.outputFormatter = outputFormatter;
        this.hiveMQRestService = hiveMQRestService;
    }

    @Override
    public @NotNull Integer call() {
        Logger.trace("Command {}", this);

        final SchemasApi schemasApi =
                hiveMQRestService.getSchemasApi(dataGovernanceOptions.getUrl(), dataGovernanceOptions.getRateLimit());

        final ListSchemasTask listSchemasTask =
                new ListSchemasTask(outputFormatter, schemasApi, schemaTypes, schemaIds);

        if (listSchemasTask.execute()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public @NotNull String toString() {
        return "ListSchemasCommand{" +
                "schemaTypes=" +
                Arrays.toString(schemaTypes) +
                ", schemaIds=" +
                Arrays.toString(schemaIds) +
                ", dataGovernanceOptions=" +
                dataGovernanceOptions +
                ", outputFormatter=" +
                outputFormatter +
                ", hiveMQRestService=" +
                hiveMQRestService +
                '}';
    }
}
