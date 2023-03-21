/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author keith
 */
@Entity
public class LendAndReturn implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lendId;

    @ManyToOne (fetch = FetchType.EAGER)
    private Member member;
    private Long memberId;
    @ManyToOne (fetch = FetchType.EAGER)
    private Book book;
    private Long bookId;
    private Date lendDate;
    private Date returnDate;
    private Double fineAmount;


    public LendAndReturn() {
        
    }
    
    public LendAndReturn(Member member, Long memberId, Book book, Long bookId, Date lendDate) {
        this.member = member;
        this.memberId = memberId;
        this.bookId = bookId;
        this.book = book;
        this.lendDate = lendDate;
        this.returnDate = null;
        this.fineAmount = 0.0;
    }
   
    public Long getId() {
        return getLendId();
    }

    public void setId(Long id) {
        this.lendId = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getLendId() != null ? getLendId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LendAndReturn)) {
            return false;
        }
        LendAndReturn other = (LendAndReturn) object;
        if ((this.getLendId() == null && other.getLendId() != null) || (this.getLendId() != null && !this.lendId.equals(other.lendId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.LendAndReturn[ id=" + getLendId() + " ]";
    }

    /**
     * @return the lendId
     */
    public Long getLendId() {
        return lendId;
    }

    /**
     * @return the memberId
     */
    public Long getMemberId() {
        return memberId;
    }
    
    public Member getMember() {
        return member;
    }

    /**
     * @param memberId the memberId to set
     */
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    /**
     * @return the bookId
     */
    public Long getBookId() {
        return bookId;
    }
    
    public Book getBook() {
        return book;
    }

    /**
     * @param bookId the bookId to set
     */
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    /**
     * @return the lendDate
     */
    public Date getLendDate() {
        return lendDate;
    }

    /**
     * @param lendDate the lendDate to set
     */
    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }

    /**
     * @return the returnDate
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * @param returnDate the returnDate to set
     */
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * @return the fineAmount
     */
    public Double getFineAmount() {
        return fineAmount;
    }

    /**
     * @param fineAmount the fineAmount to set
     */
    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

}
