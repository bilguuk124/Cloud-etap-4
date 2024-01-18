package com.itmo.library;

import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEntity {
    private List<UserInfo> receivers;
    private String message;
}
