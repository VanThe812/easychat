package va.vanthe.app_chat_2.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.checkerframework.common.aliasing.qual.Unique;

import java.util.Date;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    private String firstName;
    private String lastName;
    private String password;
    private String image;
    private String email;
    @Unique
    private String phoneNumber;
    private boolean sex;
    private Date dateOfBrith;

    public User() {}

    public User(String id, String firstName, String lastName, String password, String image, String email, @Unique String phoneNumber, boolean sex, Date dateOfBrith) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.image = image;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.sex = sex;
        this.dateOfBrith = dateOfBrith;
    }

    public String getId() {
        return id;
    }

    public void setId(String userId) {
        this.id = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNUmber) {
        this.phoneNumber = phoneNUmber;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public Date getDateOfBrith() {
        return dateOfBrith;
    }

    public void setDateOfBrith(Date dateOfBrith) {
        this.dateOfBrith = dateOfBrith;
    }
}