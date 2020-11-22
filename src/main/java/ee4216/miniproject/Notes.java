package ee4216.miniproject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 123
 */
public class Notes {
    private int id;
    private String usertoken;
    private String content;
    
    public Notes(String token,String content,int id){
        this.id = id;
        this.usertoken = token;
        this.content = content;
    }
    public int getId()
    {
        return id;
    }

    public String getToken()
    {
        return usertoken;
    }
    
    public String getContent()
    {
        return content;
    }
    
}
