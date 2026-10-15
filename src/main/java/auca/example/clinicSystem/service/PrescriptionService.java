package auca.example.clinicSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import auca.example.clinicSystem.Repository.AppointmentRepository;
import auca.example.clinicSystem.Repository.PrescriptionRepository;
import auca.example.clinicSystem.model.Appointment;
import auca.example.clinicSystem.model.AppointmentStatus;
import auca.example.clinicSystem.model.Prescription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;
    
    @Autowired
    private AppointmentRepository appointmentRepo;

    
    public String writePrescription(UUID appointmentId, Prescription prescription) {
        Optional<Appointment> apptOpt = appointmentRepo.findById(appointmentId);
        if (apptOpt.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        
        Appointment appt = apptOpt.get();
        if (appt.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }

        prescription.setAppointment(appt);
        prescription.setIssuedDate(LocalDate.now());
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    
    public Object getPrescriptionsByAppointment(UUID appointmentId) {
        if (!appointmentRepo.existsById(appointmentId)) {
            return "The appointment with that id does not exist";
        }
        return prescriptionRepo.findByAppointment_Id(appointmentId);
    }

    
    public List<Prescription> getPatientPrescriptionHistory(String ssNumber) {
        return prescriptionRepo.findByPatientSsNumber(ssNumber);
    }

    
    public String updatePrescription(UUID id, Prescription updated) {
        Optional<Prescription> existing = prescriptionRepo.findById(id);
        if (existing.isEmpty()) {
            return "The prescription with that id does not exist";
        }
        
        Prescription p = existing.get();
        p.setMedicineName(updated.getMedicineName());
        p.setDosage(updated.getDosage());
        p.setDurationDays(updated.getDurationDays());
        
        prescriptionRepo.save(p);
        return "Prescription updated successfully";
    }

    
    public String deletePrescription(UUID id) {
        if (!prescriptionRepo.existsById(id)) {
            return "The prescription with that id does not exist";
        }
        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }
}