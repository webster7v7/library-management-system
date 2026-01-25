package com.library.dto;

import lombok.Data;
import java.util.List;

@Data
public class DashboardResponse {
    private DashboardStats stats;
    private List<BorrowRecordDetail> recentRecords;

    @Data
    public static class DashboardStats {
        private Integer totalBooks;
        private Integer availableBooks;
        private Integer borrowedBooks;
        private Integer totalUsers;
    }

    @Data
    public static class BorrowRecordDetail {
        private Long id;
        private String bookTitle;
        private String username;
        private String borrowDate;
        private String status;
    }
}
