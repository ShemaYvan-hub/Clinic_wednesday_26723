package auca.example.clinicSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import auca.example.clinicSystem.Repository.*;
import auca.example.clinicSystem.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired private PatientRepository patientRepo;
    @Autowired private DoctorRepository doctorRepo;
    @Autowired private OfficeRepository officeRepo;
    @Autowired private AppointmentRepository appointmentRepo;
    @Autowired private PrescriptionRepository prescriptionRepo;

    
    public Map<String, Long> getSummary() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("patients", patientRepo.count());
        stats.put("doctors", doctorRepo.count());
        stats.put("freeOffices", (long) officeRepo.findFreeOffices().size());
        stats.put("appointmentsToday", appointmentRepo.countByDate(LocalDate.now()));
        stats.put("scheduled", appointmentRepo.countByStatus(AppointmentStatus.SCHEDULED));
        stats.put("prescriptions", prescriptionRepo.count());
        return stats;
    }
}