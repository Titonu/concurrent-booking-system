package com.tito.kafka.Messages;

import com.tito.kafka.DAO.SelectItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    private final KafkaTemplate<String, SelectItem> selectItemKafkaTemplate;

    @Value(value = "${message.topic.name}")
    private String topicName;

    @Value(value = "${select-item.topic.name}")
    private String selectItemTopic;

    @Autowired
    public MessageProducer(KafkaTemplate<String, SelectItem> selectItemKafkaTemplate) {
        this.selectItemKafkaTemplate = selectItemKafkaTemplate;
    }

    public void sendSelectItemMessage(SelectItem selectItem) {
        selectItemKafkaTemplate.send(this.selectItemTopic, selectItem);
    }

}
