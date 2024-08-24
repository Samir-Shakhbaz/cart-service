package com.shoemaster.cartservice.services;

import com.shoemaster.cartservice.models.Cart;
import com.shoemaster.cartservice.models.CartItem;
import com.shoemaster.cartservice.models.Shoe;
import com.shoemaster.cartservice.repositories.CartItemRepository;
import com.shoemaster.cartservice.repositories.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = Cart.builder().userId(userId).build();
            cart = cartRepository.save(cart);
        }
        return cart;
    }

//    @Transactional
//    public Cart addCartItem(Long userId, Shoe shoe) {
//        Cart cart = cartRepository.findByUserId(userId);
//
//        // Initialize the cart if it doesn't exist
//        if (cart == null) {
//            cart = new Cart();
//            cart.setUserId(userId);
//            cart.setShoes(new ArrayList<>()); // Initialize the list of shoes
//            cart = cartRepository.save(cart); // Save the new cart to the database
//        }
//
//        // Check if the shoe is already in the cart
//        for (CartItem item : cart.getShoes()) {
//            if (item.getShoeId().equals(shoe.getShoeId())) {
//                item.setAmount(item.getAmount() + 1);
//                return cartRepository.save(cart);
//            }
//        }
//
//        // If the shoe is not in the cart, add it
//        cart.addCartItem(shoe.getShoeId());
//        return cartRepository.save(cart);
//    }


    @Transactional
    public Cart addCartItem(Long userId, Shoe shoe) {
        Cart cart = cartRepository.findByUserId(userId);

        // Initialize the cart if it doesn't exist
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setShoes(new ArrayList<>()); // Initialize the list of CartItems
            cart = cartRepository.save(cart); // Save the new cart to the database
        }

        // Check if the shoe is already in the cart
        CartItem existingItem = cart.getShoes().stream()
                .filter(item -> item.getShoeId().equals(shoe.getShoeId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Increase the amount of the existing CartItem
            existingItem.setAmount(existingItem.getAmount() + 1);
        } else {
            // Create a new CartItem and add it to the cart
            CartItem newCartItem = new CartItem();
            newCartItem.setShoeId(shoe.getShoeId());
            newCartItem.setAmount(1); // Set initial amount
            newCartItem.setCart(cart); // Associate with the cart
            cart.getShoes().add(newCartItem);
        }

        // Save the updated cart
        return cartRepository.save(cart);
    }


    @Transactional
    public Cart removeCartItem(Long userId, Long cartItemId, Integer amount) {
        Cart cart = cartRepository.findByUserId(userId);

        if (cart != null) {
            CartItem item = cart.getShoes().stream()
                    .filter(cartItem -> cartItem.getId().equals(cartItemId))
                    .findFirst()
                    .orElse(null);

            if (item != null) {
                int newAmount = item.getAmount() - amount;
                if (newAmount > 0) {
                    item.setAmount(newAmount); // Decrease the amount
                } else {
                    cart.getShoes().remove(item); // Remove the item if the amount is 0 or less
                }
                return cartRepository.save(cart);
            }
        }

        return cartRepository.save(cart);
    }


//    @Transactional
//    public Cart updateAmount(Long userId, Long cartItemId, Integer amount) {
//        Cart cart = cartRepository.findByUserId(userId);
//        cart.getShoes().stream().filter(i -> i.getId().compareTo(cartItemId) == 0)
//                .findFirst().ifPresent(cartItem -> cartItem.setAmount(amount));
//        return cart;
//    }

    @Transactional
    public Cart updateAmount(Long userId, Long cartItemId, Integer amount) {
        Cart cart = cartRepository.findByUserId(userId);

        System.out.println("Cart items in the cart: " + cart.getShoes().stream()
                .map(CartItem::getId)
                .collect(Collectors.toList()));

        boolean updated = cart.getShoes().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .map(cartItem -> {
//                    int newAmount = cartItem.getAmount();
                    if (amount > 0) {
                        cartItem.setAmount(amount);
                    } else {
                        cart.getShoes().remove(cartItem);
                    }
                    return true;
                })
                .orElse(false);

        if (!updated) {
            System.out.println(("CartItem with id " + cartItemId + " not found in cart for user " + userId));
        }

        return cartRepository.save(cart);

    }

//    @Transactional
//    public Cart updateCartItemQuantity(Long userId, Long cartItemId, boolean increase) {
//        Cart cart = cartRepository.findByUserId(userId);
//        if (cart != null) {
//
//            for (CartItem cartItem : cart.getShoes()) {
//                if (cartItem.getId().equals(cartItemId)) {
//                    int currentAmount = cartItem.getAmount();
//                    if (increase) {
//                        cartItem.setAmount(currentAmount + 1);
//                    } else {
//                        if (currentAmount > 1) {
//                            cartItem.setAmount(currentAmount - 1);
//                        } else {
//                            // Optionally remove the item if the amount is 1 and decrease is requested
//                            cart.getShoes().remove(cartItem);
//                        }
//                    }
//                    break;
//                }
//            }
//
//        }
//        assert cart != null;
//        return cartRepository.save(cart);
//    }

    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long cartItemId, boolean increase) {
        // Retrieve the cart for the user
        Cart cart = cartRepository.findByUserId(userId);

        // Initialize the cart if it doesn't exist
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setShoes(new ArrayList<>()); // Initialize the list of shoes
            cart = cartRepository.save(cart); // Save the new cart to the database
        }

        boolean itemFound = false;

        // Check if the CartItem is already in the cart
        for (CartItem cartItem : cart.getShoes()) {
            if (cartItem.getId().equals(cartItemId)) {
                itemFound = true;
                int currentAmount = cartItem.getAmount();
                if (increase) {
                    cartItem.setAmount(currentAmount + 1);
                } else {
                    if (currentAmount > 1) {
                        cartItem.setAmount(currentAmount - 1);
                    } else {
                        // Remove the item if the amount is 1 and decrease is requested
                        cart.getShoes().remove(cartItem);
                    }
                }
                break; // Exit loop once the item is found
            }
        }

        // If the item was not found in the cart, add it
        if (!itemFound) {
            CartItem newCartItem = new CartItem();
            newCartItem.setShoeId(cartItemId); // Assuming shoeId and cartItemId are the same for simplicity
            newCartItem.setAmount(increase ? 1 : 0); // Set amount based on whether it’s being increased or not
            newCartItem.setCart(cart); // Link the CartItem to the Cart
            cart.getShoes().add(newCartItem); // Add the new CartItem to the cart
        }

        return cartRepository.save(cart); // Save the updated cart
    }


//    @Transactional
//    public Cart updateAmount(Long userId, Long cartItemId, Integer amount) {
//        Cart cart = cartRepository.findByUserId(userId);
//
//        if (cart == null) {
//            System.out.println("Cart not found for user with id " + userId);
//            return null;
//        }
//
//        boolean updated = cart.getShoes().stream()
//                .filter(i -> i.getId().equals(cartItemId))
//                .findFirst()
//                .map(cartItem -> {
//                    int newAmount = cartItem.getAmount() - 1;
//                    if (newAmount > 0) {
//                        cartItem.setAmount(newAmount);
//                    } else {
//                        cart.getShoes().remove(cartItem);
//                    }
//                    return true;
//                })
//                .orElse(false);
//
//        if (!updated) {
//            System.out.println("CartItem with id " + cartItemId + " not found in cart for user " + userId);
//        }
//
//        return cart;
//    }



    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
