package DataBase;

public class User {
    private final String email;
    private final String first_name;
    private final String last_name;
    private final String password;
    private final String role;

    public User(String e, String f, String l, String p, String r){
        email = e;
        first_name = f;
        last_name = l;
        password = p;
        role = r;
    }
    public User(){
        email = "";
        first_name = "";
        last_name = "";
        password = "";
        role = "user";
    }

    public String getEmail(){
        return email;
    }
    public String getFirst_name(){
        return first_name;
    }
    public String getLast_name(){
        return last_name;
    }
    public String getPassword(){
        return password;
    }
    public String getRole(){
        return role;
    }
}
