package org.yearup.data.mysql;

import org.springframework.stereotype.Repository;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//@Component
@Repository
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {




    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    { List<Category> categories = new ArrayList<>();

        String query = "SELECT category_id AS categoryId, name, description " +
                "FROM categories";


        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next())
            {
                categories.add(mapRow(resultSet));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error Pulling categories", e);
        }

        return categories;

    }

    @Override
    public Category getById(int categoryId)
    {
        String query = "SELECT category_id AS categoryId, name, description " +
                "FROM categories WHERE category_id = ? ";



        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, categoryId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapRow(results);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting album by ID", e);
        }

        return null;
    }

    @Override
    public Category create(Category category)
    {

        String query = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int categoryId = keys.getInt(1);
                    return getById(categoryId);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating category", e);
        }

        return null;
    }




    @Override
    public Category update(int categoryId, Category category)
    {
        String query = "UPDATE categories " +
                "SET name = ?, description = ? " +
                "WHERE category_id = ?";

        try(Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,category.getDescription());
            preparedStatement.setString(2,category.getDescription());
            preparedStatement.setInt(3,category.getCategoryId());

            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected == 0){
                return null;
            }

            return getById(categoryId);


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
//completed
    @Override
    public boolean delete(int categoryId) {

        String query = "DELETE FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, categoryId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category", e);
        }

    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("categoryId");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
