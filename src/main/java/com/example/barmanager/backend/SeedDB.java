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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    private IBrunchRepository branchRepository;
    @Autowired
    private IEmployeeRepository employeeRepository;
    @Autowired
    private CustomBranchRepository customBranchRepository;

    public SeedDB(CocktailDBAPIService drinkService, InventoryRepo barDrinkRepo, ICustomerRepository customerRepository, CustomOrderRepository orderRepository) {
        this.drinkService = drinkService;
        this.barDrinkRepo = barDrinkRepo;
        this.customerRepository = customerRepository;
        this.customOrderRepository = orderRepository;
        this.logger = LoggerFactory.getLogger(SeedDB.class);
    }

    public Branch createNewBranch(String name,String country, String city){
        Branch branch = new Branch(name, country, city);
        Branch savedBranch = branchRepository.save(branch);
        logger.info("Created branch -> " + savedBranch);
        logger.info(branchRepository.findAll().toString());

        return savedBranch;

    }
    public void addEmployeeToBranch(Branch branch, Employee employee)
    {
        logger.info("Before insertion employees size -> " + branch.getEmployeesIds().size());
        List<Employee> employees = employeeRepository.findAll();
        if ( employee != null )
        {
            customBranchRepository.addEmployee(branch,employee);
        }
        else  if ( !employees.isEmpty() ){
            customBranchRepository.addEmployee(branch,employees.get(0));

        }
        Branch updatedBranch = branchRepository.findById(branch.getId()).get();
        logger.info("After insertion employees size -> " + updatedBranch.getEmployeesIds().size());
        logger.info("Branch employees ids" + updatedBranch.getEmployeesIds());
        logger.info("Employee branches:" + employees.get(0).getBranches().toString());

    }

    public void removeEmployeeFromBranch(Branch branch,Employee employee)
    {
        int size = branchRepository.findAll().size();
        logger.info("Before removing employees size -> " +
                branchRepository.findById(branch.getId()).get().getEmployeesIds().size());
        logger.info("Branch employees ids" +  branchRepository.findById(branch.getId())
                .get().getEmployeesIds());

        if ( employee != null )
        {
            customBranchRepository.removeEmployee(branch, employee);
            logger.info("Employee branches:" + employee.getBranches().toString());

        }
        else {
            List<Employee> employees = employeeRepository.findAll();
            customBranchRepository.removeEmployee(branch,employees.get(0));
            logger.info("Employee branches:" + employees.get(0).toString());

        }

        Branch updatedBranch = branchRepository.findById(branch.getId()).get();
        logger.info("After removing employees size -> " + updatedBranch.getEmployeesIds().size());
        logger.info("Branch employees ids" + updatedBranch.getEmployeesIds());
    }
    public Employee createEmployee(int id,String firstName, String lastName,double salary){
        Employee employee = new Employee(id,firstName,lastName,salary);
        Employee savedEmployee = employeeRepository.save(employee);
        logger.info("created employee -> " + savedEmployee);

        return savedEmployee;
    }
    public Customer createNewCustomer(int idNumber,String fName,String lName)
    {
        Customer customer = new Customer(idNumber,fName,lName);
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("saving new customer " + savedCustomer);
        return savedCustomer;

    }
    public void createNewOrder(Customer customer)
    {
        Order order = new Order(customer);
        List<BarDrink> allDrinks = barDrinkRepo.findAll();

        // add some drinks to the order
        for ( BarDrink barDrink : allDrinks.subList(2, 6) )
        {
            order.addDrinkToOrder(barDrink);
        }
        Order saveNewOrder = customOrderRepository.saveNewOrder(order);

        logger.info("saving order " + saveNewOrder);


    }
    private void createRandomDates()
    {
        Customer customer = customerRepository.findAll().get(1);
        int min = 2, max = 5;
        // 2021
        for ( int i = 0; i < 12; i++ )
        {
            int randomDay = (int) (Math.random() * (max - min + 1) + min);

            LocalDate date = LocalDate.of(2020,i + 1,randomDay);
//            System.out.println(date);
            Order order = new Order(customer);
            order.setOrderDate(date);
            order.setOrderStatus(eOrderStatus.Close);
            List<BarDrink> allDrinks = barDrinkRepo.findAll();

            // add some drinks to the order
            for ( BarDrink barDrink : allDrinks.subList(randomDay, 7) )
            {
                order.addDrinkToOrder(barDrink);
            }
            Order saveNewOrder = customOrderRepository.saveNewOrder(order);

            logger.info("saving order " + saveNewOrder);

        }

    }

    @Override
    public void run(String... args) throws Exception {

//        executeSeed();

//
        //Branch branch = new Branch("Jerusalem");
//        branchRepository.save(branch);
//        Customer customer = customerRepository.findAll().get(0);
//        Order order = orderRepository.findAll().get(0);
//        orderRepository.delete(order);
//        order.setCustomer(customer);
//        customOrderRepository.saveNewOrder(order);
//        Employee employee = new Employee(987654321,"Test","User",29.5);
//        employeeRepository.save(employee);
//        customBrunchRepository.addEmployee(branch,employee);

        /*List<BarDrink> drinks = barDrinkRepo.findAll();
        Customer customer = customerRepository.findAll().get(0);
        Order order = new Order(customer, LocalDate.of(2021,13,07));
        order.addDrinkToOrder(drinks.get(0));
        order.addDrinkToOrder(drinks.get(4));
        order.addDrinkToOrder(drinks.get(3));
        customOrderRepository.saveNewOrder(order);*/
       /* Employee employee = new Employee(987456321,"Test","User",46.0);
        logger.info(employeeRepository.save(employee).toString());*/

       /* List<BarDrink> drinks = barDrinkRepo.findAll();
        List<Order> orders = orderRepository.findAll();
        Branch brunch = new Branch("Haifa");
        brunch.setOrders(orders.subList(0,3));
        brunch.setBarDrinks(drinks.subList(0,5));
        branchRepository.save(brunch);*/

        /*List<Employee> employees = employeeRepository.findAll();
        List<Branch> branches = branchRepository.findAll();
        customBrunchRepository.addEmployee(branches.get(1),employees.get(1));*/


 /*       Employee employee = new Employee(987456321,"John","Levi",31.5);
        Employee employee1 = new Employee(999666333,"Donald","Trump",34.4);
        List<Branch> branches = branchRepository.findAll();
        employee.addToBrunch(branches.get(0));
        employee1.addToBrunch(branches.get(1));
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

    private void executeSeed() throws InterruptedException, ExecutionException
    {
        logger.info("Create new Brunch");
        Branch newBranch = createNewBranch("test1", "country1", "city1");
        logger.info("add  employee to Brunch");
        addEmployeeToBranch(newBranch, null);
        logger.info("remove employee from Brunch");
        removeEmployeeFromBranch(newBranch,null);

        logger.info("create new employee");
        Employee newEmployee = createEmployee(1111, "seed", "user", 50.5);
        logger.info("add new employee to Brunch");
        addEmployeeToBranch(newBranch,newEmployee);
        logger.info("remove new employee from Brunch");
        removeEmployeeFromBranch(newBranch,newEmployee);
        logger.info("----create new customer----");
        Customer customer = createNewCustomer(00000,"customer","seedDB");
        createNewOrder(customer);

        //Seed drink category collection
        CompletableFuture<List<String>> categoriesFuture = drinkService.fetchDrinkCategories();
        List<String> drinkCategories = categoriesFuture.get();
        logger.info(drinkCategories.toString());
    }
}
