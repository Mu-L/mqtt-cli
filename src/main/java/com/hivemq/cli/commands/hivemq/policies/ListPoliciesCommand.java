package com.hivemq.cli.commands.hivemq.policies;

import com.hivemq.cli.commands.hivemq.datagovernance.DataGovernanceOptions;
import com.hivemq.cli.commands.hivemq.datagovernance.OutputFormatter;
import com.hivemq.cli.hivemq.policies.ListPoliciesTask;
import com.hivemq.cli.openapi.hivemq.PoliciesApi;
import com.hivemq.cli.rest.HiveMQRestService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "ls", description = "List all existing policies", mixinStandardHelpOptions = true)
public class ListPoliciesCommand implements Callable<Integer> {

    @SuppressWarnings("unused")
    @CommandLine.Option(names = {"-t", "--topic"}, description = "list only policies that match a topic")
    private @Nullable String topic;

    @SuppressWarnings("unused")
    @CommandLine.Option(names = {"-i", "--id"}, description = "filter by policy id")
    private @Nullable String @Nullable [] policyIds;

    @SuppressWarnings("unused")
    @CommandLine.Option(names = {"-s", "--schema-id"}, description = "filter by policies containing a schema id")
    private @Nullable String @Nullable [] schemaIds;

    @CommandLine.Mixin
    private final @NotNull DataGovernanceOptions dataGovernanceOptions = new DataGovernanceOptions();

    private final @NotNull OutputFormatter formatter;

    @Inject
    public ListPoliciesCommand(final @NotNull OutputFormatter outputFormatter) {
        this.formatter = outputFormatter;
    }

    @Override
    public @NotNull Integer call() {
        Logger.trace("Command {}", this);

        final PoliciesApi policiesApi =
                HiveMQRestService.getPoliciesApi(dataGovernanceOptions.getUrl(), dataGovernanceOptions.getRateLimit());

        final ListPoliciesTask listPoliciesTask =
                new ListPoliciesTask(formatter, policiesApi, topic, policyIds, schemaIds);

        if (listPoliciesTask.execute()) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public @NotNull String toString() {
        return "ListPoliciesCommand{" +
                "topic='" +
                topic +
                '\'' +
                ", policyIds=" +
                Arrays.toString(policyIds) +
                ", schemaIds=" +
                Arrays.toString(schemaIds) +
                ", dataGovernanceOptions=" +
                dataGovernanceOptions +
                ", formatter=" +
                formatter +
                '}';
    }
}
