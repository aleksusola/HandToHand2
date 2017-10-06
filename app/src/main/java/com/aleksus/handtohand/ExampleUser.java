
package com.aleksus.handtohand;

import com.backendless.BackendlessUser;

public class ExampleUser extends BackendlessUser {
    public String getEmail() {
        return super.getEmail();
    }

    public void setEmail(String email) {
        super.setEmail(email);
    }

    public String getPassword() {
        return super.getPassword();
    }

    public java.util.Date getCreated() {
        return (java.util.Date) super.getProperty("created");
    }

    public void setCreated(java.util.Date created) {
        super.setProperty("created", created);
    }

    public void setLogin(String login) {
        super.setProperty("login", login);
    }

    public String getObjectId() {
        return (String) super.getProperty("objectId");
    }

    public void setPhone(String phone) {
        super.setProperty("phone", phone);
    }

    public void setFirstName(String firstName) {
        super.setProperty("firstName", firstName);
    }

    public void setSecondName(String secondName) {
        super.setProperty("secondName", secondName);
    }
}