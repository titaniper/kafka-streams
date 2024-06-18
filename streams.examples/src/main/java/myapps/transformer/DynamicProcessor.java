package myapps.transformer;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
//import org.apache.kafka.streams.processor.To;

public class DynamicProcessor extends AbstractProcessor<String, String> {
    private ProcessorContext context;

    @Override
    public void init(ProcessorContext context) {
        super.init(context);
        this.context = context;
    }

    @Override
    public void process(String key, String value) {
        String targetTopic;

        if (value.contains("typeA")) {
            targetTopic = "dynamic-partition-forwarder-app-typeA";
        } else if (value.contains("typeB")) {
            targetTopic = "dynamic-partition-forwarder-app-typeB";
        } else {
            targetTopic = "dynamic-partition-forwarder-app-typeC";
        }

        // 메시지를 동적으로 지정된 토픽으로 전달, 토폴로지에 등록된 토픽으로만 전송됨
//        context.forward(key, value, To.child(targetTopic));
//        context.forward(key, value, To.child("SinkA"));
        context.forward(key, value, targetTopic);
    }

    @Override
    public void close() {
    }
}