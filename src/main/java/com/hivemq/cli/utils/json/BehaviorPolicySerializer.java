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

package com.hivemq.cli.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.hivemq.cli.openapi.hivemq.BehaviorPolicy;
import com.hivemq.cli.openapi.hivemq.BehaviorPolicyBehavior;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * The generated OpenAPI classes do not preserve JSON field ordering.
 * This serializer manually restores the correct order.
 */
public class BehaviorPolicySerializer implements JsonSerializer<BehaviorPolicy> {

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull BehaviorPolicy policy,
            final @NotNull Type typeOfSrc,
            final @NotNull JsonSerializationContext context) {

        final JsonObject object = new JsonObject();
        object.add(BehaviorPolicy.SERIALIZED_NAME_ID, context.serialize(policy.getId()));
        object.add(BehaviorPolicy.SERIALIZED_NAME_CREATED_AT, context.serialize(policy.getCreatedAt()));
        object.add(BehaviorPolicy.SERIALIZED_NAME_LAST_UPDATED_AT, context.serialize(policy.getLastUpdatedAt()));
        object.add(BehaviorPolicy.SERIALIZED_NAME_MATCHING, context.serialize(policy.getMatching()));
        object.add(BehaviorPolicy.SERIALIZED_NAME_DESERIALIZATION, context.serialize(policy.getDeserialization()));
        object.add(BehaviorPolicy.SERIALIZED_NAME_BEHAVIOR, serializePolicyBehavior(policy.getBehavior(), context));
        object.add(BehaviorPolicy.SERIALIZED_NAME_ON_TRANSITIONS, context.serialize(policy.getOnTransitions()));

        return object;
    }

    private @Nullable JsonElement serializePolicyBehavior(
            final @Nullable BehaviorPolicyBehavior policyBehavior, final @NotNull JsonSerializationContext context) {
        if (policyBehavior == null) {
            return null;
        }

        final JsonObject object = new JsonObject();
        object.add(BehaviorPolicyBehavior.SERIALIZED_NAME_ID, context.serialize(policyBehavior.getId()));
        object.add(BehaviorPolicyBehavior.SERIALIZED_NAME_ARGUMENTS, context.serialize(policyBehavior.getArguments()));

        return object;
    }

}
