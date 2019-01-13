package com.tito.kafka.Controller;

import com.tito.kafka.DAO.SelectItem;
import com.tito.kafka.Model.Item;
import com.tito.kafka.Model.Transaction;
import com.tito.kafka.Model.TransactionItem;
import com.tito.kafka.Model.User;
import com.tito.kafka.Messages.MessageListener;
import com.tito.kafka.Messages.MessageProducer;
import com.tito.kafka.Repository.ItemRepository;
import com.tito.kafka.Repository.TransactionRepository;
import com.tito.kafka.Repository.UserRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final MessageProducer messageProducer;
    private final MessageListener messageListener;

    @Autowired
    public TransactionController(TransactionRepository transactionRepository, UserRepository userRepository, ItemRepository itemRepository, MessageProducer messageProducer, MessageListener messageListener) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.messageProducer = messageProducer;
        this.messageListener = messageListener;
    }

    private Transaction setNewTransaction(User user, List<TransactionItem> transactionItems, Integer amount){
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionItems(transactionItems);
        transaction.setAmount(amount);
        return transaction;
    }

    private Item subtractItemAmount(Item item, int newAmount, int oldAmount){
        item.setAmount(oldAmount - newAmount);
        return item;
    }

    @PostMapping(value = "/select-item")
    public ResponseEntity sendMessageSelectItem(@RequestBody SelectItem selectItem) throws InterruptedException {
        messageProducer.sendSelectItemMessage(selectItem);
        logger.info("sending message: ", selectItem.toString());
        messageListener.greetingLatch.await(10, TimeUnit.SECONDS);
        return new ResponseEntity<>("select item message has been added", HttpStatus.OK);
    }

    @GetMapping(value = "/select-item")
    public ResponseEntity getMessageSelectItem(){
        SelectItem greeting = messageListener.getSelectItem();
        return new ResponseEntity<>(greeting, HttpStatus.OK);
    }

    @GetMapping(value = "/select-item/from-begining")
    public ResponseEntity getMessageFromBegining(){
        SelectItem greeting = messageListener.getSelectItem();
        JSONObject jsonObject = messageListener.getJsonObject();
        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
    }
}
