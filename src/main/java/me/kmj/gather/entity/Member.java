package me.kmj.gather.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    
    @Id
    @Column(length = 10)
    private String id;
    
    @Column(length = 100, nullable = false)
    private String name;
    
    @Column(length = 10)
    private String gender;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(length = 255)
    private String address;
} 