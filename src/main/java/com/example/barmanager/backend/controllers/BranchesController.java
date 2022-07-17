package com.example.barmanager.backend.controllers;

import com.example.barmanager.backend.assemblers.BranchAssembler;
import com.example.barmanager.backend.assemblers.BranchDtoAssembler;
import com.example.barmanager.backend.exceptions.BranchNotFoundException;
import com.example.barmanager.backend.models.Branch;
import com.example.barmanager.backend.models.BranchDto;
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
public class BranchesController
{
    private final BranchAssembler brunchAssembler;
    private final BranchDtoAssembler brunchDtoAssembler;
    private final IBrunchRepository brunchRepository;
    private final BranchService branchService;
    private final CustomBrunchRepository customBrunchRepository;

    public BranchesController(BranchAssembler brunchAssembler,
                              BranchDtoAssembler brunchDtoAssembler,
                              IBrunchRepository brunchRepository, BranchService branchService,
                              CustomBrunchRepository customBrunchRepository)
    {
        this.brunchAssembler = brunchAssembler;
        this.brunchDtoAssembler = brunchDtoAssembler;
        this.brunchRepository = brunchRepository;
        this.branchService = branchService;
        this.customBrunchRepository = customBrunchRepository;
    }

    @GetMapping("/branches")
    public ResponseEntity<CollectionModel<EntityModel<Branch>>> getAllBrunches()
    {
        return ResponseEntity.ok(brunchAssembler.toCollectionModel(brunchRepository.findAll()));

    }

    @GetMapping("/branches/{id}")
    public ResponseEntity<EntityModel<Branch>> getBrunch(@PathVariable String id)
    {
        return brunchRepository.findById(id)
                .map(brunch -> brunchAssembler.toModel(brunch)).map(ResponseEntity::ok)
                .orElseThrow(() -> new BranchNotFoundException(id));
    }

    @GetMapping("/branches/info")
    public ResponseEntity<CollectionModel<EntityModel<BranchDto>>> getAllBrunchesDto()
    {
        return ResponseEntity.ok(
                brunchDtoAssembler.toCollectionModel(
                        StreamSupport.stream(brunchRepository.findAll().spliterator(),
                                        false)
                                .map(BranchDto::new)
                                .collect(Collectors.toList())));
    }

    @GetMapping("/branches/{id}/info")
    public ResponseEntity<EntityModel<BranchDto>> getBrunchDto(@PathVariable String id)
    {
        return brunchRepository.findById(id)
                .map(BranchDto::new)
                .map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BranchNotFoundException(id));
    }

    @GetMapping("/branches/getByName")
    public ResponseEntity<EntityModel<BranchDto>> getBrunchByName(@RequestParam String branchName)
    {
        Optional<Branch> brunchByBrunchName = brunchRepository.findBrunchByBranchName(branchName);
        return brunchByBrunchName
                .map(BranchDto::new)
                .map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BranchNotFoundException());
    }

    @PutMapping("/branches/updatedEmployees/add")
    public ResponseEntity<EntityModel<BranchDto>> addEmployeeToBranch(@RequestParam String employeeToAddId,
                                                                      @RequestParam String branchId)
    {
//        System.out.println(employeeToAddId);
        branchService.addExistingEmployeeToBranch(employeeToAddId, branchId);

        // find and return the updated branch
        return brunchRepository.findById(branchId).map(BranchDto::new).map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok).orElseThrow(() -> new BranchNotFoundException());


    }

    @PutMapping("/branches/updatedEmployees/remove")
    public ResponseEntity<EntityModel<BranchDto>> removeEmployeeFromBranch(@RequestParam String employeeRemoveId,
                                                                           @RequestParam String branchId)
    {
        branchService.removeExistingEmployeeToBranch(employeeRemoveId,branchId);

        // find and return the updated branch
        return brunchRepository.findById(branchId).map(BranchDto::new).map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok).orElseThrow(() -> new BranchNotFoundException());
    }

    @PostMapping("/branches")
    public ResponseEntity<EntityModel<Branch>> createBranch(@RequestBody Branch newBranch)
    {
        System.out.println(newBranch);
        newBranch.setEmployeesIds(new ArrayList<>());
        Branch savedBranch = brunchRepository.save(newBranch);

        return ResponseEntity.created(linkTo(methodOn(BranchesController.class)
                        .getBrunch(savedBranch.getId())).toUri())
                .body(brunchAssembler.toModel(savedBranch));
    }

    @DeleteMapping("/branches/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable String id)
    {
        Branch branchToDelete = brunchRepository.findById(id)
                .orElseThrow(() -> new BranchNotFoundException(id));
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

