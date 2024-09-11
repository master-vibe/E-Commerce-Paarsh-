package com.paarsh.admin_paarsh.service;

import com.paarsh.admin_paarsh.model.Product;
import com.paarsh.admin_paarsh.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // List all products
    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    // Get a single product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Add or update a product
    public Product saveOrUpdateProduct(Product product) {
        return productRepository.save(product);
    }

    // Delete a product by ID
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Manage stock (increase or decrease stock)
    public void updateStock(Long productId, Integer quantity) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        int newStock = product.getStockQuantity() + quantity;
        if (newStock < 0) {
            throw new Exception("Insufficient stock");
        }

        product.setStockQuantity(newStock);
        productRepository.save(product);
    }

    // Get products in customer’s cart (assuming there's a method to get cart items)
    // Note: Implementation depends on how cart items are managed
    // For simplicity, assume you have a CartService with a method to get products in a cart
    // public List<Product> getProductsInCart(Long customerId) {
    //     return cartService.getProductsInCart(customerId);
    // }
}