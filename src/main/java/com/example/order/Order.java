package com.example.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

import javax.persistence.*;


@Entity
@Table(name="ORDER_TABLE")
public class Order {

    @Id  @GeneratedValue
    Long Id;
    Long productId;
    String ProductName;
    int qty;

    @PostPersist
    public  void onPostPersist(){
        OrderPlaced orderPlaced = new OrderPlaced();
        orderPlaced.setOrderId(this.getId());
        orderPlaced.setProductId(this.getProductId());
        orderPlaced.setProductName(this.getProductName());
        orderPlaced.setQty(this.getQty());
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
           json = objectMapper.writeValueAsString(orderPlaced);
            } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
    }
        //System.out.println(json);
        Processor processor = OrderApplication.applicationContext.getBean(Processor.class);
        MessageChannel outputChannel = processor.output();

        outputChannel.send(MessageBuilder
                .withPayload(json)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());

    }



    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }


}



