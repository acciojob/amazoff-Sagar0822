package com.driver;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    Map<String, Order>orderDb= new HashMap<>(); //orderId & order
    Map<String, DeliveryPartner>deliveryPartnerDb = new HashMap<>(); //partnerId & deliverypartner
    Map<String, String>orderPartnerDb = new HashMap<>();
    Map<String, List<String>>partnerOrderDb = new HashMap<>();

    public void addOrder(Order order){
        orderDb.put(order.getId() , order);
    }
    public void addPartner(String partnerId){
        deliveryPartnerDb.put(partnerId , new DeliveryPartner(partnerId));
    }
    public void addOrderPartnerPair(String orderId , String partnerId){
        if(orderDb.containsKey(orderId) && deliveryPartnerDb.containsKey(partnerId)){ //check
            orderPartnerDb.put(orderId , partnerId);

            List<String> currentOrders = new ArrayList<>();

            if(partnerOrderDb.containsKey(partnerId)){
                currentOrders = partnerOrderDb.get(partnerId);
            }
            currentOrders.add(orderId);
            partnerOrderDb.put(partnerId, currentOrders);

            //increase the numbers of order of partners
            DeliveryPartner deliveryPartner = deliveryPartnerDb.get(partnerId);
            deliveryPartner.setNumberOfOrders(currentOrders.size());
        }
    }
    public Order getOrderById(String orderId){
        return orderDb.get(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerDb.get(partnerId);
    }
    public int getOrderCountByPartnerId(String partnerId){
        return partnerOrderDb.get(partnerId).size();
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        return partnerOrderDb.get(partnerId);
    }
    public List<String> getAllOrders() {
        List<String> orders = new ArrayList<>();
        for (String order : orderDb.keySet()) {
            orders.add(order);
        }
        return orders;
    }
    public int getCountOfUnassignedOrders(){
         return orderDb.size() - orderPartnerDb.size();
    }
    public int getOrdersLeftAfterGivenTimeByPartnerId(int time, String partnerId){
        int count =0 ;
        List<String> orders = partnerOrderDb.get(partnerId);

        for(String orderId : orders){
            int deliveryTime = orderDb.get(orderId).getDeliveryTime();
            if(deliveryTime > time)
                count++;
        }
        return count;
    }
    public int getLastDeliveryTimeByPartnerId(String partnerId){ //time store in int form in DB
        int maxTime = 0;
        List<String> orders = partnerOrderDb.get(partnerId);
        for(String orderId : orders){
            int currentTime = orderDb.get(orderId).getDeliveryTime();
            maxTime = Math.max(maxTime,currentTime);
        }
        return maxTime;
    }
    public void deletePartnerById(String partnerId){
        deliveryPartnerDb.remove(partnerId);

        List<String> listOfOrders = partnerOrderDb.get(partnerId);
        partnerOrderDb.remove(partnerId);

        for(String order : listOfOrders)
            orderPartnerDb.remove(order);
    }
    public void deleteOrderById(String orderId){
        orderDb.remove(orderId);

        String partnerId = orderPartnerDb.get(orderId);
        orderPartnerDb.remove(orderId);

        partnerOrderDb.get(partnerId).remove(orderId);

        deliveryPartnerDb.get(partnerId).setNumberOfOrders(partnerOrderDb.get(partnerId).size());
    }
}
