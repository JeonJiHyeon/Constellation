package org.techtown.Constellation.link;

public class UserObj {
    private String name;
    private String email;
    private String g_code;

    public String getname(){
        return name;
    }

    public String getEmail(){
        return email;
    }
    public String getG_code(){
        return g_code;
    }

    public void setname(String data){
        this.name = data;
    }

    public void setEmail(String data){
        this.email = data;
    }
    public void setGroup_code(String data){
        this.g_code = data;
    }
}
