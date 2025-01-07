package com.nnk.springboot.domain;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private int id;

    @Column(name = "moodys_Rating", nullable = false, length = 125)
    private String moodysRating;

    @Column(name = "sandprating",  nullable = false, length = 125)
    private String sandPRating;

    @Column(name = "fitch_Rating", nullable = false, length = 125)
    private String fitchRating;

    @Column(name = "order_Number",  nullable = false)
    private Integer orderNumber;

}
