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

    // 1. Updated String to AppointmentStatus enum
    List<Appointment> findByDoctor_IdAndStatusOrderByDateAsc(UUID doctorId, AppointmentStatus status);

    // 2. JPQL 
    @Query("SELECT a FROM Appointment a WHERE a.status = 'SCHEDULED' AND a.date < CURRENT_DATE")
    List<Appointment> findPastPendingAppointments();

    // 3. Aggregate
    @Query("SELECT d.name, COUNT(a) as total FROM Appointment a JOIN a.doctor d GROUP BY d.name ORDER BY total DESC")
    List<Object[]> countAppointmentsPerDoctorBusiestFirst();

    // 4. JOIN
    @Query("SELECT a FROM Appointment a JOIN a.doctor d JOIN d.specializations s WHERE s.category = :specializationName")
    List<Appointment> findAppointmentsByDoctorSpecialization(@Param("specializationName") String specializationName);

    // 5. Pagination 
    Page<Appointment> findByDoctor_Id(UUID doctorId, Pageable pageable);

    // A2: Appointments by status, ordered by date
    List<Appointment> findByStatusOrderByDateAsc(AppointmentStatus status);

    // A3: Appointments between two dates, ordered by date
    List<Appointment> findByDateBetweenOrderByDateAsc(LocalDate start, LocalDate end);

    // A4: Check if doctor has a non-cancelled appointment on a specific date
    boolean existsByDoctor_IdAndDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    // C1: Appointments per status
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsPerStatus();

    // C4: Doctor is absent (Bulk update)
    @Modifying
    @Query("UPDATE Appointment a SET a.status = :cancelled WHERE a.doctor.id = :doctorId AND a.date = :date AND a.status <> :completed")
    int cancelDoctorAppointmentsForDay(
            @Param("doctorId") UUID doctorId, 
            @Param("date") LocalDate date, 
            @Param("cancelled") AppointmentStatus cancelled, 
            @Param("completed") AppointmentStatus completed);
}