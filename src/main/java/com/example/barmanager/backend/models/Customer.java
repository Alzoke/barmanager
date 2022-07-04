package com.example.barmanager.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@Document("Customers")
public class Customer extends Person
{
   @Id private String customerId;

   @DocumentReference
   private ArrayList<Order> orders;

/*   public void addOrder(Order order)
   {
      orders.add(order);
   }*/

 /*  public void removeOrder(Order order)
   {
      orders.remove(order);
   }
*/
   public Customer(String name, int idNumber)
   {
      super(name, idNumber);
      orders = new ArrayList<>();
   }

  /* public Customer(String name, int idNumber, ArrayList<Order> orders)
   {
      super(name, idNumber);
      this.orders = orders;
   }*/

   @Override
   public String toString()
   {
      return String.format("Customer: customerId: %s, name:%s, idNumber:%s",getCustomerId(),
              getName(),getIdNumber());
   }
}
