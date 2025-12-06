package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Contact {

    private int contactId;
    private String firstName;
    private String lastName;
    private String nickname;
    private String phonePrimary;
    private String email;
    private Date birthDate;
    private Date createdAt;
    private Date updatedAt;

    /**
     * Get contact id.
     * @author Mert Bölükbaşı
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * @param contactId Contact id
     * Set contact id.
     * @author Mert Bölükbaşı
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Get first name.
     * @author Mert Bölükbaşı
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName First name
     * Set first name.
     * @author Mert Bölükbaşı
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get last name.
     * @author Mert Bölükbaşı
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName Last name
     * Set last name.
     * @author Mert Bölükbaşı
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get nickname.
     * @author Mert Bölükbaşı
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname Nickname
     * Set nickname.
     * @author Mert Bölükbaşı
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Get primary phone.
     * @author Mert Bölükbaşı
     */
    public String getPhonePrimary() {
        return phonePrimary;
    }

    /**
     * @param phonePrimary Primary phone
     * Set primary phone.
     * @author Mert Bölükbaşı
     */
    public void setPhonePrimary(String phonePrimary) {
        this.phonePrimary = phonePrimary;
    }

    /**
     * Get email.
     * @author Mert Bölükbaşı
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email Email
     * Set email.
     * @author Mert Bölükbaşı
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get birthdate.
     * @author Mert Bölükbaşı
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate Birthdate
     * Set birthdate.
     * @author Mert Bölükbaşı
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Get created date.
     * @author Mert Bölükbaşı
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt Created date
     * Set created date.
     * @author Mert Bölükbaşı
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get updated date.
     * @author Mert Bölükbaşı
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt Updated date
     * Set updated date.
     * @author Mert Bölükbaşı
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}