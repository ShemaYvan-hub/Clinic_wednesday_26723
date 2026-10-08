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

    // 2. JPQL Query Endpoint
    @GetMapping("/past-pending")
    public ResponseEntity<List<Appointment>> getPastPending() {
        return new ResponseEntity<>(appointmentService.getPastPendingAppointments(), HttpStatus.OK);
    }

    // 3. Aggregate Query Endpoint
    @GetMapping("/busiest-doctors")
    public ResponseEntity<List<Object[]>> getBusiestDoctors() {
        return new ResponseEntity<>(appointmentService.getBusiestDoctors(), HttpStatus.OK);
    }

    // 4. JOIN Query Endpoint
    @GetMapping("/specialization/{specName}")
    public ResponseEntity<List<Appointment>> getBySpecialization(@PathVariable String specName) {
        return new ResponseEntity<>(appointmentService.getAppointmentsBySpecialization(specName), HttpStatus.OK);
    }

    // 5. Pagination Query Endpoint
    @GetMapping("/doctor/{doctorId}/page1")
    public ResponseEntity<Page<Appointment>> getDoctorAppointmentsPage(@PathVariable UUID doctorId) {
        return new ResponseEntity<>(appointmentService.getDoctorAppointmentsPaginated(doctorId), HttpStatus.OK);
    }

 
    @PostMapping("/save")
    public ResponseEntity<String> saveAppointment(@RequestBody Appointment appointment) {
        String result = appointmentService.saveAppointment(appointment);
        
        // Return HTTP 409 CONFLICT if double booked
        if (result.equals("Doctor is already booked on that date")) {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT); 
        }
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // A2: Appointments by status[cite: 2]
    @GetMapping("/by-status")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@RequestParam AppointmentStatus status) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByStatus(status), HttpStatus.OK);
    }

    // A3: Appointments in a date range[cite: 2]
    @GetMapping("/between")
    public ResponseEntity<List<Appointment>> getAppointmentsBetween(
            @RequestParam String start, 
            @RequestParam String end) {
        // Parse the Strings into LocalDate in the controller[cite: 2]
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(appointmentService.getAppointmentsBetween(startDate, endDate), HttpStatus.OK);
    }
// C1: Appointments per status
    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>> getAppointmentsPerStatus() {
        List<Object[]> stats = appointmentService.getAppointmentsPerStatus();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    // C4: Doctor is absent
    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelDoctorAppointmentsForDay(
            @RequestParam UUID doctorId, 
            @RequestParam String date) {
        LocalDate cancelDate = LocalDate.parse(date);
        return new ResponseEntity<>(appointmentService.cancelDoctorAppointmentsForDay(doctorId, cancelDate), HttpStatus.OK);
    }

}