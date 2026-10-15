
package auca.example.clinicSystem.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import auca.example.clinicSystem.Repository.OfficeRepository;
import auca.example.clinicSystem.model.Office;
import java.util.*;
@Service
public class OfficeService {   
    @Autowired
    private OfficeRepository officeRepo;
    public String saveOffice(Office office){
        if(officeRepo.findByOfficeNumber(office.getOfficeNumber()) != null ){
            return "Office number already exists";
        }
        officeRepo.save(office);
        return "Office saved successfully";
    }
   public List <Office> getAllOffices(){
    return officeRepo.findAll();
   }
   public Optional<Office> getOfficeById(UUID id){
    return officeRepo.findById(id);
   }
   public String updateOffice(UUID id,Office updated){
    Optional <Office> existing = officeRepo.findById(id);
    if(existing.isEmpty()){
        return "Office not Found";
    }
    Office office = existing.get();
    office.setName(updated.getName());
    office.setOfficeNumber(updated.getOfficeNumber());
    office.setFloor(updated.getFloor());
    officeRepo.save(office);
    return "Office Updated Successfully";
   }
   public String deleteOffice(UUID id){
    if (!officeRepo.existsById(id)) {
        return "Office not found";
    }officeRepo.deleteById(id);
    return "Office deleted Successfully";
   }
   
    public Object getBusiestOffice() {
        List<Object[]> offices = officeRepo.findBusiestOffices();
        if (offices.isEmpty()) {
            return "No appointments yet";
        }
        
        
        Object[] topOffice = offices.get(0);
        String name = (String) topOffice[0];
        Integer number = (Integer) topOffice[1];
        Long count = (Long) topOffice[2];
        
        return "Office: " + name + " (Number " + number + ") with " + count + " appointments";
    }
    
    public List<Office> getFreeOffices() {
        return officeRepo.findFreeOffices();
    }
}