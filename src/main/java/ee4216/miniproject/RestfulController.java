/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee4216.miniproject;

/**
 *
 * @author 55212718
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.Integer.parseInt;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.PreparedStatement;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.apache.commons.codec.digest.DigestUtils;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(path="/api")
public class RestfulController {
    
    Map tokenManager = new HashMap();
   
    @GetMapping("/status")        
    public String status() 
    {
        return "Answer to the Ultimate Question of Life, the Universe, and Everything is 42";
    }
    //@GetMapping(value = "/showsAllmemo", produces = MediaType.APPLICATION_JSON_VALUE)
    public String JsonAllmemo() throws Exception{
        List<Notes> tempresult = this.retrieveAll();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(tempresult);
    }
    
    public List<Notes> retrieveAll() throws SQLException
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:derby://localhost:1527/memotest1";
        String user = "Orz";
        String password = "Orz";

        List<Notes> result = new ArrayList<Notes>();
        try
        {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM memodb");

            while(rs.next())
            {
                int id = rs.getInt("id");
                String token = rs.getString("token");
                String content = rs.getString("content");
                String color = rs.getString("color");
                int font_size = rs.getInt("font_size");
                
                Notes tempNote = new Notes(token,content,id,color,font_size); 
                result.add(tempNote);

            }
            //System.out.println(result);
            return result;
        }
        catch(SQLException e)
        {
            return null;
        }
    }
    
    @PostMapping(value = "/memo", produces = MediaType.APPLICATION_JSON_VALUE)
    public String memobyToken(@RequestBody String userDetails) throws SQLException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(userDetails, new TypeReference<HashMap<String, Object>>() {}); 
        String tk = (String)data.get("token");
        String tokenpw = (String)data.get("tokenpw");
        //System.out.println(tokenManager.get(tk));
        if (tokenpw.length()!=64 || !tokenManager.get(tk).equals(tokenpw))
            return "Not Authorized";
        List<Notes> temp = retrievebyToken(tk);
        mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(temp);
        return result;        
    }    
    
    public List<Notes> retrievebyToken(String tk) throws SQLException {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:derby://localhost:1527/memotest1";
        String user = "Orz";
        String password = "Orz";

        List<Notes> result = new ArrayList<Notes>();
        try
        {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            PreparedStatement statement = con.prepareStatement("SELECT * FROM memodb where token=?");
            statement.setString(1, tk);
            rs = statement.executeQuery();
            
            while(rs.next())
            {
                int id = rs.getInt("id");
                String token = rs.getString("token");
                String content = rs.getString("content");
                String color = rs.getString("color");
                int font_size = rs.getInt("font_size");
                
                Notes tempNote = new Notes(token,content,id,color,font_size);
                //System.out.println(tempNote.toText());
                result.add(tempNote);
                
            }
            //System.out.println(result);
            return result;
        }
        catch(SQLException e)
        {
            return null;
        }
    }

    @PostMapping("/NewUser")  
    public String newUser(@RequestBody String userDetails) throws JsonProcessingException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(userDetails, new TypeReference<HashMap<String, Object>>() {}); 
        String username = (String)data.get("username");
        String PW = (String)data.get("password");
        User tempuser = new User(username,PW);
        if(addUser(tempuser))
            return "done";
        return "Fail, username already exist or internal error";
    }
    
    public boolean addUser(User temp)throws SQLException {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        String url = "jdbc:derby://localhost:1527/memotest1";
        String user = "Orz";
        String password = "Orz";
        
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
        rs = st.executeQuery("SELECT username FROM userlist"); 
        Set<String> tempset = new HashSet<String>();
        while (rs.next())
        {   
            //System.out.println(rs.getString("username"));
            tempset.add(rs.getString("username"));
        }
        if(tempset.contains(temp.getusername()))
            return false;
        PreparedStatement statement = con.prepareStatement
            ("insert into userlist (username, password, token)" + " VALUES (?,?,?)");
        //System.out.println(temp.getusername());
        statement.setString(1, temp.getusername()); 
        statement.setString(2, temp.getPW()); 
        statement.setString(3, temp.getToken()); 
        statement.executeUpdate();
        return true;
    }
    
    @PostMapping("/GetMyToken")
    public String getToken(@RequestBody String userDetails) throws JsonProcessingException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(userDetails, new TypeReference<HashMap<String, Object>>() {});
        String username = (String)data.get("username");
        String pw = (String)data.get("password");
        return hellotoken(username,pw);
    }
    
    public String hellotoken(String ID,String PW) throws SQLException {
        String result = "GGWP";
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:derby://localhost:1527/memotest1";
        String user = "Orz";
        String password = "Orz";
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
        
        PreparedStatement statement = con.prepareStatement("SELECT TOKEN FROM userlist WHERE username = ? AND password = ?");
        statement.setString(1, ID);
        statement.setString(2, PW);
        rs = statement.executeQuery(); 
        while (rs.next())
        {   
            result = rs.getString("token");
        }
        return result;
    }
    
    @PostMapping("/GetMyTokenpw")
    public String getTokenpw(@RequestBody String userDetails) throws JsonProcessingException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(userDetails, new TypeReference<HashMap<String, Object>>() {});
        String username = (String)data.get("username");
        String pw = (String)data.get("password");
        return hellotokenpw(username,pw);
    }
    
    public String hellotokenpw(String ID,String PW) throws SQLException {
        String token = hellotoken(ID,PW);
        if (token.length()!=64)
            return "what are you doing";
        String tempPW = DigestUtils.sha256Hex(token + Double.toString(Math.random()));
        tokenManager.put(token, tempPW);
        //System.out.println(tempPW);
        return tempPW;
        
    }
    
    @PostMapping("/postMemo")
    public String postMemoAPI(@RequestBody String userDetails) throws JsonProcessingException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(userDetails, new TypeReference<HashMap<String, Object>>() {});
        String token = (String)data.get("token");
        String content = (String)data.get("content");
        String color = (String)data.get("color");
        int font_size = parseInt((String)data.get("font_size"));
        if(postMemo(token,content,color,font_size))
            return "cool";
        return "fail";
    }
    
    public boolean postMemo(String token, String content,String color,int font_size) throws SQLException{
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:derby://localhost:1527/memotest1";
        String user = "Orz";
        String password = "Orz";
        
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
        
        PreparedStatement statement = con.prepareStatement
            ("insert into memodb (token, content,color,font_size)" + " VALUES (?,?,?,?)");
        statement.setString(1, token);
        statement.setString(2, content);
        statement.setString(3, color);
        statement.setInt(4, font_size);
        statement.executeUpdate(); 
        return true;
    }
    
    @PostMapping("/updateMemo")
    public String updateMemoAPI(@RequestBody String userDetails) throws JsonProcessingException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(userDetails, new TypeReference<HashMap<String, Object>>() {});
        int id = parseInt((String)data.get("id"));
        String token = (String)data.get("token");
        String newC = (String)data.get("content");
        String tokenpw = (String)data.get("tokenpw");
        String color = (String)data.get("color");
        int font_size = parseInt((String)data.get("font_size"));
        if (tokenpw.length()!=64 || !tokenManager.get(token).equals(tokenpw))
            return "Not Authorized";
        //System.out.println(id + color + font_size);
        if(updateMemo(id,token,newC,color,font_size))
            return "changed";
        return "update fail";
    }
    
    public boolean updateMemo(int id,String token,String content,String color,int font_size) throws SQLException{
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:derby://localhost:1527/memotest1";
        String user = "Orz";
        String password = "Orz";
        
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
        
        PreparedStatement statement = con.prepareStatement
            ("update memodb SET content=?, color=?, font_size=? where id=? AND token=?");
        statement.setString(1,content);
        statement.setString(2, color);
        statement.setInt(3, font_size);
        statement.setInt(4, id);
        statement.setString(5, token);
        statement.executeUpdate(); 
        return true;
    }
    
    @PostMapping("/deleteMemo")
    public String deleteMemoAPI(@RequestBody String userDetails) throws JsonProcessingException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(userDetails, new TypeReference<HashMap<String, Object>>() {});
        int id = parseInt((String)data.get("id"));
        String token = (String)data.get("token");
        String tokenpw = (String)data.get("tokenpw");
        if (tokenpw.length()!=64 || !tokenManager.get(token).equals(tokenpw))
            return "Not Authorized";
        if(deleteMemo(id,token))
            return "it is goen";
        return "delete fail";
    }
    
    public boolean deleteMemo(int id,String token) throws SQLException{
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:derby://localhost:1527/memotest1";
        String user = "Orz";
        String password = "Orz";
        
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
        
        PreparedStatement statement = con.prepareStatement
            ("delete from memodb where id=? AND token=?");
        statement.setInt(1, id);
        statement.setString(2, token);
        statement.executeUpdate(); 
        return true;
    }
}
