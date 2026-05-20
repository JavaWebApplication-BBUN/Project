package com.busticket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = BusTicketApplication.class)
public class DatabaseConnectionTest {

    @Test
    void contextLoadsAndGeneratesSchema() {

        System.out.println("=================================================");
        System.out.println("Genarate DB Complete");
        System.out.println("=================================================");
    }
}