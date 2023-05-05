package va.vanthe.app_chat_2.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.PropertyName;
import com.google.j2objc.annotations.Property;

import org.checkerframework.common.aliasing.qual.Unique;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Entity
public class User implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String firstName;
    private String lastName;
    private String password;
    private String image;
    private String backgroundImage;
    private String email;
    private String describe;
    @Unique
    private String phoneNumber;
    private boolean sex;
    private String dateOfBrith;
    private String fcmToken;

    public User() {}

    public User(@NonNull String id, String firstName, String lastName, String password, String image, String backgroundImage, String email, String describe, @Unique String phoneNumber, boolean sex, String dateOfBrith, String fcmToken) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.image = image;
        this.backgroundImage = backgroundImage;
        this.email = email;
        this.describe = describe;
        this.phoneNumber = phoneNumber;
        this.sex = sex;
        this.dateOfBrith = dateOfBrith;
        this.fcmToken = fcmToken;
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

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
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

    public String getDateOfBrith() {
        return dateOfBrith;
    }

    public void setDateOfBrith(String dateOfBrith) {
        this.dateOfBrith = dateOfBrith;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                ", backgroundImage='" + backgroundImage + '\'' +
                ", email='" + email + '\'' +
                ", describe='" + describe + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", sex=" + sex +
                ", dateOfBrith='" + dateOfBrith + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                '}';
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("password", password);
        userMap.put("image", image);
        userMap.put("backgroundImage", backgroundImage);
        userMap.put("email", email);
        userMap.put("describe", describe);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("sex", sex);
        userMap.put("dateOfBrith", dateOfBrith);
        return userMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    public boolean isEmpty() {
        if (Objects.isNull(id) || id.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

}