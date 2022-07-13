package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.BrunchAssembler;
import com.example.barmanager.backend.assemblers.BrunchDtoAssembler;
import com.example.barmanager.backend.exceptions.BrunchNotFoundException;
import com.example.barmanager.backend.exceptions.OrderNotFoundException;
import com.example.barmanager.backend.models.Brunch;
import com.example.barmanager.backend.models.BrunchDto;
import com.example.barmanager.backend.models.OrderDto;
import com.example.barmanager.backend.repositories.IBrunchRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class BrunchesController
{
    private final BrunchAssembler brunchAssembler;
    private final BrunchDtoAssembler brunchDtoAssembler;
    private final IBrunchRepository brunchRepository;

    public BrunchesController(BrunchAssembler brunchAssembler,
                              BrunchDtoAssembler brunchDtoAssembler,
                              IBrunchRepository brunchRepository)
    {
        this.brunchAssembler = brunchAssembler;
        this.brunchDtoAssembler = brunchDtoAssembler;
        this.brunchRepository = brunchRepository;
    }

    @GetMapping("/brunches")
    public ResponseEntity<CollectionModel<EntityModel<Brunch>>> getAllBrunches()
    {
        return ResponseEntity.ok(brunchAssembler.toCollectionModel(brunchRepository.findAll()));

    }

    @GetMapping("/brunches/{id}")
    public ResponseEntity<EntityModel<Brunch>> getBrunch(@PathVariable String id)
    {
        return brunchRepository.findById(id)
                .map(brunch -> brunchAssembler.toModel(brunch)).map(ResponseEntity::ok)
                .orElseThrow(() -> new BrunchNotFoundException(id));
    }

    @GetMapping("/brunches/info")
    public ResponseEntity<CollectionModel<EntityModel<BrunchDto>>> getAllBrunchesDto()
    {
        return ResponseEntity.ok(
                brunchDtoAssembler.toCollectionModel(
                        StreamSupport.stream(brunchRepository.findAll().spliterator(),
                                        false)
                                .map(BrunchDto::new)
                                .collect(Collectors.toList())));
    }

    @GetMapping("/brunches/{id}/info")
    public ResponseEntity<EntityModel<BrunchDto>> getBrunchDto(@PathVariable String id)
    {
        return brunchRepository.findById(id)
                .map(BrunchDto::new)
                .map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BrunchNotFoundException(id));
    }
}

