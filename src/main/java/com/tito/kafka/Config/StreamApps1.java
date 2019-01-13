//package com.tito.kafka.Config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.KeyValue;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.kstream.Consumed;
//import org.apache.kafka.streams.kstream.KStream;
//import org.apache.kafka.streams.kstream.Produced;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class StreamApps1 {
//    @Bean("app1StreamTopology")
//    public KStream<String, Long> startProcessing(@Qualifier("app1StreamBuilder") StreamsBuilder builder) {
//
//        final KStream<String, Long> selectItem = builder.stream("select-item", Consumed.with(Serdes.String(), Serdes.Long()));
//        selectItem.map((key, value) -> { // do something with each msg, square the values in our case
//            return KeyValue.pair(key, value * value);
//        }).to("squared", Produced.with(Serdes.String(), Serdes.Long())); // send downstream to another topic
//
//        return selectItem;
//    }
//
//}
