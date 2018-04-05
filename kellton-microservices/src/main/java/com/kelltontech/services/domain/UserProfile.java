package com.kelltontech.services.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A UserProfile.
 */
@Entity
@Table(name = "user_profile")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email_id")
    private String userEmailId;

    @Column(name = "user_mobile_no")
    private Long userMobileNo;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public UserProfile userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmailId() {
        return userEmailId;
    }

    public UserProfile userEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
        return this;
    }

    public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }

    public Long getUserMobileNo() {
        return userMobileNo;
    }

    public UserProfile userMobileNo(Long userMobileNo) {
        this.userMobileNo = userMobileNo;
        return this;
    }

    public void setUserMobileNo(Long userMobileNo) {
        this.userMobileNo = userMobileNo;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserProfile userProfile = (UserProfile) o;
        if (userProfile.getId() == null || this.getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userProfile.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserProfile{" +
            "id=" + getId() +
            ", userName='" + getUserName() + "'" +
            ", userEmailId='" + getUserEmailId() + "'" +
            ", userMobileNo=" + getUserMobileNo() +
            "}";
    }
}
