package me.kmj.gather.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    
    private String id;
    
    private String name;
    
    private String gender;
    
    private String phoneNumber;
    
    private String address;
} 