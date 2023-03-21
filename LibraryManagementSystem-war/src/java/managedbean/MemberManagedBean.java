/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Member;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import session.MemberSessionBeanLocal;

/**
 *
 * @author keith
 */
@Named(value = "memberManagedBean")
@ViewScoped
public class MemberManagedBean implements Serializable {

    @EJB
    private MemberSessionBeanLocal memberSessionBeanLocal;

    private String firstName;
    private String lastName;
    private String identityNumber;
    private String phone;
    private int gender;
    private int age;
    private String id;
    private String address;

    private List<Member> members;
    private Member selectedMember;
    private int rows = 5;

    private String searchType = "FIRST NAME";
    private String searchString;

    public MemberManagedBean() {
    }

    @PostConstruct
    public void init() {
        if (searchString == null || searchString.equals("")) {
            members = memberSessionBeanLocal.searchMemberByFirstName(null);
        } else {
            switch (searchType) {
                case "FIRST NAME": {
                    members = memberSessionBeanLocal.searchMemberByFirstName(searchString);
                    break;
                }

                case "ID": {
                    members = memberSessionBeanLocal.searchMemberbyIdentityNumber(searchString);
                    break;
                }

                case "LAST NAME": {
                    members = memberSessionBeanLocal.searchMemberByLastName(searchString);
                    break;
                }

                case "PHONE": {
                    members = memberSessionBeanLocal.searchMemberbyPhone(searchString);
                    break;
                }
                default:
                    break;
            }
        }
    }

    public void loadSelectedMember() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            this.selectedMember = memberSessionBeanLocal.retrieveMemberByIdentityNumber(id);
            firstName = this.selectedMember.getFirstName();
            lastName = this.selectedMember.getLastName();
            gender = this.selectedMember.getGender();

            age = this.selectedMember.getAge();
            id = this.selectedMember.getIdentityNo();
            phone = this.selectedMember.getPhone();
            address = this.selectedMember.getAddress();

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! Unable to load customer", ""));
        }
    } //end loadSelectedCustomer

    public void handleSearch() {
        init();
    } //end handleSearch

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    public List<Member> getMembers() {
        return members;
    }

    public String getSearchType() {
        return searchType;
    }

    public String getSearchString() {
        return searchString;
    }

    public Member getSelectedMember() {
        return selectedMember;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param selectedMember the selectedMember to set
     */
    public void setSelectedMember(Member selectedMember) {
        this.selectedMember = selectedMember;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public void setFirstName(String s) {
        this.firstName = s;
    }

    public void setLastName(String s) {
        this.lastName = s;
    }

    public void setIdentityNumber(String s) {
        this.identityNumber = s;
    }

    public void setPhone(String s) {
        this.phone = s;
    }

    public void setAge(Integer i) {
        this.age = i;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void addMember(ActionEvent evt) {

        Character genderChar;
        if (gender == 1) {
            genderChar = 'M';
        } else {
            genderChar = 'F';
        }
        Member newMember = new Member(firstName, lastName, genderChar, age, identityNumber, phone, address);

        try {
            memberSessionBeanLocal.createNewMember(newMember);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, firstName + "  " + lastName + " has been registered!", ""));

            firstName = null;
            lastName = null;
            identityNumber = null;
            phone = null;
            gender = 1; // default male
            age = 0;
            address = null;

        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! Member with same ID already exists in System", ""));
        }

    }

    /**
     * @return the rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * @return the selectedMember
     */
    /**
     * Creates a new instance of MemberManagedBean
     */
}
