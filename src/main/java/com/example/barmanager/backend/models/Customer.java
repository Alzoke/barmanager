package com.example.barmanager.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document("Customers")
public class Customer extends Person
{
   @Id private String customerId;

   public Customer(String name, int idNumber)
   {
      super(name, idNumber);
   }

   @Override
   public String toString()
   {
      return String.format("Customer: customerId: %s, name:%s, idNumber:%s",getCustomerId(),
              getName(),getIdNumber());
   }
}
