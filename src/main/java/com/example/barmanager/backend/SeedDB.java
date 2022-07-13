package com.example.barmanager.backend;

import com.example.barmanager.backend.models.*;
import com.example.barmanager.backend.repositories.*;
import com.example.barmanager.backend.service.CocktailDBAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class SeedDB implements CommandLineRunner{
    private final CocktailDBAPIService drinkService;
    private final Logger logger;
    private final InventoryRepo barDrinkRepo;
    private final ICustomerRepository customerRepository;
    private final CustomOrderRepository customOrderRepository;

    @Autowired
    private  IOrderRepository orderRepository;
    @Autowired
    private IBrunchRepository brunchRepository;
    @Autowired
    private IEmployeeRepository employeeRepository;
    @Autowired
    private CustomBrunchRepository customBrunchRepository;

    public SeedDB(CocktailDBAPIService drinkService, InventoryRepo barDrinkRepo, ICustomerRepository customerRepository, CustomOrderRepository orderRepository) {
        this.drinkService = drinkService;
        this.barDrinkRepo = barDrinkRepo;
        this.customerRepository = customerRepository;
        this.customOrderRepository = orderRepository;
        this.logger = LoggerFactory.getLogger(SeedDB.class);
    }


    @Override
    public void run(String... args) throws Exception {

        List<BarDrink> drinks = barDrinkRepo.findAll();
        Customer customer = customerRepository.findAll().get(0);
        Order order = new Order(customer, LocalDate.of(2021,13,07));
        order.addDrinkToOrder(drinks.get(0));
        order.addDrinkToOrder(drinks.get(4));
        order.addDrinkToOrder(drinks.get(3));
        customOrderRepository.saveNewOrder(order);
       /* Employee employee = new Employee(987456321,"Test","User",46.0);
        logger.info(employeeRepository.save(employee).toString());*/

       /* List<BarDrink> drinks = barDrinkRepo.findAll();
        List<Order> orders = orderRepository.findAll();
        Brunch brunch = new Brunch("Haifa");
        brunch.setOrders(orders.subList(0,3));
        brunch.setBarDrinks(drinks.subList(0,5));
        brunchRepository.save(brunch);*/

        /*List<Employee> employees = employeeRepository.findAll();
        List<Brunch> brunches = brunchRepository.findAll();
        customBrunchRepository.addEmployee(brunches.get(1),employees.get(1));*/


 /*       Employee employee = new Employee(987456321,"John","Levi",31.5);
        Employee employee1 = new Employee(999666333,"Donald","Trump",34.4);
        List<Brunch> brunches = brunchRepository.findAll();
        employee.addToBrunch(brunches.get(0));
        employee1.addToBrunch(brunches.get(1));
        employeeRepository.save(employee);
        employeeRepository.save(employee1);*/


//        customOrderRepository.findCloseBySeat(3);
//        Order byId = orderRepository.findById("62cc388e5063b30494a66d63").get();
//        customOrderRepository.deleteOrder(byId);


//
//        BarDrink barDrink = barDrinkRepo.findAll().get(0);
//        BarDrink barDrink1 = barDrinkRepo.findAll().get(1);
//        BarDrink barDrink2= barDrinkRepo.findAll().get(2);
//        Customer customerTest = customerRepository.findAll().get(1);

     /*   Order order1 = new Order(customerTest, LocalDate.now());

        order1.addDrinkToOrder(barDrink2);
        order1.addDrinkToOrder(barDrink);
        order1.addDrinkToOrder(barDrink1);
        customOrderRepository.saveNewOrder(order1);*/



//        //Seed drink category collection
//        CompletableFuture<List<DrinkCategories>> categoriesFuture = drinkService.fetchDrinkCategories();
//        List<DrinkCategories> drinkCategories = categoriesFuture.get();
//        drinkCategoryRepo.saveAll(drinkCategories);
//
//        //Seed drink ingredients collection
//        CompletableFuture<List<DrinkIngredients>>  ingredientsFuture = drinkService.fetchDrinkIngredients();
//        List<DrinkIngredients> drinkIngredients = ingredientsFuture.get();
//        drinkIngredientsRepo.saveAll(drinkIngredients);
//
//        CompletableFuture<List<Drink>> reducedDrinkObjectsFuture;
//        CompletableFuture<List<Drink>> completedDrinkObject;
//        List<Drink> currentDrinks;
//
//        //Fetch all drinks by category
//        this.logger.info("Fetching all drinks from the API , Current time : " + LocalDateTime.now());
//        for (DrinkCategories categoryName: drinkCategories) {
//            reducedDrinkObjectsFuture = drinkService.fetchDrinksListByCategory(categoryName.getStrCategory());
//            currentDrinks = reducedDrinkObjectsFuture.get();
//
//            for (Drink drink: currentDrinks) {
//                completedDrinkObject = drinkService.fetchCocktailByName(drink.strDrink);
//                drinksRepo.saveAll(completedDrinkObject.get());
//            }
//        }
//        this.logger.info("Completed fetching all drinks from the API , Current time : " + LocalDateTime.now());
//
//        //Fetch all drinks by ingredient
//        this.logger.info("Fetching all drinks from the API , Current time : " + LocalDateTime.now());
//        for (DrinkIngredients ingredientName: drinkIngredients) {
//            reducedDrinkObjectsFuture = drinkService.fetchDrinksListByIngredient(ingredientName.getStrIngredient());
//            currentDrinks = reducedDrinkObjectsFuture.get();
//
//            for (Drink drink: currentDrinks) {
//                completedDrinkObject = drinkService.fetchCocktailByName(drink.strDrink);
//                drinksRepo.saveAll(completedDrinkObject.get());
//            }
//        }
//        this.logger.info("Completed fetching all drinks from the API , Current time : " + LocalDateTime.now());

//        BarDrink barDrink1 = new BarDrink("11007","Margarita","Cocktail","Alcoholic",new String[]{"Vodka","Gin"},"image",1222.0,"Round Glass");
//        BarDrink barDrink2 = new BarDrink("11118","Blue Margarita","Cocktail","Alcoholic",new String[]{"Pisco","Salt","Vodka"},"image",1222.0,"Round Glass");
//        BarDrink barDrink3 = new BarDrink("12345","Crimson Vodka","Ordinary Drink","Alcoholic", new String[]{"Blood","Tears","Vodka"},null,66.6,"Bare hands");
//        barDrinkRepo.save(barDrink1);
//        barDrinkRepo.save(barDrink2);
//        barDrinkRepo.save(barDrink3);
    }
}
