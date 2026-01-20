package com.medicalapp.service;

import com.google.firebase.auth.FirebaseToken;

public interface FirebaseService {
    FirebaseToken verifyToken(String idToken) throws Exception;
}
