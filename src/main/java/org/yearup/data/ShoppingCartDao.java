package org.yearup.data;

import org.yearup.models.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartDao {
    List<ShoppingCartItem> getAllCarts(int userId);
    void addOnCarts (int user_id,int product_id);
    void deleteCarts(int user_id);



    // add additional method signatures here
    //add Product
    // update quantity
    // clear cart
}
