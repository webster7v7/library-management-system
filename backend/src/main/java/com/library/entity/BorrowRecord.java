package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("borrow_record")
public class BorrowRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField(value = "user_id")
    private Long userId;
    
    @TableField(value = "book_id")
    private Long bookId;
    
    @TableField(value = "borrow_date")
    private LocalDateTime borrowDate;
    
    @TableField(value = "due_date")
    private LocalDateTime dueDate;
    
    @TableField(value = "return_date")
    private LocalDateTime returnDate;
    
    private String status;
    
    @TableField(value = "renew_count")
    private Integer renewCount;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
