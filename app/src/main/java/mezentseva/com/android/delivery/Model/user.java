package mezentseva.com.android.delivery.Model;

public class user {

    private String name;
    private String password;
    private String phone;
    private String isStaff;

    public user() {
    }

    public user(String Pname, String Ppassword) {
        name = Pname;
        password = Ppassword;
        isStaff = "false";
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {

        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String setname(String Pname) {
        name = Pname;
        return name;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}