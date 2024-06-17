package myapps.transformer;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;

public class DynamicProcessor extends AbstractProcessor<String, String> {
    @Override
    public void init(ProcessorContext context) {
        super.init(context);
    }

    @Override
    public void process(String key, String value) {
        String targetTopic;

        if (value.contains("typeA")) {
            targetTopic = "topic-typeA";
        } else if (value.contains("typeB")) {
            targetTopic = "topic-typeB";
        } else {
            targetTopic = "default-topic";
        }

        // 메시지를 동적으로 지정된 토픽으로 전달, 토폴로지에 등록된 토픽으로만 전송됨
        context().forward(key, value, targetTopic);
        context().forward(key, value, "SinkA");
    }

    @Override
    public void close() {
    }
}