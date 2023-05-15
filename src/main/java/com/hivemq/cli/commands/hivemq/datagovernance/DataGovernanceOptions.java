package com.hivemq.cli.commands.hivemq.datagovernance;

import com.hivemq.cli.utils.LoggerUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import picocli.CommandLine;

public class DataGovernanceOptions {

    @SuppressWarnings({"NotNullFieldNotInitialized", "unused"}) // will be initialized via default value
    @CommandLine.Option(names = {"-u", "--url"},
                        defaultValue = "http://localhost:8888",
                        description = "The URL of the HiveMQ REST API endpoint (default http://localhost:8888)",
                        order = 1)
    private @NotNull String url;

    @SuppressWarnings({"unused"})
    @CommandLine.Option(names = {"-r", "--rate"},
                        defaultValue = "1500",
                        description = "The rate limit of the REST calls to the HiveMQ API endpoint in requests per second (default 1500/s)",
                        order = 3)
    private double rateLimit;

    @SuppressWarnings({"unused"})
    @CommandLine.Option(names = {"-l", "--log"},
                        defaultValue = "false",
                        description = "Log to $HOME/.mqtt.cli/logs (Configurable through $HOME/.mqtt-cli/config.properties)",
                        order = 2)
    private void initLogging(final boolean logToLogfile) {
        LoggerUtils.turnOffConsoleLogging(logToLogfile);
    }

    public DataGovernanceOptions() {
    }

    @VisibleForTesting
    public DataGovernanceOptions(final @NotNull String url) {
        this.url = url;
    }

    public @NotNull String getUrl() {
        return url;
    }

    public double getRateLimit() {
        return rateLimit;
    }

    @Override
    public @NotNull String toString() {
        return "DataGovernanceOptions{" + "url='" + url + '\'' + ", rateLimit=" + rateLimit + '}';
    }
}
