package com.shoemaster.cartservice.controllers;

import com.shoemaster.cartservice.models.Cart;
import com.shoemaster.cartservice.models.Shoe;
import com.shoemaster.cartservice.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping
    public List<Cart> getAllCarts(){
     return cartService.getAllCarts();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(cartService.getCartByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PostMapping("/{userId}")
//    public ResponseEntity<?> addNewCartItem(@RequestParam("item-id") Long itemId, @PathVariable Long userId) {
//        try {
//            return ResponseEntity.ok(cartService.addCartItem(itemId, userId));
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(e.getMessage());
//        }
//    }
    
    @PostMapping("/{userId}/shoes")
    public ResponseEntity<?> addShoeToCart(@PathVariable ("userId") Long userId, @RequestBody Shoe shoe){
        try {
            return ResponseEntity.ok(cartService.addCartItem(userId, shoe));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

//    @DeleteMapping("/{userId}/{cartItemId}")
//    public ResponseEntity<?> removeCartItem(@PathVariable("cartItemId") Long cartItemId,
//                                            @PathVariable("userId") Long userId,
//                                            @RequestParam("amount") Integer amount) {
//        try {
//            return ResponseEntity.ok(cartService.removeCartItem(cartItemId, userId, amount));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

//    @PatchMapping("/{userId}/{cartItemId}")
//    public ResponseEntity<?> updateItemAmount(@PathVariable("userId") Long userId,
//                                              @PathVariable("cartItemId") Long cartItemId,
//                                              @RequestParam("amount") Integer amount) {
//        try {
//            return ResponseEntity.ok(cartService.updateAmount(userId, cartItemId, amount));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PatchMapping("/{userId}/{cartItemId}")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @RequestParam boolean increase) {

        try {
            // Call the service method to update the quantity
            Cart updatedCart = cartService.updateCartItemQuantity(userId, cartItemId, increase);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long cartItemId) {
        try {
            cartService.deleteCartItem(cartItemId);
            return ResponseEntity.ok("item with " + cartItemId + " deleted");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


//    @PatchMapping("/{userId}/{cartItemId}")
//    public ResponseEntity<Cart> updateCartItemAmount(@PathVariable Long userId,
//                                                     @PathVariable Long cartItemId,
//                                                     @RequestParam Integer amount) {
//        Cart updatedCart = cartService.updateAmount(userId, cartItemId, amount);
//        return ResponseEntity.ok(updatedCart);
//    }


}
