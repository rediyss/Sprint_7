package ru.praktikum_services.qa_scooter.tests;

public class CreateUser {
    private String password;
    private String firstName;
    private String login;

    public CreateUser(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }
    //Пустой конструктор
    public CreateUser(){
    }


    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
