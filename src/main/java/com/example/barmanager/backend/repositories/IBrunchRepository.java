package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Brunch;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IBrunchRepository extends MongoRepository<Brunch,String>
{
}
