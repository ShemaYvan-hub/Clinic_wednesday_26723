package auca.example.clinicSystem.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import auca.example.clinicSystem.model.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Doctor findByPhoneNumber(String phoneNumber);
    Doctor findByOfficeId(UUID officeId);
    List<Doctor> findByName(String name);

    // B1: Doctors by specialization, matched without regard to letter case
    @Query("SELECT d FROM Doctor d JOIN d.specializations s WHERE LOWER(s.category) = LOWER(:name)")
    List<Doctor> findDoctorsBySpecializationNameIgnoreCase(@Param("name") String name);

    // B2: Doctors without an office, sorted by last name (assuming 'name' field)
    @Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.name ASC")
    List<Doctor> findDoctorsWithoutOffice();
}