package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.yearup.models.Category;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Profile> getAllProfiles ()
    { List<Profile> profiles = new ArrayList<>();

        String query = "select user_id,first_name,last_name," +
                "phone,email,address,city,state,zip\n" +
                "from profiles;";

        //where

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next())
            {
                profiles.add(mapRow(resultSet));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error Pulling Profiles", e);
        }

        return profiles;

    }
    @Override
    public List<Profile> getProfilesByUserId(int userId) {
        List<Profile> profiles = new ArrayList<>();

        String sql = """
        SELECT user_id, first_name, last_name,
               phone, email, address, city, state, zip
        FROM profiles
        WHERE user_id = ?
    """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    profiles.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading profile for user " + userId, e);
        }

        return profiles;
    }


    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


    private Profile mapRow (ResultSet row) throws SQLException
    {
        int userId = row.getInt("user_Id");
        String firstName = row.getString("first_Name");
        String lastName = row.getString("last_Name");
        String phone = row.getString("phone");
        String email = row.getString("email");
        String address = row.getString("address");
        String city = row.getString("city");
        String state = row.getString("state");
        String zip = row.getString("zip");
        Profile profile = new Profile()
        {{
           setUserId(userId);
           setFirstName(firstName);
           setLastName(lastName);
           setPhone(phone);
           setEmail(email);
           setAddress(address);
           setCity(city);
           setState(state);
           setZip(zip);
        }};

        return profile;
    }
}
