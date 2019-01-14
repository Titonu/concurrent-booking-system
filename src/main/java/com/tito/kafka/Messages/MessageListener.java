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

    @KafkaListener(topics = "${select-item.topic.name}", containerFactory = "selectItemKafkaListenerContainerFactory")
    public void greetingListener(SelectItem selectItem) {
        System.out.println("Recieved selectItem message: " + selectItem);
        this.selectItem = selectItem;
        this.greetingLatch.countDown();
    }

    public SelectItem getSelectItem(){
        return selectItem;
    }

}
