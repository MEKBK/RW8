package com.kbk.model;



import java.io.Serializable;


public class User implements Serializable {
    private static final long serialVersionUID = 5114971728232239721L;
    private Integer id;

    private String image;

    private String username;

    private String email;

    private String phoneNumber;

    private String password;

    private Long createAt;

    private Long updateAt;

    public User(Integer id, String image, String username, String email, String phoneNumber, String password, Long createAt, Long updateAt) {
        this.id = id;
        this.image = image;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public User(){super();}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", image='" + image + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", password='" + password + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
