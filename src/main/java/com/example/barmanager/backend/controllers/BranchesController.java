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
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller which is responsible for managing
 * and routing http requests for the branches route
 */
@RestController
public class BranchesController {
    private final BranchAssembler brunchAssembler;
    private final BranchDtoAssembler branchDtoAssembler;
    private final IBrunchRepository branchRepository;
    private final BranchService branchService;
    private final CustomBrunchRepository customBrunchRepository;

    public BranchesController(BranchAssembler brunchAssembler,
                              BranchDtoAssembler brunchDtoAssembler,
                              IBrunchRepository brunchRepository, BranchService branchService,
                              CustomBrunchRepository customBrunchRepository) {
        this.brunchAssembler = brunchAssembler;
        this.branchDtoAssembler = brunchDtoAssembler;
        this.branchRepository = brunchRepository;
        this.branchService = branchService;
        this.customBrunchRepository = customBrunchRepository;
    }

    /**
     * function that handle Get request for get all exiting branches
     *
     * @return Collection model of entities model of Branch with status code "ok"
     */
    @GetMapping("/branches")
    public ResponseEntity<CollectionModel<EntityModel<Branch>>> getAllBrunches() {
        return ResponseEntity.ok(brunchAssembler.toCollectionModel(branchRepository.findAll()));
    }

    /**
     * function which handle get request and receiving single branch by id in the DB
     * @param id - represents id of the requested branch in DB
     * @return return Entity model of requested branch
     */
    @GetMapping("/branches/{id}")
    public ResponseEntity<EntityModel<Branch>> getBrunch(@PathVariable String id) {
        return branchRepository.findById(id)
                .map(brunchAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new BranchNotFoundException(id));
    }

    /**
     * function that handle Get request for get all exiting branches (as DTOs)
     * @return Collection model of entities model of Dtos Branches with status code "ok"
     */
    @GetMapping("/branches/info")
    public ResponseEntity<CollectionModel<EntityModel<BranchDto>>> getAllBrunchesDto() {
        return ResponseEntity.ok(
                branchDtoAssembler.toCollectionModel(
                        StreamSupport.stream(branchRepository.findAll().spliterator(),
                                        false)
                                .map(BranchDto::new)
                                .collect(Collectors.toList())));
    }

    /**
     * function which handle get request get single DTO branch
     *
     * @param id - represents id of the requested branch in DB
     * @return return Entity model of requested branch as DTO
     */
    @GetMapping("/branches/{id}/info")
    public ResponseEntity<EntityModel<BranchDto>> getBrunchDto(@PathVariable String id) {
        return branchRepository.findById(id)
                .map(BranchDto::new)
                .map(branchDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BranchNotFoundException(id));
    }

    /**
     * function that handle Get request for getting  branch by given branch name (as DTO)
     *
     * @param branchName - name of requested branch
     * @return requested branch that fits to given branch name as entity model of branch DTO
     * or throws BranchNotFoundException if can't find such branch
     */
    @GetMapping("/branches/getByName")
    public ResponseEntity<EntityModel<BranchDto>> getBrunchByName(@RequestParam String branchName) {
        Optional<Branch> brunchByBrunchName = branchRepository.findBrunchByBranchName(branchName);
        return brunchByBrunchName
                .map(BranchDto::new)
                .map(branchDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(BranchNotFoundException::new);
    }

    /**
     * function that handle Put request for adding employee to branch
     *
     * @param employeeToAddId id of requested employee
     * @param branchId        id of the requested branch
     * @return updated branch after adding employee
     */
    @PutMapping("/branches/updatedEmployees/add")
    public ResponseEntity<?> addEmployeeToBranch(@RequestParam String employeeToAddId,
                                                                      @RequestParam String branchId) {
        branchService.addExistingEmployeeToBranch(employeeToAddId, branchId);

        // find and return the updated branch
        return branchRepository.findById(branchId).map(BranchDto::new).map(branchDtoAssembler::toModel)
                .map(branchDto -> {
                    try {
                        return ResponseEntity.created(new URI(
                                branchDto.getRequiredLink(IanaLinkRelations.SELF).getHref())).body(branchDto);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return ResponseEntity.internalServerError().body("Something went wrong while updating the branch");
                }).orElseThrow(BranchNotFoundException::new);
    }

    /**
     * function that handle Put request for removing employee from branch
     *
     * @param employeeRemoveId id of requested employee
     * @param branchId         id of the requested branch
     * @return updated branch after removing employee
     */
    @PutMapping("/branches/updatedEmployees/remove")
    public ResponseEntity<?> removeEmployeeFromBranch(@RequestParam String employeeRemoveId,
                                                                           @RequestParam String branchId) {
        branchService.removeExistingEmployeeToBranch(employeeRemoveId, branchId);

        // find and return the updated branch
        return branchRepository.findById(branchId).map(BranchDto::new).map(branchDtoAssembler::toModel)
                .map(branchDto -> {
                    try {
                        return ResponseEntity.created(new URI(
                                branchDto.getRequiredLink(IanaLinkRelations.SELF).getHref())).body(branchDto);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return ResponseEntity.internalServerError().body("Something went wrong while updating the branch");
                }).orElseThrow(BranchNotFoundException::new);
    }

    /**
     * function that handle Post requests for creating new  branch
     *
     * @param newBranch new brunch to create and save into DB
     * @return created branch
     */
    @PostMapping("/branches")
    public ResponseEntity<EntityModel<Branch>> createBranch(@RequestBody Branch newBranch) {
        newBranch.setEmployeesIds(new ArrayList<>());
        // saving new branch into DB
        Branch savedBranch = branchService.saveBranchToDB(newBranch);

        return ResponseEntity.created(linkTo(methodOn(BranchesController.class)
                        .getBrunch(savedBranch.getId())).toUri())
                .body(brunchAssembler.toModel(savedBranch));
    }

    /**
     * function that handle deletes request for removing  branch
     *
     * @param id of branch to delete
     * @return deleted branch
     */
    @DeleteMapping("/branches/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable String id) {
        // find the requested branch or throw  BranchNotFoundException  exception
        Branch branchToDelete = branchService.findBranchById(id);

        // performs the removing logic
        boolean isDeleted = customBrunchRepository.removeBranch(branchToDelete);
        EntityModel<Branch> branchEntityModel = brunchAssembler.toModel(branchToDelete);

        if (isDeleted) {
            return ResponseEntity.ok(branchEntityModel);
        } else {
            return ResponseEntity.badRequest().body("cant remove desire branch");
        }
    }
}

