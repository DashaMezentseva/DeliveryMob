package mezentseva.com.android.delivery.Model;

public class user {

    private String name;
    private String password;

    public user() {
    }

    public user(String Pname, String Ppassword) {


        name = Pname;
        password = Ppassword;
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