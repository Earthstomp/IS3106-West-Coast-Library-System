/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Member;
import exception.InputDataValidationException;
import exception.MemberIdExistException;
import exception.MemberNotFoundException;
import exception.UnknownPersistenceException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author keith
 */
@Local
public interface MemberSessionBeanLocal {

    public Long createNewMember(Member newMember) throws MemberIdExistException, UnknownPersistenceException, InputDataValidationException;

    public Member retrieveMemberByIdentityNumber(String identityNo) throws MemberNotFoundException;

    public List<Member> searchMemberByFirstName(String name);

    public List<Member> searchMemberbyIdentityNumber(String identityNo);

    public List<Member> searchMemberbyPhone(String phone);

    public List<Member> searchMemberByLastName(String name);
    
}
