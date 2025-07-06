package me.kmj.gather.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfoDto {
    
    private Long id;
    private String name;
    private String description;
    private String createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private int approvedMembersCount;
    private int pendingMembersCount;
} 