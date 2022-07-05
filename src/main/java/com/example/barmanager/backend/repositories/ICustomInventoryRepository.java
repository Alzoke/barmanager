package com.example.barmanager.backend.repositories;

import com.example.barmanager.backend.queryresults.CountByCategory;

import java.util.List;

public interface ICustomInventoryRepository {
    public List<CountByCategory> getCountGroupByCategory();
}
