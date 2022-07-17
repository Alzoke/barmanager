package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.BrunchAssembler;
import com.example.barmanager.backend.assemblers.BrunchDtoAssembler;
import com.example.barmanager.backend.exceptions.BrunchNotFoundException;
import com.example.barmanager.backend.models.Branch;
import com.example.barmanager.backend.models.BrunchDto;
import com.example.barmanager.backend.repositories.CustomBrunchRepository;
import com.example.barmanager.backend.repositories.IBrunchRepository;
import com.example.barmanager.backend.service.BranchService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class BrunchesController
{
    private final BrunchAssembler brunchAssembler;
    private final BrunchDtoAssembler brunchDtoAssembler;
    private final IBrunchRepository brunchRepository;
    private final BranchService branchService;
    private final CustomBrunchRepository customBrunchRepository;

    public BrunchesController(BrunchAssembler brunchAssembler,
                              BrunchDtoAssembler brunchDtoAssembler,
                              IBrunchRepository brunchRepository, BranchService branchService,
                              CustomBrunchRepository customBrunchRepository)
    {
        this.brunchAssembler = brunchAssembler;
        this.brunchDtoAssembler = brunchDtoAssembler;
        this.brunchRepository = brunchRepository;
        this.branchService = branchService;
        this.customBrunchRepository = customBrunchRepository;
    }

    @GetMapping("/brunches")
    public ResponseEntity<CollectionModel<EntityModel<Branch>>> getAllBrunches()
    {
        return ResponseEntity.ok(brunchAssembler.toCollectionModel(brunchRepository.findAll()));

    }

    @GetMapping("/brunches/{id}")
    public ResponseEntity<EntityModel<Branch>> getBrunch(@PathVariable String id)
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

    @GetMapping("/brunches/getByName")
    public ResponseEntity<EntityModel<BrunchDto>> getBrunchByName(@RequestParam String branchName)
    {
        Optional<Branch> brunchByBrunchName = brunchRepository.findBrunchByBranchName(branchName);
        return brunchByBrunchName
                .map(BrunchDto::new)
                .map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BrunchNotFoundException());
    }

    @PutMapping("/branches/updatedEmployees/add")
    public ResponseEntity<EntityModel<BrunchDto>> addEmployeeToBranch(@RequestParam String employeeToAddId,
                                                                      @RequestParam String branchId)
    {
//        System.out.println(employeeToAddId);
        branchService.addExistingEmployeeToBranch(employeeToAddId, branchId);

        // find and return the updated branch
        return brunchRepository.findById(branchId).map(BrunchDto::new).map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok).orElseThrow(() -> new BrunchNotFoundException());


    }

    @PutMapping("/branches/updatedEmployees/remove")
    public ResponseEntity<EntityModel<BrunchDto>> removeEmployeeFromBranch(@RequestParam String employeeRemoveId,
                                                                         @RequestParam String branchId)
    {
        branchService.removeExistingEmployeeToBranch(employeeRemoveId,branchId);

        // find and return the updated branch
        return brunchRepository.findById(branchId).map(BrunchDto::new).map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok).orElseThrow(() -> new BrunchNotFoundException());
    }

    @PostMapping("/branches")
    public ResponseEntity<EntityModel<Branch>> createBranch(@RequestBody Branch newBranch)
    {
        System.out.println(newBranch);
        newBranch.setEmployeesIds(new ArrayList<>());
        Branch savedBranch = brunchRepository.save(newBranch);

        return ResponseEntity.created(linkTo(methodOn(BrunchesController.class)
                        .getBrunch(savedBranch.getId())).toUri())
                .body(brunchAssembler.toModel(savedBranch));
    }

    @DeleteMapping("/branches/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable String id)
    {
        Branch branchToDelete = brunchRepository.findById(id)
                .orElseThrow(() -> new BrunchNotFoundException(id));
        boolean isDeleted = customBrunchRepository.removeBranch(branchToDelete);
        EntityModel<Branch> branchEntityModel = brunchAssembler.toModel(branchToDelete);

        if ( isDeleted ){
            return ResponseEntity.ok(branchEntityModel);
        }
        else {
            return ResponseEntity.badRequest().body("cant remove desire branch");
        }

    }


}

