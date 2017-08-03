
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

    public String getLogin() {
        return (String) super.getProperty("login");
    }

    public void setLogin(String login) {
        super.setProperty("login", login);
    }

    public String getOwnerId() {
        return (String) super.getProperty("ownerId");
    }

    public void setOwnerId(String ownerId) {
        super.setProperty("ownerId", ownerId);
    }

    public String getObjectId() {
        return (String) super.getProperty("objectId");
    }

    public void setObjectId(String objectId) {
        super.setProperty("objectId", objectId);
    }

    public java.util.Date getUpdated() {
        return (java.util.Date) super.getProperty("updated");
    }

    public void setUpdated(java.util.Date updated) {
        super.setProperty("updated", updated);
    }

    public String getPhone() {
        return (String) super.getProperty("phone");
    }

    public void setPhone(String phone) {
        super.setProperty("phone", phone);
    }

    public String getFirstName() {
        return (String) super.getProperty("firstName");
    }

    public void setFirstName(String firstName) {
        super.setProperty("firstName", firstName);
    }

    public String getSecondName() {
        return (String) super.getProperty("secondName");
    }

    public void setSecondName(String secondName) {
        super.setProperty("secondName", secondName);
    }
}