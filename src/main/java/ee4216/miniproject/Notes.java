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
    private String color;
    private int font_size;
    
    public Notes(String token,String content,int id){
        this.id = id;
        this.usertoken = token;
        this.content = content;
        this.color = "Grey";
        this.font_size = 1;
    }
    
    public Notes(String token,String content,int id,String color,int size){
        this.id = id;
        this.usertoken = token;
        this.content = content;
        this.color = color;
        this.font_size = size;
    }
    
    /*public String toText(){
        return "ID:" + id + " USER: " +usertoken  + "text:" + content  + " C:" + color + " F:"  +font_size ;
    }*/
    
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
    
    public String getColor()
    {
        return color;
    }
    
    public int getSize()
    {
        return font_size;
    }
    
}
