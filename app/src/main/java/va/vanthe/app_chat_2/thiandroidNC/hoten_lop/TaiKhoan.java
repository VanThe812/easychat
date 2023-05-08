package va.vanthe.app_chat_2.thiandroidNC.hoten_lop;

import java.util.HashMap;

public class TaiKhoan {

    private String id;
    private String ten;
    private String diachi;
    private String phone;
    private String email;
    private String image;

    public TaiKhoan() {}

    public TaiKhoan(String id, String ten, String diachi, String phone, String email, String image) {
        this.id = id;
        this.ten = ten;
        this.diachi = diachi;
        this.phone = phone;
        this.email = email;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> taiKhoanMap = new HashMap<>();
        taiKhoanMap.put("ten", ten);
        taiKhoanMap.put("diachi", diachi);
        taiKhoanMap.put("phone", phone);
        taiKhoanMap.put("email", email);
        taiKhoanMap.put("image", image);
        return taiKhoanMap;
    }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "id='" + id + '\'' +
                ", ten='" + ten + '\'' +
                ", diachi='" + diachi + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
