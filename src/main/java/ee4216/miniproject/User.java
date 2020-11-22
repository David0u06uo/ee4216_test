package ee4216.miniproject;

import org.apache.commons.codec.digest.DigestUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 123
 */
public class User {
    private String username;
    private String password;
    private String token;
    
    public User(String name,String pw){
        this.username = name;
        this.password = pw;
        token = DigestUtils.sha256Hex(name+pw);
    }
    
    public String getusername(){
        return username;
    }
    
    public String getToken(){
        return token;
    }
    
    public String getPW(){
        return password;
    }
    
    
}
