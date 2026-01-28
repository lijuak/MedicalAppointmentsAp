package com.medicalapp.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.medicalapp.model.Patient;
import com.medicalapp.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyTokenAndLogin(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("token");
        System.out.println("=== VERIFY TOKEN CALLED ===");
        System.out.println("Token received (first 50 chars): " + (idToken != null ? idToken.substring(0, Math.min(50, idToken.length())) : "NULL"));
        try {
            // Firebase Verification
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            System.out.println("Firebase verification SUCCESS - UID: " + uid + ", Email: " + email);

            Patient patient = patientService.findOrCreatePatient(uid, email, name);
            System.out.println("Patient created/found: " + patient.getId());

            return ResponseEntity.ok(patient);

        } catch (Exception e) {
            System.err.println("Firebase verification FAILED: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication Error: " + e.getMessage());
        }
    }
}
