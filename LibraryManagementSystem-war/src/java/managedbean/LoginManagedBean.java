package managedbean;

import entity.Staff;
import exception.InvalidLoginCredentialException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import session.StaffSessionBeanLocal;

@Named(value = "loginManagedBean")
@SessionScoped
public class LoginManagedBean implements Serializable {

    @EJB
    private StaffSessionBeanLocal staffSessionBeanLocal;

    private String username = null;
    private String password = null;
    private Long userId = new Long(0);

    public LoginManagedBean() {
    }
    
    public String visitLogin() {
        return "/login.xhtml?faces-redirect=true";
    }

    public String login() {

        try {
            Staff staff = staffSessionBeanLocal.staffLogin(username, password);
            userId = staff.getStaffId();
            return "/staff/main.xhtml?faces-redirect=true";
        } catch (InvalidLoginCredentialException ex) {
            username = null;
            password = null;
            userId = new Long(-1);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Login Credentials", ""));
            return "";
        }

    } //end login

    public String logout() {
        username = null;
        password = null;
        userId = new Long(-1);

        return "/login.xhtml?faces-redirect=true";
    } //end logout

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

