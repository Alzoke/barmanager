package com.example.barmanager.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@Document("customers")
public class Customer extends Person
{
   @Id private String customerId;

   //   @DocumentReference
   private ArrayList<String> ordersIds;

   public Customer(int idNumber, String firstName, String lastName)
   {
      super(idNumber, firstName, lastName);
      this.ordersIds = new ArrayList<>();
   }



   @Override
   public String toString()
   {
      return String.format("Customer: customerId: %s, name:%s, idNumber:%s",getCustomerId(),
              getFirstName() +" " + getLastName(),getIdNumber());
   }
}