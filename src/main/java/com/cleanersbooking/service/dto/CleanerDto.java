package com.cleanersbooking.service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class CleanerDto {
    private Long cleanerId;
    private String cleanerName;
}
