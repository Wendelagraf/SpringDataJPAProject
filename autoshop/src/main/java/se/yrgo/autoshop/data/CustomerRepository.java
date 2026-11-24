package se.yrgo.autoshop.data;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import se.yrgo.autoshop.domain.Customer;
import se.yrgo.autoshop.domain.Vehicle;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByName(String name);

    @Query("SELECT c.vehicles FROM Customer c WHERE c.id = :customerId")
    Set<Vehicle> findVehicleByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT c, v FROM Customer c LEFT JOIN c.vehicles v")
    List<Object[]> findAllCustomersWithVehicles();
}