package auca.example.clinicSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import auca.example.clinicSystem.Repository.AppointmentRepository;
import auca.example.clinicSystem.Repository.PrescriptionRepository;
import auca.example.clinicSystem.model.Appointment;
import auca.example.clinicSystem.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    
    @Autowired
    private PrescriptionRepository prescriptionRepo;

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

    

    
    public String saveAppointment(Appointment appointment) {
        
        if (appointment.getDate() != null && appointment.getDate().isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }
        
        
        appointment.setStatus(AppointmentStatus.SCHEDULED);

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

    
    public String updateAppointmentStatus(UUID id, AppointmentStatus newStatus) {
        Optional<Appointment> opt = appointmentRepo.findById(id);
        if (opt.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        
        Appointment appt = opt.get();
        AppointmentStatus current = appt.getStatus();

        boolean valid = false;
        if (current == AppointmentStatus.SCHEDULED && (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED)) {
            valid = true;
        } else if (current == AppointmentStatus.CONFIRMED && (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED)) {
            valid = true;
        }
        
        if (!valid) {
            return "Cannot change status from " + current + " to " + newStatus;
        }

        appt.setStatus(newStatus);
        appointmentRepo.save(appt);
        return "Appointment status updated successfully";
    }

    
    public String rescheduleAppointment(UUID id, LocalDate newDate) {
        Optional<Appointment> opt = appointmentRepo.findById(id);
        if (opt.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        
        Appointment appt = opt.get();

        if (appt.getStatus() == AppointmentStatus.COMPLETED || appt.getStatus() == AppointmentStatus.CANCELLED) {
            return "A " + appt.getStatus() + " appointment cannot be rescheduled";
        }

        if (newDate.isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        boolean isBooked = appointmentRepo.existsByDoctor_IdAndDateAndStatusNot(
                appt.getDoctor().getId(), 
                newDate, 
                AppointmentStatus.CANCELLED
        );
        
        if (isBooked) {
            return "Doctor is already booked on that date";
        }

        appt.setDate(newDate);
        appt.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appt);
        return "Appointment rescheduled successfully";
    }

    
    
    
    public String deleteAppointment(UUID id) {
        if (!appointmentRepo.existsById(id)) {
            return "The appointment with that id does not exist";
        }
        if (prescriptionRepo.existsByAppointment_Id(id)) {
            return "That appointment has prescriptions, delete them first";
        }
        
        appointmentRepo.deleteById(id);
        return "Appointment deleted successfully";
    }
}