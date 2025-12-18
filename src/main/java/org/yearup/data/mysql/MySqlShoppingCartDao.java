package org.yearup.data.mysql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<ShoppingCartItem> getAllCarts(int userId) {

        List<ShoppingCartItem> items = new ArrayList<>();

        String query = """
                    SELECT
                        p.product_id,
                        p.name,
                        p.price,
                        p.category_id,
                        p.description,
                        p.subcategory,
                        p.stock,
                        p.image_url,
                        p.featured,
                        s.quantity
                    FROM shopping_cart s
                    JOIN products p ON s.product_id = p.product_id
                    WHERE s.user_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product product = mapProduct(rs);

                int quantity = rs.getInt("quantity");

                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(quantity);
                item.setDiscountPercent(BigDecimal.valueOf(0));

                BigDecimal lineTotal =
                        product.getPrice().multiply(BigDecimal.valueOf(quantity));
                item.setLineTotal(lineTotal);

                items.add(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading shopping cart", e);
        }

        return items;
    }

    public void addOnCarts(int user_id, int product_id) {

        String query =
                "INSERT INTO shopping_cart (user_id, product_id, quantity) " +
                        "VALUES (?, ?, 1) " +
                        "ON DUPLICATE KEY UPDATE quantity = quantity";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {


            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, product_id);


            preparedStatement.executeUpdate();

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(
                    "addOnCarts -> user_id=" + user_id +
                            ", product_id=" + product_id +
                            ", rowsAffected=" + rowsAffected
            );

        } catch (SQLException e) {
            throw new RuntimeException("error loading carts", e);
        }
    }

    public void deleteCarts(int user_id) {

        String query = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, user_id);

            int rowsAffected = preparedStatement.executeUpdate();

            System.out.println(
                    "deleteCarts -> user_id=" + user_id +
                            ", rowsAffected=" + rowsAffected
            );

        } catch (SQLException e) {
            throw new RuntimeException("error clearing carts", e);
        }
    }

    protected static Product mapProduct(ResultSet row) throws SQLException {

        Product product = new Product();

        product.setProductId(row.getInt("product_id"));
        product.setName(row.getString("name"));
        product.setPrice(row.getBigDecimal("price"));
        product.setCategoryId(row.getInt("category_id"));
        product.setDescription(row.getString("description"));
        product.setSubCategory(row.getString("subcategory"));
        product.setStock(row.getInt("stock"));
        product.setFeatured(row.getBoolean("featured"));
        product.setImageUrl(row.getString("image_url"));

        return product;
    }



}



