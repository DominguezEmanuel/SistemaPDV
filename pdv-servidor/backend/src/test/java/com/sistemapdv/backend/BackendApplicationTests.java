package com.sistemapdv.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {
        "DB_URL_DEV=jdbc:postgresql://localhost:5432/pdv_dev",
        "DB_USERNAME_DEV=postgres",
        "DB_PASSWORD_DEV=admin",
        "JWT_SECRET=xbUMgC1cuauaxExDABSsF4KEsfCXy9MXeV+bhuXC4Xs=",
        "JWT_EXPIRATION=28800000",
        "CLOUDINARY_CLOUD_NAME=w09zsldd",
        "CLOUDINARY_API_KEY=296374333262985",
        "CLOUDINARY_API_SECRET=9V5fzpklGXSwF6m88lXy8IikJYg",
        "EMAIL_HOST=smtp.gmail.com",
        "EMAIL_PORT=587",
        "EMAIL_USER=dmanu401@gmail.com",
        "EMAIL_PASSWORD=qliu itjl iyta uvic",
        "EMAIL_ADMIN=dmanu401@gmail.com"
})
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
