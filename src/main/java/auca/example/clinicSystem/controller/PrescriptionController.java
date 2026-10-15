package auca.example.clinicSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import auca.example.clinicSystem.service.PrescriptionService;
import auca.example.clinicSystem.model.Prescription;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    
    @PostMapping("/save")
    public ResponseEntity<String> savePrescription(
            @RequestParam UUID appointmentId, 
            @RequestBody Prescription prescription) {
        
        String result = prescriptionService.writePrescription(appointmentId, prescription);
        
        if (result.contains("does not exist")) return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); 
        if (result.contains("only for COMPLETED")) return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST); 
        
        return new ResponseEntity<>(result, HttpStatus.CREATED); 
    }

    
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getPrescriptionsByAppointment(@PathVariable UUID appointmentId) {
        Object result = prescriptionService.getPrescriptionsByAppointment(appointmentId);
        
        if (result instanceof String) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); 
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    
    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<List<Prescription>> getPatientHistory(@PathVariable String ssNumber) {
        return new ResponseEntity<>(prescriptionService.getPatientPrescriptionHistory(ssNumber), HttpStatus.OK);
    }

    
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updatePrescription(
            @PathVariable UUID id, 
            @RequestBody Prescription prescription) {
        
        String result = prescriptionService.updatePrescription(id, prescription);
        if (result.contains("does not exist")) return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); 
        return new ResponseEntity<>(result, HttpStatus.OK); 
    }

   
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePrescription(@PathVariable UUID id) {
        String result = prescriptionService.deletePrescription(id);
        if (result.contains("does not exist")) return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); 
        return new ResponseEntity<>(result, HttpStatus.OK); 
    }
}