package com.sistemapdv.backend.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;

public class SecretGenerator {

    public static void main(String[] args){

        String secret = Base64.getEncoder()
                .encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());

        System.out.println(secret);
    }
}
