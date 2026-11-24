package se.yrgo.autoshop.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import se.yrgo.autoshop.domain.Vehicle;
import se.yrgo.autoshop.projection.VehicleBrandProjection;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByBrand(String brand);

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.brand  = :brand WHERE v.id = :vehicleId")
    int UpdateBrandByVehicleId(@Param("brand") String brand, @Param("vehicleId") Long vehicleId);

    @Query("SELECT v.registrationNumber AS registrationNumber, v.brand AS brand FROM Vehicle v")
    List<VehicleBrandProjection> findAllVehicleBrandInfo();
}