
package se.yrgo.autoshop.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import se.yrgo.autoshop.data.CustomerRepository;
import se.yrgo.autoshop.data.VehicleRepository;
import se.yrgo.autoshop.domain.Customer;
import se.yrgo.autoshop.domain.Vehicle;
import se.yrgo.autoshop.dto.CustomerDTO;
import se.yrgo.autoshop.dto.VehicleDTO;
import se.yrgo.autoshop.dto.VehicleBrandDTO;
import se.yrgo.autoshop.projection.VehicleBrandProjection;

@RestController
public class Controller {

    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    public Controller(CustomerRepository customerRepository, VehicleRepository vehicleRepository) {
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // Create customer

    @PostMapping("/customers")
    public ResponseEntity<String> createCustomer(
            @RequestParam String name,
            @RequestParam String phone) {

        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhoneNumber(phone);

        customerRepository.save(customer);

        return ResponseEntity.ok("Customer created successfully");
    }

    // Get customer ID by name

    @GetMapping("/customer-id")
    public ResponseEntity<Long> getCustomerIdFromName(@RequestParam String name) {
        return customerRepository.findByName(name)
                .map(Customer::getId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all customers with their vehicles

    @GetMapping("/customers")
    public List<CustomerDTO> allCustomersWithVehicles() {

        List<Object[]> all = customerRepository.findAllCustomersWithVehicles();

        Map<Long, CustomerDTO> customerMap = new HashMap<>();

        for (Object[] result : all) {

            Customer customer = (Customer) result[0];
            Vehicle vehicle = (Vehicle) result[1];

            CustomerDTO customerDTO = customerMap.getOrDefault(
                    customer.getId(), new CustomerDTO());

            customerDTO.setId(customer.getId());
            customerDTO.setName(customer.getName());
            customerDTO.setPhoneNumber(customer.getPhoneNumber());

            if (vehicle != null) {

                VehicleDTO vehicleDTO = new VehicleDTO();

                vehicleDTO.setId(vehicle.getId());
                vehicleDTO.setRegistrationNumber(vehicle.getRegistrationNumber());
                vehicleDTO.setBrand(vehicle.getBrand());
                vehicleDTO.setModel(vehicle.getModel());
                vehicleDTO.setProductionYear(vehicle.getProductionYear());

                customerDTO.getVehicles().add(vehicleDTO);
            }

            customerMap.put(customer.getId(), customerDTO);
        }

        return new ArrayList<>(customerMap.values());
    }

    // Create vehicle

    @PostMapping("/vehicles")
    public ResponseEntity<String> createVehicle(
            @RequestParam String registrationNumber,
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam int productionYear) {

        Vehicle vehicle = new Vehicle();

        vehicle.setRegistrationNumber(registrationNumber);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setProductionYear(productionYear);

        vehicleRepository.save(vehicle);

        return ResponseEntity.ok("Vehicle created successfully");
    }

    // Get all vehicles

    @GetMapping("/vehicles")
    public List<Vehicle> allVehicles() {

        return vehicleRepository.findAll();
    }

    // Get vehicles by brand

    @GetMapping("/vehicles-by-brand")
    public List<VehicleDTO> getVehiclesByBrand(@RequestParam String brand) {

        List<Vehicle> vehicles = vehicleRepository.findByBrand(brand);

        List<VehicleDTO> vehicleDTOList = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {

            VehicleDTO vehicleDTO = new VehicleDTO();

            vehicleDTO.setId(vehicle.getId());
            vehicleDTO.setRegistrationNumber(vehicle.getRegistrationNumber());
            vehicleDTO.setBrand(vehicle.getBrand());
            vehicleDTO.setModel(vehicle.getModel());
            vehicleDTO.setProductionYear(vehicle.getProductionYear());

            vehicleDTOList.add(vehicleDTO);
        }

        return vehicleDTOList;
    }

    // Add vehicle to customer

    @PostMapping("/addvehicletocustomer")
    public ResponseEntity<String> addVehicleToCustomer(
            @RequestParam Long customerId,
            @RequestParam Long vehicleId) {

        Optional<Customer> customerOpt = customerRepository.findById(customerId);

        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);

        if (customerOpt.isEmpty() || vehicleOpt.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Invalid customer or vehicle");
        }

        Customer customer = customerOpt.get();
        Vehicle vehicle = vehicleOpt.get();

        vehicle.setCustomer(customer);

        vehicleRepository.save(vehicle);

        return ResponseEntity.ok("Vehicle successfully added to customer");
    }

    // Update vehicle brand

    @PutMapping("/vehicles/{id}/brand")
    public ResponseEntity<String> updateVehicleBrand(
            @PathVariable Long id,
            @RequestParam String brand) {

        int updated = vehicleRepository.UpdateBrandByVehicleId(brand, id);

        if (updated == 0) {

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Vehicle brand updated successfully");
    }

    // Get vehicle registration numbers and brands using Projection

    @GetMapping("/vehicle-brand-info")
    public List<VehicleBrandDTO> getVehicleBrandInfo() {

        List<VehicleBrandProjection> all = vehicleRepository.findAllVehicleBrandInfo();

        List<VehicleBrandDTO> vehicleBrandDTOList = new ArrayList<>();

        for (VehicleBrandProjection projection : all) {

            VehicleBrandDTO vehicleBrandDTO = new VehicleBrandDTO();

            vehicleBrandDTO.setRegistrationNumber(
                    projection.getRegistrationNumber());

            vehicleBrandDTO.setBrand(
                    projection.getBrand());

            vehicleBrandDTOList.add(vehicleBrandDTO);
        }

        return vehicleBrandDTOList;
    }
}
