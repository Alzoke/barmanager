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

/**
 * class which represents Branches Controller;
 */
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

    /**
     * function that handle Get request for get all exiting branches
     * @return Collection model of entities model of Branch with status code "ok"
     */
    @GetMapping("/branches")
    public ResponseEntity<CollectionModel<EntityModel<Branch>>> getAllBrunches()
    {
        return ResponseEntity.ok(brunchAssembler.toCollectionModel(brunchRepository.findAll()));

    }

    /**
     * function which handle get request and receiving single branch by id in the DB
     * @param id  - represents id of the requested branch in DB
     * @return return Entity model of requested branch
     */
    @GetMapping("/branches/{id}")
    public ResponseEntity<EntityModel<Branch>> getBrunch(@PathVariable String id)
    {
        return brunchRepository.findById(id)
                .map(brunch -> brunchAssembler.toModel(brunch)).map(ResponseEntity::ok)
                .orElseThrow(() -> new BranchNotFoundException(id));
    }

    /**
     * function that handle Get request for get all exiting branches (as DTOs)
     * @return Collection model of entities model of Dtos Branches with status code "ok"
     */
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

    /**
     * function which handle get request get single DTO branch
     * @param id  - represents id of the requested branch in DB
     * @return return Entity model of requested branch as DTO
     */
    @GetMapping("/branches/{id}/info")
    public ResponseEntity<EntityModel<BranchDto>> getBrunchDto(@PathVariable String id)
    {
        return brunchRepository.findById(id)
                .map(BranchDto::new)
                .map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BranchNotFoundException(id));
    }

    /**
     * function that handle Get request for getting  branch by given branch name (as DTO)
     * @param branchName - name of requested branch
     * @return requested branch that fits to given branch name as entity model of branch DTO
     *  or throws BranchNotFoundException if can't find such branch
     */
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

    /**
     * function that handle Put request for adding employee to branch
     * @param employeeToAddId id of requested employee
     * @param branchId  id of the requested branch
     * @return updated branch after adding employee
     */
    @PutMapping("/branches/updatedEmployees/add")
    public ResponseEntity<EntityModel<BranchDto>> addEmployeeToBranch(@RequestParam String employeeToAddId,
                                                                      @RequestParam String branchId)
    {
        branchService.addExistingEmployeeToBranch(employeeToAddId, branchId);

        // find and return the updated branch
        return brunchRepository.findById(branchId).map(BranchDto::new).map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok).orElseThrow(() -> new BranchNotFoundException());


    }

    /**
     * function that handle Put request for removing employee from branch
     * @param employeeRemoveId id of requested employee
     * @param branchId id of the requested branch
     * @return updated branch after removing employee
     */
    @PutMapping("/branches/updatedEmployees/remove")
    public ResponseEntity<EntityModel<BranchDto>> removeEmployeeFromBranch(@RequestParam String employeeRemoveId,
                                                                           @RequestParam String branchId)
    {
        branchService.removeExistingEmployeeToBranch(employeeRemoveId,branchId);

        // find and return the updated branch
        return brunchRepository.findById(branchId).map(BranchDto::new).map(brunchDtoAssembler::toModel)
                .map(ResponseEntity::ok).orElseThrow(() -> new BranchNotFoundException());
    }

    /**
     * function that handle Post request for creating new  branch
     * @param newBranch new brunch to create and save into DB
     * @return  created branch
     */
    @PostMapping("/branches")
    public ResponseEntity<EntityModel<Branch>> createBranch(@RequestBody Branch newBranch)
    {
//        System.out.println(newBranch);
        newBranch.setEmployeesIds(new ArrayList<>());
        // saving new branch into DB
        Branch savedBranch = branchService.saveBranchToDB(newBranch);
//        Branch /savedBranch = brunchRepository.save(newBranch);

        return ResponseEntity.created(linkTo(methodOn(BranchesController.class)
                        .getBrunch(savedBranch.getId())).toUri())
                .body(brunchAssembler.toModel(savedBranch));
    }

    /**
     * function that handle delete request for removing  branch
     * @param id of branch to delete
     * @return deleted branch
     */
    @DeleteMapping("/branches/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable String id)
    {
        // find the requested branch or throw  NOT FOUNT  exception
        Branch branchToDelete = branchService.findBranchById(id);
       /* Branch branchToDelete = brunchRepository.findById(id)
                .orElseThrow(() -> new BranchNotFoundException(id));*/

        // performs the removing logic
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

