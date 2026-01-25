package com.library.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BorrowRecordDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status;
    private Integer renewCount;
}
