package ru.curs.counting;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import ru.curs.counting.cli.GUI;
import ru.curs.counting.configuration.KafkaConfiguration;
import ru.curs.counting.configuration.TopologyConfiguration;
import ru.curs.counting.transformer.CountingTransformer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTopology {


    TopologyTestDriver topologyTestDriver;

    @BeforeEach
    public void setUp() throws IOException {
        KafkaStreamsConfiguration config = new KafkaConfiguration().getStreamsConfig();
        StreamsBuilder sb = new StreamsBuilder();
        Topology topology = new TopologyConfiguration(
                new GUI(VirtualScreenBuilder.screen())).createTopology(sb);
        topologyTestDriver = new TopologyTestDriver(
                topology, config.asProperties());
    }

    void processValue(String key, String value) {
        ConsumerRecordFactory<String, String> factory = new ConsumerRecordFactory<>(Serdes.String().serializer(),
                Serdes.String().serializer());
        topologyTestDriver.pipeInput(factory.create("in", key, value));
    }

    @Test
    void testTopology() {
        processValue("a", "hello, world");
        processValue("a", "hello, #jpoint");
        processValue("a", "#jpoint is great");
        processValue("b", "hello, jokerconf");
        processValue("b", "hello, world");
        processValue("b", "#jpoint");

        KeyValueStore<String, Integer> store = topologyTestDriver.getKeyValueStore(CountingTransformer.STORE_NAME);
        assertEquals(2, store.get("a").intValue());
        assertEquals(1, store.get("b").intValue());

    }
}
