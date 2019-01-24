package ru.curs.yelling.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopologyConfiguration {
    @Bean
    public Topology createTopology(StreamsBuilder streamsBuilder) {

        KStream<String, String> foo = streamsBuilder.
                stream("foo",
                        Consumed.with(Serdes.String(), Serdes.String()))

                .mapValues((ValueMapper<String, String>) String::toUpperCase);

        foo.print(Printed.toSysOut());
        foo.to("bar", Produced.with(Serdes.String(), Serdes.String()));


        return streamsBuilder.build();
    }
}
