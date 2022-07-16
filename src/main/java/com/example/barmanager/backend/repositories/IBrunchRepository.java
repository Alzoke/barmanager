package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.models.Branch;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IBrunchRepository extends MongoRepository<Branch,String>
{
    Optional<Branch> findBrunchByBranchName(String name);
}
