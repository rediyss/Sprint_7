package ru.praktikum_services.qa_scooter.tests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUser {
    private String login;
    private String password;
    private String firstName;
}
