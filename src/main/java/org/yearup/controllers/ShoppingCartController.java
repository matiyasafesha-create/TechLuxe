package org.yearup.controllers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;
import java.util.List;

// convert this class to a REST controller
// only logged in users should have access to these actions

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
@CrossOrigin
@Slf4j
public class ShoppingCartController
{
    private static final Logger log = LoggerFactory.getLogger(ShoppingCartController.class);
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    public ShoppingCartController (
            ShoppingCartDao shoppingCartDao,
            UserDao userDao,
            ProductDao productDao
    ){
        this.userDao = userDao;
        this.shoppingCartDao = shoppingCartDao;
        this.productDao = productDao;
    }



    // each method in this controller requires a Principal object as a parameter

    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {

            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            List<ShoppingCartItem> itemList = shoppingCartDao.getAllCarts(user.getId());

            ShoppingCart shoppingCart = new ShoppingCart();

            for(ShoppingCartItem item : itemList){
                shoppingCart.add(item);
            }

            return shoppingCart;
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }



    @PostMapping("/products/{productId}")
    public ResponseEntity<ShoppingCart> addToCart(@PathVariable int productId,
            Principal principal
    ) {

        User user = userDao.getByUserName(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        log.info("Adding product {} to cart for user {}", productId, user.getId());

        shoppingCartDao.addOnCarts(user.getId(), productId);

        List<ShoppingCartItem> itemList =
                shoppingCartDao.getAllCarts(user.getId());

        ShoppingCart cart = new ShoppingCart();
        for (ShoppingCartItem item : itemList) {
            cart.add(item);
        }

        return ResponseEntity.ok(cart);
    }

@DeleteMapping
public ResponseEntity<ShoppingCart> deleteCart(Principal principal) {

    User user = userDao.getByUserName(principal.getName());
    if (user == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    log.info("Clearing cart for user {}", user.getId());


    shoppingCartDao.deleteCarts(user.getId());


    List<ShoppingCartItem> itemList =
            shoppingCartDao.getAllCarts(user.getId());

    ShoppingCart cart = new ShoppingCart();
    for (ShoppingCartItem item : itemList) {
        cart.add(item);
    }

    return ResponseEntity.ok(cart);
}










    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated


    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart

}
