package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("book")
public class Book {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String isbn;
    
    private String title;
    
    private String author;
    
    private String publisher;
    
    @TableField(value = "publish_date")
    private LocalDate publishDate;
    
    @TableField(value = "category_id")
    private Long categoryId;
    
    private BigDecimal price;
    
    @TableField(value = "total_quantity")
    private Integer totalQuantity;
    
    @TableField(value = "available_quantity")
    private Integer availableQuantity;
    
    private String location;
    
    private String description;
    
    @TableField(value = "cover_url")
    private String coverUrl;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
