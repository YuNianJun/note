package com.notebook.note_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class NoteBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoteBackApplication.class, args);
    }

}
