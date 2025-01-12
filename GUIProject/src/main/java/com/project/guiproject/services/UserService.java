package com.project.guiproject.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.project.guiproject.models.Match;
import com.project.guiproject.models.User;
import com.project.guiproject.utils.MyDataBase;


public class UserService implements IService<User> {

    private Connection connection;

    public UserService() {
        connection = MyDataBase.getInstace().getConnection();
    }

    @Override
    public void add(User User) throws SQLException {
        String req = "INSERT INTO user (id,username,password,email,role) VALUES ('"
                + User.getId() + "', '" + User.getUsername() + "', '" + User.getPassword() + "', '"
                + User.getEmail() + "', '" + User.getRole() +"')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
    }

    @Override
    public void update(User User) throws SQLException {
        String req = "UPDATE user SET id = ?,username = ?, password = ? ,email = ? WHERE id = ?";
        PreparedStatement PS = connection.prepareStatement(req);
        PS.setInt(1, User.getId());
        PS.setString(2, User.getUsername());
        PS.setString(3, User.getPassword());
        PS.setString(4, User.getEmail());
        PS.setInt(5, User.getId());
        PS.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String req = "DELETE FROM user WHERE id = ?";
        PreparedStatement PS = connection.prepareStatement(req);
        try {
            PS.setInt(1, id);
            PS.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public User getById(int id) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";
        PreparedStatement PS = connection.prepareStatement(query);
        PS.setInt(1, id);
        PS.executeQuery();
        ResultSet RS = PS.getResultSet();
        return RS.next()
                ? new User(RS.getInt("id"), RS.getString("username"), RS.getString("password"), RS.getString("email"))
                : null;

    }



    @Override
    public List<User> get() throws SQLException {
        List<User> User = new ArrayList<>();
        String query = "SELECT * FROM user";
        PreparedStatement PS = connection.prepareStatement(query);
        PS.executeQuery();
        ResultSet RS = PS.getResultSet();
        while (RS.next()) {
            User.add(new User(RS.getInt("id"), RS.getString("username"), RS.getString("password"),
                    RS.getString("email")));
        }
        return User;
    }

    // Login and Signup Methods
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // User found, return the User object
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("email"), rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Re-throw the exception to handle it in the caller method
        }
        return null; // Return null if login fails
    }

    public void signup(String username, String password, String email) throws SQLException {
        User user = new User(username, password, email);
        add(user);
    }



}
