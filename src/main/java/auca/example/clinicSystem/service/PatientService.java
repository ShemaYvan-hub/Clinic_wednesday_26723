package auca.example.clinicSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Arrays;
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

    
    @Autowired
    private DoctorRepository doctorRepo; 

    public String savePatient(Patient patient) {
        patientRepo.save(patient);
        return "Patient saved successfully";
    }

    public List<Patient> getAllPatients(){
        return patientRepo.findAll();
    }

    
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

    
    public Object getPatientsOfDoctor(UUID doctorId) {
       
        if (!doctorRepo.existsById(doctorId)) {
            return "The doctor with that id does not exist";
        }
        return patientRepo.findPatientsByDoctorId(doctorId);
    }
    
    public List<Patient> getFrequentPatients(long min) {
        return patientRepo.findFrequentPatients(min);
    }
    @Autowired
    private auca.example.clinicSystem.Repository.AppointmentRepository appointmentRepo;

    
    public Object getUpcomingAppointments(String ssNumber) {
        if (!patientRepo.existsBySsNumber(ssNumber)) {
            return "That patient with that ssnumber does not exist";
        }
        List<auca.example.clinicSystem.model.AppointmentStatus> statuses = Arrays.asList(
                auca.example.clinicSystem.model.AppointmentStatus.SCHEDULED, 
                auca.example.clinicSystem.model.AppointmentStatus.CONFIRMED
        );
        return appointmentRepo.findByPatient_SsNumberAndDateGreaterThanEqualAndStatusInOrderByDateAsc(ssNumber, LocalDate.now(), statuses);
    }
}