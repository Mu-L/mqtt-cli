package com.hivemq.cli.hivemq.schemas;

import com.hivemq.cli.commands.hivemq.datagovernance.OutputFormatter;
import com.hivemq.cli.openapi.ApiException;
import com.hivemq.cli.openapi.hivemq.Schema;
import com.hivemq.cli.openapi.hivemq.SchemasApi;
import org.bouncycastle.util.encoders.Base64;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateSchemaTaskTest {

    private final @NotNull SchemasApi schemasApi = mock(SchemasApi.class);
    private final @NotNull OutputFormatter outputFormatter = mock(OutputFormatter.class);

    @SuppressWarnings("FieldCanBeLocal")
    private final @NotNull String JSON_SCHEMA_DEFINITION = "{ \"type\": \"object\" }";

    // test.proto:
    // ```
    // syntax = "proto3";
    // message Test {}
    // ```
    // Created with `protoc -o /dev/stdout | base64`
    @SuppressWarnings("FieldCanBeLocal")
    private final @NotNull String PROTOBUF_SCHEMA_DEFINITION = "ChwKCnRlc3QucHJvdG8iBgoEVGVzdGIGcHJvdG8z";

    @Test
    void execute_validJsonSchema_created() throws ApiException {
        final CreateSchemaTask task = new CreateSchemaTask(outputFormatter,
                schemasApi,
                "test-1",
                "json",
                null,
                false,
                ByteBuffer.wrap(JSON_SCHEMA_DEFINITION.getBytes(StandardCharsets.UTF_8)));

        final ArgumentCaptor<Schema> schemaCaptor = ArgumentCaptor.forClass(Schema.class);

        assertTrue(task.execute());
        verify(schemasApi, times(1)).createSchema(schemaCaptor.capture());
        final Schema createdSchema = schemaCaptor.getValue();
        assertEquals("test-1", createdSchema.getId());
        assertEquals("json", createdSchema.getType());
        final String createdSchemaDefinition = new String(Base64.decode(createdSchema.getSchemaDefinition()));
        assertEquals(JSON_SCHEMA_DEFINITION, createdSchemaDefinition);
        assertNull(createdSchema.getArguments());
    }

    @Test
    void execute_validProtobufSchema_created() throws ApiException {
        final CreateSchemaTask task = new CreateSchemaTask(outputFormatter,
                schemasApi,
                "test-1",
                "protobuf",
                "Test",
                true,
                ByteBuffer.wrap(Base64.decode(PROTOBUF_SCHEMA_DEFINITION)));

        final ArgumentCaptor<Schema> schemaCaptor = ArgumentCaptor.forClass(Schema.class);

        assertTrue(task.execute());
        verify(schemasApi, times(1)).createSchema(schemaCaptor.capture());
        final Schema createdSchema = schemaCaptor.getValue();
        assertEquals("test-1", createdSchema.getId());
        assertEquals("protobuf", createdSchema.getType());
        assertEquals(PROTOBUF_SCHEMA_DEFINITION, createdSchema.getSchemaDefinition());
        assertNotNull(createdSchema.getArguments());
        assertEquals(createdSchema.getArguments().get("messageType"), "Test");
        assertEquals(createdSchema.getArguments().get("allowUnknownFields"), "true");
    }

    @Test
    void execute_exceptionThrown_printError() throws ApiException {
        final CreateSchemaTask task = new CreateSchemaTask(outputFormatter,
                schemasApi,
                "test-1",
                "json",
                null,
                false,
                ByteBuffer.wrap(new byte[]{}));
        when(schemasApi.getSchema("test-1", null)).thenThrow(ApiException.class);

        assertFalse(task.execute());
        verify(outputFormatter).printApiException(any(), any());
    }
}
