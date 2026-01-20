package com.medicalapp.serviceImpl;

import org.springframework.stereotype.Service;

import com.medicalapp.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@Service
public class FirebaseServiceImpl implements FirebaseService {

    @Override
    public FirebaseToken verifyToken(String idToken) throws Exception {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }
}
