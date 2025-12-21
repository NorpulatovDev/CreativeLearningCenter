package com.ogabek.CreativeLearningCenter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    
    private Long id;
    private String fullName;
    private String parentName;
    private String parentPhoneNumber;
    private String smsLinkCode;
    private BigDecimal totalPaid;
    
    // Multiple groups support with payment status
    private List<GroupInfo> activeGroups;
    private Integer activeGroupsCount;
    
    // Overall payment status for current month
    private Boolean paidForCurrentMonth;
    private Integer groupsPaidCount;
    private Integer groupsUnpaidCount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupInfo {
        private Long groupId;
        private String groupName;
        private String teacherName;
        private BigDecimal monthlyFee;
        
        // Payment status for this group
        private Boolean paidForCurrentMonth;
        private String currentMonth;
        private BigDecimal amountPaidThisMonth;
    }
}
