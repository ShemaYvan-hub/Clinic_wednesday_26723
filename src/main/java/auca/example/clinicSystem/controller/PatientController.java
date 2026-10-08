package auca.example.clinicSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import auca.example.clinicSystem.model.Patient;
import auca.example.clinicSystem.service.PatientService;

@RestController
@RequestMapping(value = "/api/patients")
public class PatientController {
    
    @Autowired
    private PatientService patientServ;

    @PostMapping(value = "/save")
    public ResponseEntity<?> savePatient(@RequestBody Patient patient){
        String returnedMessage = patientServ.savePatient(patient);
        if (returnedMessage.equals("Patient saved successfully")) {
            return new ResponseEntity<>(returnedMessage,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value ="/all")
    public ResponseEntity<List<Patient>>getAllPatients(){
        return new ResponseEntity<>(patientServ.getAllPatients(),HttpStatus.OK);
    }

    // A1: Find patients by last name
    @GetMapping("/by-last-name")
    public ResponseEntity<List<Patient>> getPatientsByLastName(@RequestParam String lastName) {
        return new ResponseEntity<>(patientServ.getPatientsByLastName(lastName), HttpStatus.OK);
    }

    @GetMapping(value ="/all/{id}")
    public ResponseEntity<?>getPatientById(@PathVariable UUID id){
        Optional<Patient>patient = patientServ.getPatientById(id);
        if (patient.isPresent()) {
            return new ResponseEntity<>(patient.get(),HttpStatus.OK);
        }
        return new ResponseEntity<>("Patient not found",HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?>updatePatient(@PathVariable UUID id , @RequestBody Patient patient){
        String message = patientServ.updatePatient(id,patient);
        if (message.equals("Patient Updated Successfully")) {
            return new ResponseEntity<>(message,HttpStatus.OK);
        }
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable UUID id){
        String message = patientServ.deletePatient(id);
        if (message.equals("Patient Deleted Successfully")) {
            return new ResponseEntity<>(message,HttpStatus.OK);
        }
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
    }
   // B4: Patients of a doctor
    @GetMapping("/of-doctor/{doctorId}")
    public ResponseEntity<?> getPatientsOfDoctor(@PathVariable UUID doctorId) {
        Object result = patientServ.getPatientsOfDoctor(doctorId);
        
        if (result instanceof String) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); 
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    // C2: Frequent patients
    @GetMapping("/frequent")
    public ResponseEntity<List<Patient>> getFrequentPatients(@RequestParam long min) {
        return new ResponseEntity<>(patientServ.getFrequentPatients(min), HttpStatus.OK);
    }
}