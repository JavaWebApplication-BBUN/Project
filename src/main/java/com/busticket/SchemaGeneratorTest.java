package com.busticket;

import com.busticket.config.HibernateConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SchemaGeneratorTest {
    public static void main(String[] args) {

        // Nạp file cấu hình Hibernate vào Context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(HibernateConfig.class);

        System.out.println("=================================================");
        System.out.println("Generate Table complete");
        System.out.println("=================================================");

        // Đóng context
        context.close();
    }
}