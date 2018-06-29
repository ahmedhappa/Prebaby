package com.usama.salamtek.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.PrivateKey;

public class User implements Parcelable {
    private String email, name, age, mobile, pass, country, city, image, childDateOfBirth, currWeight;
    private int id;

    public int getId() {
        return id;
    }

    public String getChildDateOfBirth() {
        return childDateOfBirth;
    }

    public void setChildDateOfBirth(String childDateOfBirth) {
        this.childDateOfBirth = childDateOfBirth;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User() {

    }

    protected User(Parcel in) {
        email = in.readString();
        name = in.readString();
        age = in.readString();
        mobile = in.readString();
        pass = in.readString();
        country = in.readString();
        city = in.readString();
        image = in.readString();
        id = in.readInt();
        childDateOfBirth = in.readString();
        currWeight = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(age);
        parcel.writeString(mobile);
        parcel.writeString(pass);
        parcel.writeString(country);
        parcel.writeString(city);
        parcel.writeString(image);
        parcel.writeInt(id);
        parcel.writeString(childDateOfBirth);
        parcel.writeString(currWeight);
    }

    public String getCurrWeight() {
        return currWeight;
    }

    public void setCurrWeight(String currWeight) {
        this.currWeight = currWeight;
    }
}
