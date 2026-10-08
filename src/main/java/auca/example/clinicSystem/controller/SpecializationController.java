package auca.example.clinicSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import auca.example.clinicSystem.service.SpecializationService;
import auca.example.clinicSystem.model.Specialization;
import java.util.List;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    @Autowired
    private SpecializationService specService;

    @PostMapping("/save")
    public ResponseEntity<?> saveSpecialization(@RequestBody Specialization spec) {
        return new ResponseEntity<>(specService.saveSpecialization(spec), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        return new ResponseEntity<>(specService.getAllSpecializations(), HttpStatus.OK);
    }
    // B3: Specializations nobody offers
   // B3: Specializations nobody offers
    @GetMapping("/unused")
    public ResponseEntity<List<Specialization>> getUnusedSpecializations() {
        return new ResponseEntity<>(specService.getUnusedSpecializations(), HttpStatus.OK);
    }
}