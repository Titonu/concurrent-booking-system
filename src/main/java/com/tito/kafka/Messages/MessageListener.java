package com.tito.kafka.Messages;

import com.tito.kafka.DAO.SelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class MessageListener {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public CountDownLatch selectItemLatch = new CountDownLatch(1);
    private SelectItem selectItem;

    @KafkaListener(topics = "${select-item.topic.name}", containerFactory = "selectItemKafkaListenerContainerFactory")
    public void selectItemListener(SelectItem selectItem) {
        logger.info("Recieved selectItem message: {}", selectItem);
        this.selectItem = selectItem;
        this.selectItemLatch.countDown();
    }

    public SelectItem getSelectItem(){
        return selectItem;
    }

}
