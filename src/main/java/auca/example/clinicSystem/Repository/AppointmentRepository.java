package auca.example.clinicSystem.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import auca.example.clinicSystem.model.Appointment;
import auca.example.clinicSystem.model.AppointmentStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    
    List<Appointment> findByDoctor_IdAndStatusOrderByDateAsc(UUID doctorId, AppointmentStatus status);

     
    @Query("SELECT a FROM Appointment a WHERE a.status = 'SCHEDULED' AND a.date < CURRENT_DATE")
    List<Appointment> findPastPendingAppointments();

    
    @Query("SELECT d.name, COUNT(a) as total FROM Appointment a JOIN a.doctor d GROUP BY d.name ORDER BY total DESC")
    List<Object[]> countAppointmentsPerDoctorBusiestFirst();

   
    @Query("SELECT a FROM Appointment a JOIN a.doctor d JOIN d.specializations s WHERE s.category = :specializationName")
    List<Appointment> findAppointmentsByDoctorSpecialization(@Param("specializationName") String specializationName);

    
    Page<Appointment> findByDoctor_Id(UUID doctorId, Pageable pageable);

    
    List<Appointment> findByStatusOrderByDateAsc(AppointmentStatus status);

    
    List<Appointment> findByDateBetweenOrderByDateAsc(LocalDate start, LocalDate end);

    
    boolean existsByDoctor_IdAndDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsPerStatus();

    
    @Modifying
    @Query("UPDATE Appointment a SET a.status = :cancelled WHERE a.doctor.id = :doctorId AND a.date = :date AND a.status <> :completed")
    int cancelDoctorAppointmentsForDay(
            @Param("doctorId") UUID doctorId, 
            @Param("date") LocalDate date, 
            @Param("cancelled") AppointmentStatus cancelled, 
            @Param("completed") AppointmentStatus completed);
            
    List<Appointment> findByDoctor_IdAndDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    
    List<Appointment> findByPatient_SsNumberAndDateGreaterThanEqualAndStatusInOrderByDateAsc(String ssNumber, LocalDate date, List<AppointmentStatus> statuses);

    
    long countByDate(LocalDate date);
    long countByStatus(AppointmentStatus status);
}