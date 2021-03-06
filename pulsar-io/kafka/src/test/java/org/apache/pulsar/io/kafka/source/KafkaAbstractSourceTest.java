/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.pulsar.io.kafka.source;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.pulsar.io.core.SourceContext;
import org.apache.pulsar.io.kafka.KafkaAbstractSource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;


public class KafkaAbstractSourceTest {

    private static class DummySource extends KafkaAbstractSource<String> {

        @Override
        public String extractValue(ConsumerRecord<String, byte[]> record) {
            return new String(record.value());
        }
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Throwable;
    }

    private static <T extends Exception> void expectThrows(Class<T> expectedType, String expectedMessage, ThrowingRunnable runnable) {
        try {
            runnable.run();
            Assert.fail();
        } catch (Throwable e) {
            if (expectedType.isInstance(e)) {
                T ex = expectedType.cast(e);
                assertEquals(expectedMessage, ex.getMessage());
                return;
            }
            throw new AssertionError("Unexpected exception type, expected " + expectedType.getSimpleName() + " but got " + e);
        }
        throw new AssertionError("Expected exception");
    }

    @Test
    public void testInvalidConfigWillThrownException() throws Exception {
        KafkaAbstractSource source = new DummySource();
        SourceContext ctx = new SourceContext() {
            @Override
            public int getInstanceId() {
                return 0;
            }

            @Override
            public int getNumInstances() {
                return 0;
            }

            @Override
            public void recordMetric(String metricName, double value) {

            }
        };
        Map<String, Object> config = new HashMap<>();
        ThrowingRunnable openAndClose = ()->{
            try {
                source.open(config, ctx);
                fail();
            } finally {
                source.close();
            }
        };
        expectThrows(NullPointerException.class, "Kafka topic is not set", openAndClose);
        config.put("topic", "topic_1");
        expectThrows(NullPointerException.class, "Kafka bootstrapServers is not set", openAndClose);
        config.put("bootstrapServers", "localhost:8080");
        expectThrows(NullPointerException.class, "Kafka consumer group id is not set", openAndClose);
        config.put("groupId", "test-group");
        config.put("fetchMinBytes", -1);
        expectThrows(IllegalArgumentException.class, "Invalid Kafka Consumer fetchMinBytes : -1", openAndClose);
        config.put("fetchMinBytes", 1000);
        config.put("autoCommitEnabled", true);
        config.put("autoCommitIntervalMs", -1);
        expectThrows(IllegalArgumentException.class, "Invalid Kafka Consumer autoCommitIntervalMs : -1", openAndClose);
        config.put("autoCommitIntervalMs", 100);
        config.put("sessionTimeoutMs", -1);
        expectThrows(IllegalArgumentException.class, "Invalid Kafka Consumer sessionTimeoutMs : -1", openAndClose);
        config.put("sessionTimeoutMs", 10000);
        config.put("heartbeatIntervalMs", -100);
        expectThrows(IllegalArgumentException.class, "Invalid Kafka Consumer heartbeatIntervalMs : -100", openAndClose);
        config.put("heartbeatIntervalMs", 20000);
        expectThrows(IllegalArgumentException.class, "Unable to instantiate Kafka consumer", openAndClose);
        config.put("heartbeatIntervalMs", 5000);
        source.open(config, ctx);
        source.close();
    }
}
