package com.tito.kafka.Controller;

import com.tito.kafka.DAO.ItemDAO;
import com.tito.kafka.DAO.SelectItem;
import com.tito.kafka.Model.Item;
import com.tito.kafka.Model.Transaction;
import com.tito.kafka.Model.TransactionItem;
import com.tito.kafka.Model.User;
import com.tito.kafka.Messages.MessageListener;
import com.tito.kafka.Messages.MessageProducer;
import com.tito.kafka.Repository.ItemRepository;
import com.tito.kafka.Repository.TransactionItemRepository;
import com.tito.kafka.Repository.TransactionRepository;
import com.tito.kafka.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final MessageProducer messageProducer;
    private final MessageListener messageListener;
    private final TransactionItemRepository transactionItemRepository;

    @Autowired
    public TransactionController(TransactionRepository transactionRepository, UserRepository userRepository, ItemRepository itemRepository, MessageProducer messageProducer, MessageListener messageListener, TransactionItemRepository transactionItemRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.messageProducer = messageProducer;
        this.messageListener = messageListener;
        this.transactionItemRepository = transactionItemRepository;
    }

    @PostMapping(value = "/order-item")
    public GenericResponse createNewOrder(@RequestBody SelectItem request){
        SelectItem selectItemMessage = messageListener.getSelectItem();
        if (selectItemMessage != null){
            HashMap<String, Object> itemAvailable = isItemAvailableCheck(request, selectItemMessage);
            String itemName = (String) itemAvailable.get("itemName");
            Integer availableAmount = (Integer) itemAvailable.get("availableAmount");
            Boolean isAvailable = (Boolean) itemAvailable.get("isAvailable");
            if (!isAvailable){
                return new GenericResponse(202, "The amount item "+ itemName +" that you selected is not available" +
                        " current amount available is: "+ availableAmount );
            }
        }
        User user = userRepository.findById(request.getUserId()).get();
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        for (ItemDAO i :request.getItems()) {
            Optional<Item> itemById = itemRepository.findById(i.getId());
//            if (!itemById.isPresent()){
//                return new GenericResponse(400, "item id "+i.getId()+" is not found");
//            }
//            if (i.getAmount() > itemById.get().getAmount()){
//                return new GenericResponse(200, "item "+itemById.get().getName()+" is exceed the current available item");
//            }
            TransactionItem transactionItem = new TransactionItem();
            transactionItem.setItem(itemById.get());
            transactionItem.setAmount(i.getAmount());
            Transaction transactionSave = transactionRepository.save(transaction);
            transactionItem.setTransaction(transactionSave);
            transactionItemRepository.save(transactionItem);
        }
        return new GenericResponse(200, "transaction has been added successfully");
    }

    @PostMapping(value = "/select-item")
    public GenericResponse sendMessageSelectItem(@RequestBody SelectItem selectItem) throws InterruptedException {
        SelectItem selectedItem = messageListener.getSelectItem();
        HashMap<String, Object> itemAvailable = isItemAvailableCheck(selectedItem, selectedItem);
        String itemName = (String) itemAvailable.get("itemName");
        Integer availableAmount = (Integer) itemAvailable.get("availableAmount");
        Boolean isAvailable = (Boolean) itemAvailable.get("isAvailable");
        if (!isAvailable){
            return new GenericResponse(202, "The amount item "+ itemName +" that you selected is not available" +
                    " current amount available is: "+ availableAmount );
        }
        messageProducer.sendSelectItemMessage(selectItem);
        logger.info("sending message: ", selectItem);
        messageListener.greetingLatch.await(10, TimeUnit.SECONDS);
        return new GenericResponse<>(200, "select item message has been sent", selectItem);
    }

    @GetMapping(value = "/select-item")
    public GenericResponse getMessageSelectItem(){
        SelectItem selectItem = messageListener.getSelectItem();
        return new GenericResponse<>(200, null, selectItem);
    }

    private Item subtractItemAmount(Item item, int newAmount, int oldAmount){
        item.setAmount(oldAmount - newAmount);
        return item;
    }

    private HashMap<String, Object> isItemAvailableCheck(SelectItem selectingItem, SelectItem selectedItem){
        HashMap<String, Object> isItemAvailableHashMap = new LinkedHashMap<>();
        logger.info("Last selected message: {}", selectedItem.toString());
        Iterable<Item> itemTable = itemRepository.findAll();
        if (!selectingItem.getUserId().equals(selectedItem.getUserId())){
            logger.info("different user");
            for (Item it:itemTable) {
                boolean isSameItemId = selectedItem.getItems().stream().anyMatch(item -> item.getId().equals(it.getId()));
                if (isSameItemId){
                    ItemDAO itemSelected = selectedItem.getItems().stream().filter(item -> item.getId().equals(it.getId())).findAny().get();
                    ItemDAO itemSelecting = selectingItem.getItems().stream().filter(item -> item.getId().equals(it.getId())).findAny().get();
                    int currentAvailableItem = it.getAmount() - itemSelected.getAmount();
                    logger.info("current available item: {}", currentAvailableItem);
                    if (itemSelecting.getAmount() > currentAvailableItem){
                        isItemAvailableHashMap.put("itemName", it.getName());
                        isItemAvailableHashMap.put("availableAmount", currentAvailableItem);
                        isItemAvailableHashMap.put("isAvailable", false);
                        return isItemAvailableHashMap;
                    }
                }
            }
        }
        isItemAvailableHashMap.put("isAvailable", true);
        return isItemAvailableHashMap;
    }


//    @GetMapping(value = "/select-item/from-begining")
//    public ResponseEntity getMessageFromBegining(){
//        SelectItem greeting = messageListener.getSelectItem();
//        return new ResponseEntity<>(greeting, HttpStatus.OK);
//    }
}
