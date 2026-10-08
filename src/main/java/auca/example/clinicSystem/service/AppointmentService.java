package auca.example.clinicSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import auca.example.clinicSystem.Repository.AppointmentRepository;
import auca.example.clinicSystem.model.Appointment;
import auca.example.clinicSystem.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    public List<Appointment> getPendingAppointmentsByDoctor(UUID doctorId) {
        return appointmentRepo.findByDoctor_IdAndStatusOrderByDateAsc(doctorId, AppointmentStatus.SCHEDULED);
    }

    public List<Appointment> getPastPendingAppointments() {
        return appointmentRepo.findPastPendingAppointments();
    }

    public List<Object[]> getBusiestDoctors() {
        return appointmentRepo.countAppointmentsPerDoctorBusiestFirst();
    }

    public List<Appointment> getAppointmentsBySpecialization(String specName) {
        return appointmentRepo.findAppointmentsByDoctorSpecialization(specName);
    }

    public Page<Appointment> getDoctorAppointmentsPaginated(UUID doctorId) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").ascending());
        return appointmentRepo.findByDoctor_Id(doctorId, pageable);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByDateAsc(status);
    }

    public List<Appointment> getAppointmentsBetween(LocalDate start, LocalDate end) {
        return appointmentRepo.findByDateBetweenOrderByDateAsc(start, end);
    }

    public String saveAppointment(Appointment appointment) {
        boolean isBooked = appointmentRepo.existsByDoctor_IdAndDateAndStatusNot(
                appointment.getDoctor().getId(), 
                appointment.getDate(), 
                AppointmentStatus.CANCELLED
        );
        
        if (isBooked) {
            return "Doctor is already booked on that date";
        }
        
        appointmentRepo.save(appointment);
        return "Appointment saved successfully";
    }

    public List<Object[]> getAppointmentsPerStatus() {
        return appointmentRepo.countAppointmentsPerStatus();
    }

    @org.springframework.transaction.annotation.Transactional
    public String cancelDoctorAppointmentsForDay(UUID doctorId, LocalDate date) {
        int count = appointmentRepo.cancelDoctorAppointmentsForDay(
                doctorId, 
                date, 
                AppointmentStatus.CANCELLED, 
                AppointmentStatus.COMPLETED
        );
        return count + " appointments cancelled";
    }
}