package auca.example.clinicSystem.controller;

import java.util.*;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import auca.example.clinicSystem.model.Doctor;
import auca.example.clinicSystem.service.DoctorService;

@RestController
@RequestMapping(value = "/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorServ;

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        String message = doctorServ.saveDoctor(doctor);
        if (message.equals(DoctorService.SAVED)) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        if (message.equals(DoctorService.OFFICE_NOT_FOUND)) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return new ResponseEntity<>(doctorServ.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id) {
        Optional<Doctor> doctor = doctorServ.getDoctorById(id);
        if (doctor.isPresent()) {
            return new ResponseEntity<>(doctor.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(DoctorService.NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/name")
    public ResponseEntity<?> getDoctorsByName(@RequestParam String name) {
        return new ResponseEntity<>(doctorServ.getDoctorsByName(name), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable UUID id, @RequestBody Doctor doctor) {
        String message = doctorServ.updateDoctor(id, doctor);
        if (message.equals(DoctorService.UPDATED)) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        if (message.equals(DoctorService.OFFICE_TAKEN)) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable UUID id) {
        String message = doctorServ.deleteDoctor(id);
        if (message.equals(DoctorService.DELETED)) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/by-specialization")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialization(@RequestParam String name) {
        return new ResponseEntity<>(doctorServ.getDoctorsBySpecialization(name), HttpStatus.OK);
    }

    @GetMapping("/without-office")
    public ResponseEntity<List<Doctor>> getDoctorsWithoutOffice() {
        return new ResponseEntity<>(doctorServ.getDoctorsWithoutOffice(), HttpStatus.OK);
    }

    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String msg = doctorServ.addSpecialization(doctorId, specializationId);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.contains("already has")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> removeSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String msg = doctorServ.removeSpecialization(doctorId, specializationId);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.contains("does not have")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<String> moveDoctor(@PathVariable UUID doctorId, @RequestParam String officeNumber) {
        String msg = doctorServ.moveDoctor(doctorId, officeNumber);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.contains("taken")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }
    
    @GetMapping("/{doctorId}/agenda")
    public ResponseEntity<?> getDoctorAgenda(@PathVariable UUID doctorId, @RequestParam String date) {
        Object result = doctorServ.getDoctorAgenda(doctorId, LocalDate.parse(date));
        if (result instanceof String) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); 
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    
    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctors(@RequestParam String name) {
        return new ResponseEntity<>(doctorServ.searchDoctors(name), HttpStatus.OK);
    }
}