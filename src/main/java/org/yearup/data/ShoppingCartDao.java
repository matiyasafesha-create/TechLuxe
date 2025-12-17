package org.yearup.data;

import org.yearup.models.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartDao {
    List<ShoppingCartItem> getByUserId(int userId);
    // add additional method signatures here
    //add Product
    // update quantity
    // clear cart
}
