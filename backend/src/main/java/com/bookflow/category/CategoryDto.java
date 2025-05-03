package com.bookflow.category;

import lombok.*;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long categoryId;
    private String categoryName;
}
