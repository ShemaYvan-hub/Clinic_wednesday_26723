package auca.example.clinicSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import auca.example.clinicSystem.Repository.PatientRepository;
import auca.example.clinicSystem.Repository.DoctorRepository;
import auca.example.clinicSystem.model.Patient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepo;

    // Added DoctorRepository to check if doctor exists for B4
    @Autowired
    private DoctorRepository doctorRepo; 

    public String savePatient(Patient patient) {
        patientRepo.save(patient);
        return "Patient saved successfully";
    }

    public List<Patient> getAllPatients(){
        return patientRepo.findAll();
    }

    // A1: Get patients by last name
    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public Optional<Patient> getPatientById(UUID id){
        return patientRepo.findById(id);
    }

    public String updatePatient(UUID id, Patient updated){
        Optional<Patient> existing = patientRepo.findById(id);
        if (existing.isEmpty()) {
            return "Patient not found";
        }
        
        Patient patient = existing.get();
        // Updated to use the new firstName and lastName fields
        patient.setFirstName(updated.getFirstName());
        patient.setLastName(updated.getLastName());
        patient.setEmail(updated.getEmail());
        patient.setPhoneNumber(updated.getPhoneNumber());
        patient.setGender(updated.getGender());
        
        patientRepo.save(patient);
        return "Patient Updated Successfully";
    }

    public String deletePatient(UUID id){
        if (!patientRepo.existsById(id)) {
            return "Patient not found";
        }
        patientRepo.deleteById(id);
        return "Patient Deleted Successfully";
    }

    // B4: Patients of a doctor
    public Object getPatientsOfDoctor(UUID doctorId) {
        // Check if the doctor exists first
        if (!doctorRepo.existsById(doctorId)) {
            return "The doctor with that id does not exist";
        }
        return patientRepo.findPatientsByDoctorId(doctorId);
    }
    // C2: Frequent patients
    public List<Patient> getFrequentPatients(long min) {
        return patientRepo.findFrequentPatients(min);
    }
}