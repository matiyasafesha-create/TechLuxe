package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
// add annotation to allow cross site origin requests
@RestController
@RequestMapping ("/categories")
@CrossOrigin
public class CategoriesController
{
    private final CategoryDao categoryDao;
    private final ProductDao productDao;


@Autowired
    public CategoriesController (CategoryDao categoryDao,ProductDao productDao){     // added a constructor for Injection Beans
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }


    @GetMapping
    public ResponseEntity<List<Category>> getAllCategory() {
        return ResponseEntity.ok(categoryDao.getAllCategories());
    }




    @GetMapping ("/{id}")
    public ResponseEntity <Category> getById(@PathVariable int id) {

        Category category = categoryDao.getById(id);

        if(category == null){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(category);

    }

           // works HTTP is the port not 'S' website not secured
    @GetMapping("{categoryId}/products")
    public ResponseEntity<List<Product>> getProductsById(@PathVariable int categoryId)
    {
        List<Product> products = productDao.listByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public Category addCategory(@RequestBody Category category) {
        return categoryDao.create(category);
    }



    @PutMapping ("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Category updateCategory(@PathVariable int id, @RequestBody Category category) {

        try {
            Category updatedCategory = categoryDao.update(id, category);

            if (updatedCategory == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"
                );
            }

            return updatedCategory;

        } catch (ResponseStatusException e) {

            throw e;

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Oops... ERROR updating category. Try again.",
                    e
            );
        }
        // update the category by id
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function


    @DeleteMapping ("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable int id)
    {

        try {
            boolean deleted = categoryDao.delete(id);

            if (!deleted) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"
                );
            }

        } catch (ResponseStatusException e) {
            throw e;

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error deleting category",
                    e
            );
        }
    }
}
