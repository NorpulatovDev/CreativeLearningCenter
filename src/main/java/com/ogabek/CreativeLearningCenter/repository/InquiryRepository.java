package com.ogabek.CreativeLearningCenter.repository;

import com.ogabek.CreativeLearningCenter.entity.Inquiry;
import com.ogabek.CreativeLearningCenter.entity.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    
    List<Inquiry> findByStatus(InquiryStatus status);
    
    List<Inquiry> findByParentPhoneNumber(String parentPhoneNumber);
}