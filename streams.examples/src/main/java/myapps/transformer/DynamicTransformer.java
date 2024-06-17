package myapps.transformer;

import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.KeyValue;

public class DynamicTransformer implements Transformer<String, String, KeyValue<String, String>> {
    private ProcessorContext context;

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public KeyValue<String, String> transform(String key, String value) {
        String targetTopic;

        if (value.contains("typeA")) {
            targetTopic = "topic-typeA";
        } else if (value.contains("condition2")) {
            targetTopic = "topic-typeB";
        } else {
            targetTopic = "default-topic";
        }

        context.forward(key, value, targetTopic);
        return null; // forwarding the record, no need to return it
    }

    @Override
    public KeyValue<String, String> punctuate(long l) {
        return null;
    }

    @Override
    public void close() {
    }
}
