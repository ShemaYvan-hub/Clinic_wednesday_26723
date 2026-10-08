package auca.example.clinicSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import auca.example.clinicSystem.Repository.SpecializationRepository;
import auca.example.clinicSystem.model.Specialization;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specRepo;

    public String saveSpecialization(Specialization specialization) {
        specRepo.save(specialization);
        return "Specialization saved successfully";
    }

    public List<Specialization> getAllSpecializations() {
        return specRepo.findAll();
    }

    public Optional<Specialization> getSpecializationById(UUID id) {
        return specRepo.findById(id);
    }
    
    public List<Specialization> getUnusedSpecializations() {
        return specRepo.findUnusedSpecializations();
    }
    
}