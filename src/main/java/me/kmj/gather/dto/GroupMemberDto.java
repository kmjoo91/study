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
public class GroupMemberDto {
    
    private Long id;
    private Long groupId;
    private String groupName;
    private String memberId;
    private String memberName;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
} 