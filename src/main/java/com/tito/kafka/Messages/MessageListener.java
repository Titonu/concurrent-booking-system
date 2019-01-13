package com.tito.kafka.Messages;

import com.tito.kafka.DAO.SelectItem;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class MessageListener {
    public CountDownLatch greetingLatch = new CountDownLatch(1);
    private SelectItem selectItem;
    private JSONObject jsonObject;


    @KafkaListener(topics = "${select-item.topic.name}", containerFactory = "filterKafkaListenerContainerFactory")
    public void greetingListener(SelectItem selectItem) {
        System.out.println("Recieved selectItem message: " + selectItem.toString());
        this.selectItem = selectItem;
        this.greetingLatch.countDown();
    }

    @KafkaListener(id = "select-item-api", topicPartitions = { @TopicPartition(topic = "schema.select-item",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")),
                            @TopicPartition(topic = "data.select-item", partitions = { "0" })})
    public void receiveMessage(String message) {
        try {
            JSONObject incomingJsonObject = new JSONObject(message);
            if (!incomingJsonObject.isNull("data")) {
                this.jsonObject = incomingJsonObject;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SelectItem getSelectItem(){
        return selectItem;
    }
    public JSONObject getJsonObject(){
        return jsonObject;
    }

}
