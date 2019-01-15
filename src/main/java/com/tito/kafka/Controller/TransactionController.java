package com.tito.kafka.Controller;

import com.tito.kafka.DAO.ItemDAO;
import com.tito.kafka.DAO.SelectItem;
import com.tito.kafka.Messages.MessageListener;
import com.tito.kafka.Messages.MessageProducer;
import com.tito.kafka.Model.Item;
import com.tito.kafka.Model.Transaction;
import com.tito.kafka.Model.TransactionItem;
import com.tito.kafka.Model.User;
import com.tito.kafka.Repository.ItemRepository;
import com.tito.kafka.Repository.TransactionItemRepository;
import com.tito.kafka.Repository.TransactionRepository;
import com.tito.kafka.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
        SelectItem selectedItem = messageListener.getSelectItem();
        if (selectedItem != null){
            HashMap<String, Object> itemAvailable = isItemAvailableCheck(request, selectedItem);
            String itemName = (String) itemAvailable.get("itemName");
            Integer availableAmount = (Integer) itemAvailable.get("availableAmount");
            Boolean isAvailable = (Boolean) itemAvailable.get("isAvailable");
            if (!isAvailable){
                return new GenericResponse(202, "The amount item "+ itemName +" that you selected is not available" +
                        " current amount available is: "+ availableAmount );
            }
        }
        createNewTransaction(request.getItems(), request.getUserId());
        return new GenericResponse(200, "transaction has been added successfully");
    }

    @Transactional
    void createNewTransaction(List<ItemDAO> items, Integer userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new GenericException(400, "user id: "+userId+" is not found"));
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        for (ItemDAO i :items) {
            Item itemById = itemRepository.findById(i.getId()).orElseThrow(
                    ()-> new GenericException(400, "item id: "+i.getId()+" is not found"));
            TransactionItem transactionItem = new TransactionItem();
            transactionItem.setItem(itemById);
            transactionItem.setAmount(i.getAmount());
            Transaction transactionSave = transactionRepository.save(transaction);
            transactionItem.setTransaction(transactionSave);
            transactionItemRepository.save(transactionItem);
            //update amount in database
            itemById.setAmount(itemById.getAmount() - i.getAmount());
            itemRepository.save(itemById);
        }
    }

    @PostMapping(value = "/select-item")
    public GenericResponse sendMessageSelectItem(@RequestBody SelectItem selectItem) throws InterruptedException {
        SelectItem selectedItem = messageListener.getSelectItem();
        Boolean isAvailable = true;
        //if there is not selected item before
        if (selectedItem != null) {
            HashMap<String, Object> itemAvailable = isItemAvailableCheck(selectItem, selectedItem);
            isAvailable = (Boolean) itemAvailable.get("isAvailable");
        }
        logger.info("is available: {}", isAvailable);
        if (!isAvailable){
            return new GenericResponse<>(202, "The selected item message is not sent, " +
                    "because the selected item is exceed the amount item that available");
        } else{
            messageProducer.sendSelectItemMessage(selectItem);
            logger.info("sending message: ", selectItem);
            messageListener.selectItemLatch.await(5, TimeUnit.SECONDS);
        }
        return new GenericResponse<>(200, "select item message has been sent", selectItem);
    }

    @GetMapping(value = "/select-item")
    public GenericResponse getMessageSelectItem(){
        //get selected item before
        SelectItem selectItem = messageListener.getSelectItem();
        return new GenericResponse<>(200, null, selectItem);
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

}
