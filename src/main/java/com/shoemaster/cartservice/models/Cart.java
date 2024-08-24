package com.shoemaster.cartservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> shoes;

//    public void addCartItem(Long shoeId) {
//        shoes.add(CartItem.builder().shoeId(shoeId).amount(1).build());
//    }

    public void addCartItem(Long shoeId) {
        for (CartItem item : shoes) {
            if (item.getShoeId().equals(shoeId)) {
                item.setAmount(item.getAmount() + 1);
                return;
            }
        }
        shoes.add(CartItem.builder().shoeId(shoeId).amount(1).cart(this).build());
    }


//
//    public void removeCartItem(Long shoeId) {
//        shoes.removeIf(ci -> ci.getShoeId().equals(shoeId));
//    }

    public void removeCartItem(Long shoeId) {
        shoes.removeIf(ci -> {
            if (ci.getShoeId().equals(shoeId)) {
                if (ci.getAmount() > 1) {
                    ci.setAmount(ci.getAmount() - 1);
                    return false;
                }
                return true; // Remove if amount is 1
            }
            return false;
        });
    }


}
