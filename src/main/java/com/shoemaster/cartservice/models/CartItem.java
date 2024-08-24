package com.shoemaster.cartservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Tolerate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long shoeId;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    @ToString.Exclude
    private Cart cart;


    @Tolerate
    public CartItem(Long shoeId, Integer amount, Cart cart) {
        this.shoeId = shoeId;
        this.amount = amount;
        this.cart = cart;
    }

//    @ManyToOne
//    @JoinColumn(name = "shoe_id", nullable = false)
//    private Shoe shoe;


//    @ManyToOne
//    @JoinColumn(name = "cart_id")
//    private Cart cart;

}
