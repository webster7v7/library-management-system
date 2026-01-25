package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.BorrowRecordDTO;
import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    @Select("SELECT br.id, br.user_id, br.book_id, b.title as book_title, b.author as book_author, b.isbn as book_isbn, " +
            "br.borrow_date, br.due_date, br.return_date, br.status, br.renew_count " +
            "FROM borrow_record br " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "WHERE br.deleted = 0 " +
            "AND br.user_id = #{userId} " +
            "AND br.status = #{status} " +
            "ORDER BY br.borrow_date DESC")
    IPage<BorrowRecordDTO> selectBorrowRecordsWithBook(Page<BorrowRecordDTO> page, @Param("userId") Long userId, @Param("status") String status);

    @Select("SELECT br.id, br.user_id, br.book_id, b.title as book_title, b.author as book_author, b.isbn as book_isbn, " +
            "br.borrow_date, br.due_date, br.return_date, br.status, br.renew_count " +
            "FROM borrow_record br " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "WHERE br.deleted = 0 " +
            "AND br.user_id = #{userId} " +
            "ORDER BY br.borrow_date DESC")
    IPage<BorrowRecordDTO> selectUserBorrowHistory(Page<BorrowRecordDTO> page, @Param("userId") Long userId);
}
