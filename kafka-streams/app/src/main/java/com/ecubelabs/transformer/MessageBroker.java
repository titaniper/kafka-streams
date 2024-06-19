package com.ecubelabs.transformer;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.To;

public class MessageBroker extends AbstractProcessor<String, String> {
    private ProcessorContext context;

    @Override
    public void init(ProcessorContext context) {
        super.init(context);
        this.context = context;
    }

    @Override
    public void process(String key, String value) {
        String targetTopic = value;
//        if (value.contains("typeA")) {
//            targetTopic = "dynamic-partition-forwarder-app-typeA";
//        } else if (value.contains("typeB")) {
//            targetTopic = "dynamic-partition-forwarder-app-typeB";
//        } else {
//            targetTopic = "dynamic-partition-forwarder-app-typeC";
//        }
        context.forward(key, value, To.child(targetTopic));
    }

    @Override
    public void close() {
    }
}