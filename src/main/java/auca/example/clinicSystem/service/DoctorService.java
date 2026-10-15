package auca.example.clinicSystem.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import auca.example.clinicSystem.Repository.DoctorRepository;
import java.time.LocalDate;
import auca.example.clinicSystem.Repository.OfficeRepository;
import auca.example.clinicSystem.Repository.SpecializationRepository;
import auca.example.clinicSystem.model.Doctor;
import auca.example.clinicSystem.model.Office;
import auca.example.clinicSystem.model.Specialization;

@Service
public class DoctorService {

    public static final String SAVED = "Doctor saved successfully";
    public static final String UPDATED = "Doctor updated successfully";
    public static final String DELETED = "Doctor deleted successfully";
    public static final String NOT_FOUND = "Doctor not found";
    public static final String OFFICE_NOT_FOUND = "Office not found";
    public static final String OFFICE_TAKEN = "Office already assigned to another doctor";
    public static final String PHONE_EXISTS = "Phone number already exists";

    @Autowired
    private DoctorRepository doctorRepo;
    
    @Autowired
    private OfficeRepository officeRepo;
    
    @Autowired
    private SpecializationRepository specRepo;

    public String saveDoctor(Doctor doctor) {
        if (doctorRepo.findByPhoneNumber(doctor.getPhoneNumber()) != null) {
            return PHONE_EXISTS;
        }
        if (doctor.getOffice() != null) {
            if (doctor.getOffice().getId() == null) {
                return OFFICE_NOT_FOUND;
            }
            Optional<Office> office = officeRepo.findById(doctor.getOffice().getId());
            if (office.isEmpty()) {
                return OFFICE_NOT_FOUND;
            }
            if (doctorRepo.findByOfficeId(office.get().getId()) != null) {
                return OFFICE_TAKEN;
            }
            doctor.setOffice(office.get());
        }
        doctorRepo.save(doctor);
        return SAVED;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    public Optional<Doctor> getDoctorById(UUID id) {
        return doctorRepo.findById(id);
    }

    public List<Doctor> getDoctorsByName(String name) {
        return doctorRepo.findByName(name);
    }

    
    public List<Doctor> getDoctorsBySpecialization(String name) {
        return doctorRepo.findDoctorsBySpecializationNameIgnoreCase(name);
    }

    
    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }

    public String updateDoctor(UUID id, Doctor updated) {
        Optional<Doctor> existing = doctorRepo.findById(id);
        if (existing.isEmpty()) {
            return NOT_FOUND;
        }
        Doctor doctor = existing.get();
        doctor.setName(updated.getName());
        doctor.setPhoneNumber(updated.getPhoneNumber());

        if (updated.getOffice() != null && updated.getOffice().getId() != null) {
            Optional<Office> office = officeRepo.findById(updated.getOffice().getId());
            if (office.isEmpty()) {
                return OFFICE_NOT_FOUND;
            }
            Doctor owner = doctorRepo.findByOfficeId(office.get().getId());
            if (owner != null && !owner.getId().equals(id)) {
                return OFFICE_TAKEN;
            }
            doctor.setOffice(office.get());
        }
        doctorRepo.save(doctor);
        return UPDATED;
    }

    public String deleteDoctor(UUID id) {
        if (!doctorRepo.existsById(id)) {
            return NOT_FOUND;
        }
        doctorRepo.deleteById(id);
        return DELETED;
    }

    
    public String addSpecialization(UUID doctorId, UUID specId) {
        Optional<Doctor> dOpt = doctorRepo.findById(doctorId);
        if (dOpt.isEmpty()) return "The doctor with that id does not exist";
        
        Optional<Specialization> sOpt = specRepo.findById(specId);
        if (sOpt.isEmpty()) return "That specialization does not exist";

        Doctor doc = dOpt.get();
        Specialization spec = sOpt.get();

        if (doc.getSpecializations().contains(spec)) {
            return "The doctor already has that specialization";
        }
        
        doc.getSpecializations().add(spec);
        doctorRepo.save(doc);
        return "Specialization added to the doctor";
    }

    
    public String removeSpecialization(UUID doctorId, UUID specId) {
        Optional<Doctor> dOpt = doctorRepo.findById(doctorId);
        if (dOpt.isEmpty()) return "The doctor with that id does not exist";
        
        Optional<Specialization> sOpt = specRepo.findById(specId);
        if (sOpt.isEmpty()) return "That specialization does not exist";

        Doctor doc = dOpt.get();
        Specialization spec = sOpt.get();

        if (!doc.getSpecializations().contains(spec)) {
            return "The doctor does not have that specialization";
        }
        
        doc.getSpecializations().remove(spec);
        doctorRepo.save(doc);
        return "Specialization removed from the doctor";
    }

    
    public String moveDoctor(UUID doctorId, String officeNumber) {
        Optional<Doctor> dOpt = doctorRepo.findById(doctorId);
        if (dOpt.isEmpty()) return "The doctor with that id does not exist";
        
        Office newOffice = officeRepo.findByOfficeNumber(officeNumber);
        if (newOffice == null) return "The office with that office number does not exist";

        Doctor doc = dOpt.get();

        
        Doctor owner = doctorRepo.findByOfficeId(newOffice.getId());
        if (owner != null && !owner.getId().equals(doctorId)) {
            return "That office is taken by other doctor";
        }

        doc.setOffice(newOffice);
        doctorRepo.save(doc);
        return "Office changed successfully";
    }
    @Autowired
    private auca.example.clinicSystem.Repository.AppointmentRepository appointmentRepo;

   
    public Object getDoctorAgenda(UUID doctorId, LocalDate date) {
        if (!doctorRepo.existsById(doctorId)) {
            return "The doctor with that id does not exist";
        }
        return appointmentRepo.findByDoctor_IdAndDateAndStatusNot(doctorId, date, auca.example.clinicSystem.model.AppointmentStatus.CANCELLED);
    }

   
    public List<Doctor> searchDoctors(String name) {
        return doctorRepo.searchByName(name);
    }
}