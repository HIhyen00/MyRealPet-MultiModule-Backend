package com.myrealpet.sns.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDto {
    private Long id;
    private String username;
    private String name;
    private String phoneNumber;
    private String role;
    private Boolean isActive;
}
