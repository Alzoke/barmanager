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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Component
public class SeedDB implements CommandLineRunner {
    private final CocktailDBAPIService drinkService;
    private final Logger logger;
    private final InventoryRepo barDrinkRepo;
    private final ICustomerRepository customerRepository;
    private final CustomOrderRepository customOrderRepository;

    @Autowired
    private IOrderRepository orderRepository;
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

    public Branch createNewBranch(String name, String country, String city) {
        Branch branch = new Branch(name, country, city);
        Branch savedBranch = branchRepository.save(branch);
        logger.info("Created branch -> " + savedBranch);
        logger.info(branchRepository.findAll().toString());

        return savedBranch;

    }

    public void addEmployeeToBranch(Branch branch, Employee employee) {
        logger.info("Before insertion employees size -> " + branch.getEmployeesIds().size());
        List<Employee> employees = employeeRepository.findAll();
        if (employee != null) {
            customBranchRepository.addEmployee(branch, employee);
        } else if (!employees.isEmpty()) {
            customBranchRepository.addEmployee(branch, employees.get(0));

        }
        Branch updatedBranch = branchRepository.findById(branch.getId()).get();
        logger.info("After insertion employees size -> " + updatedBranch.getEmployeesIds().size());
        logger.info("Branch employees ids" + updatedBranch.getEmployeesIds());
        logger.info("Employee branches:" + employees.get(0).getBranches().toString());

    }

    public void removeEmployeeFromBranch(Branch branch, Employee employee) {
        int size = branchRepository.findAll().size();
        logger.info("Before removing employees size -> " +
                branchRepository.findById(branch.getId()).get().getEmployeesIds().size());
        logger.info("Branch employees ids" + branchRepository.findById(branch.getId())
                .get().getEmployeesIds());

        if (employee != null) {
            customBranchRepository.removeEmployee(branch, employee);
            logger.info("Employee branches:" + employee.getBranches().toString());

        } else {
            List<Employee> employees = employeeRepository.findAll();
            customBranchRepository.removeEmployee(branch, employees.get(0));
            logger.info("Employee branches:" + employees.get(0).toString());

        }

        Branch updatedBranch = branchRepository.findById(branch.getId()).get();
        logger.info("After removing employees size -> " + updatedBranch.getEmployeesIds().size());
        logger.info("Branch employees ids" + updatedBranch.getEmployeesIds());
    }

    public Employee createEmployee(int id, String firstName, String lastName, double salary) {
        Employee employee = new Employee(id, firstName, lastName, salary);
        Employee savedEmployee = employeeRepository.save(employee);
        logger.info("created employee -> " + savedEmployee);

        return savedEmployee;
    }

    public Customer createNewCustomer(int idNumber, String fName, String lName) {
        Customer customer = new Customer(idNumber, fName, lName);
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("saving new customer " + savedCustomer);
        return savedCustomer;

    }

    public void createNewOrder(Customer customer) {
        Order order = new Order(customer);
        List<BarDrink> allDrinks = barDrinkRepo.findAll();

        // add some drinks to the order
        for (BarDrink barDrink : allDrinks.subList(2, 6)) {
            order.addDrinkToOrder(barDrink);
        }
        Order saveNewOrder = customOrderRepository.saveNewOrder(order);

        logger.info("saving order " + saveNewOrder);


    }

    private void createOrders(Customer customer, int year) {
        int min = 2, max = 5;

        for (int i = 0; i < 12; i++) {
            int randomDay = (int) (Math.random() * (max - min + 1) + min);
            LocalDate date = LocalDate.of(year, i + 1, randomDay);
            Order order = new Order(customer);
            order.setOrderDate(date);
            order.setOrderStatus(eOrderStatus.Close);
            List<BarDrink> allDrinks = barDrinkRepo.findAll();

            // add some drinks to the order
            for (BarDrink barDrink : allDrinks.subList(randomDay, 7)) {
                order.addDrinkToOrder(barDrink);
            }
            customOrderRepository.saveNewOrder(order);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        executeSeed();
    }

    private void executeSeed() throws InterruptedException, ExecutionException {
        Random rand = new Random();
        Branch newBranch = createNewBranch("Dizengoff", "Israel", "Tel Aviv");
        Employee employee = createEmployee(123456789, "Natan", "Gershbein", 29.3);

        customBranchRepository.addEmployee(newBranch, employee);

        Customer customer = createNewCustomer(321321321, "Alexi", "Barkan");

        List<ApiDrink> supplierDrinks = new ArrayList<>();
        List<BarDrink> barDrinks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ApiDrink randomDrink = drinkService.fetchRandomDrink().get();

            if (!supplierDrinks.contains(randomDrink)) {
                supplierDrinks.add(randomDrink);
            } else {
                i--;
            }
        }

        for (ApiDrink apiDrink : supplierDrinks) {
            List<String> ingredients = new ArrayList<>();

            //Each drink in the api can contain up to 15 ingredients, so we check dynamically with reflection.
            for (int i = 0; i < 15; i++) {
                String fieldName = String.format("strIngredient%s", (i + 1));
                try {
                    if (apiDrink.getClass().getDeclaredField(fieldName).get(apiDrink) != null) {
                        ingredients.add(String.valueOf(apiDrink.getClass().getDeclaredField(fieldName).get(apiDrink)));
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }

            barDrinks.add(new BarDrink(
                    apiDrink.getIdDrink(),
                    apiDrink.getStrDrink(),
                    apiDrink.getStrCategory(),
                    apiDrink.getStrAlcoholic(),
                    ingredients,
                    apiDrink.getStrDrinkThumb(),
                    randPriceFormatter(rand),
                    apiDrink.getStrGlass()
            ));
        }
        barDrinkRepo.saveAll(barDrinks);
        createOrders(customer, 2021);
        createOrders(customer, 2022);
    }

    private double randPriceFormatter(Random rand)
    {
        double randPrice = rand.nextDouble(150.0);
        return Double.parseDouble(String.format("%.2f",randPrice));
    }
}
