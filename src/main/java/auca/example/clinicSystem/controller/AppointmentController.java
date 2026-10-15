package auca.example.clinicSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import auca.example.clinicSystem.service.AppointmentService;
import auca.example.clinicSystem.model.Appointment;
import auca.example.clinicSystem.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    
    @GetMapping("/doctor/{doctorId}/pending")
    public ResponseEntity<List<Appointment>> getPendingByDoctor(@PathVariable UUID doctorId) {
        return new ResponseEntity<>(appointmentService.getPendingAppointmentsByDoctor(doctorId), HttpStatus.OK);
    }

  
    @GetMapping("/past-pending")
    public ResponseEntity<List<Appointment>> getPastPending() {
        return new ResponseEntity<>(appointmentService.getPastPendingAppointments(), HttpStatus.OK);
    }

    @GetMapping("/busiest-doctors")
    public ResponseEntity<List<Object[]>> getBusiestDoctors() {
        return new ResponseEntity<>(appointmentService.getBusiestDoctors(), HttpStatus.OK);
    }

    @GetMapping("/specialization/{specName}")
    public ResponseEntity<List<Appointment>> getBySpecialization(@PathVariable String specName) {
        return new ResponseEntity<>(appointmentService.getAppointmentsBySpecialization(specName), HttpStatus.OK);
    }

    
    @GetMapping("/doctor/{doctorId}/page1")
    public ResponseEntity<Page<Appointment>> getDoctorAppointmentsPage(@PathVariable UUID doctorId) {
        return new ResponseEntity<>(appointmentService.getDoctorAppointmentsPaginated(doctorId), HttpStatus.OK);
    }

   
    @GetMapping("/by-status")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@RequestParam AppointmentStatus status) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByStatus(status), HttpStatus.OK);
    }

    
    @GetMapping("/between")
    public ResponseEntity<List<Appointment>> getAppointmentsBetween(
            @RequestParam String start, 
            @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(appointmentService.getAppointmentsBetween(startDate, endDate), HttpStatus.OK);
    }

   
    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>> getAppointmentsPerStatus() {
        List<Object[]> stats = appointmentService.getAppointmentsPerStatus();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    
    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelDoctorAppointmentsForDay(
            @RequestParam UUID doctorId, 
            @RequestParam String date) {
        LocalDate cancelDate = LocalDate.parse(date);
        return new ResponseEntity<>(appointmentService.cancelDoctorAppointmentsForDay(doctorId, cancelDate), HttpStatus.OK);
    }



   
    
    @PostMapping("/save")
    public ResponseEntity<String> saveAppointment(@RequestBody Appointment appointment) {
        String result = appointmentService.saveAppointment(appointment);
        
        if (result.equals("Appointment date cannot be in the past")) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST); 
        }
        if (result.equals("Doctor is already booked on that date")) {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT); 
        }
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateAppointmentStatus(
            @PathVariable UUID id, 
            @RequestParam AppointmentStatus status) {
        
        String result = appointmentService.updateAppointmentStatus(id, status);
        
        if (result.contains("does not exist")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); 
        }
        if (result.contains("Cannot change status")) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<>(result, HttpStatus.OK); 
    }

    
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<String> rescheduleAppointment(
            @PathVariable UUID id, 
            @RequestParam String newDate) {
        
        LocalDate date = LocalDate.parse(newDate);
        String result = appointmentService.rescheduleAppointment(id, date);
        
        if (result.contains("does not exist")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); 
        }
        if (result.contains("cannot be rescheduled") || result.contains("past")) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST); 
        }
        if (result.contains("booked")) {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT); 
        }
        
        return new ResponseEntity<>(result, HttpStatus.OK); 
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable UUID id) {
        String result = appointmentService.deleteAppointment(id);
        if (result.contains("does not exist")) return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        if (result.contains("has prescriptions")) return new ResponseEntity<>(result, HttpStatus.CONFLICT); 
        return new ResponseEntity<>(result, HttpStatus.OK); 
    }
}