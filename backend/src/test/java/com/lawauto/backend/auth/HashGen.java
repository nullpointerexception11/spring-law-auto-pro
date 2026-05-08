package com.lawauto.backend.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pass = "superadmin18695531334";
        System.out.println("HASH:" + encoder.encode(pass));
    }
}
