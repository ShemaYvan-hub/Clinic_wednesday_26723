package auca.example.clinicSystem.Repository;

import java.util.*;
import auca.example.clinicSystem.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {
     Office findByOfficeNumber(String  officeNumber);
     List<Office> findByName(String name);
     // C3: Busiest office
    @Query("SELECT o.name, o.officeNumber, COUNT(a) FROM Appointment a JOIN a.doctor d JOIN d.office o GROUP BY o.name, o.officeNumber ORDER BY COUNT(a) DESC")
    List<Object[]> findBusiestOffices();
}
