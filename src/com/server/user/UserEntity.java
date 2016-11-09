package com.server.user;

import javax.persistence.*;
import java.util.Date;


/**
 * Created by Administrator on 2016/6/22.
 */
@Entity
@Table(name = "c_user", schema = "card", catalog = "")
public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private String realname;
    private Integer pic;
    private String score;
    private Integer allnum;
    private Integer winnum;
    private Integer gatenum;
    private Integer allinnum;
    private String rank;
    private Integer level;
    private Date registerDate;
    private Integer type;//权限
    public int roomIndex;


    private String currentScore;
    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
        this.registerDate = new Date();
        this.level = 0;
        this.type = 1;
    }
    public UserEntity() {
    }


    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "realname")
    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    @Basic
    @Column(name = "pic")
    public Integer getPic() {
        return pic;
    }

    public void setPic(Integer pic) {
        this.pic = pic;
    }

    @Basic
    @Column(name = "score")
    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Basic
    @Column(name = "allnum")
    public Integer getAllnum() {
        return allnum;
    }

    public void setAllnum(Integer allnum) {
        this.allnum = allnum;
    }

    @Basic
    @Column(name = "winnum")
    public Integer getWinnum() {
        return winnum;
    }

    public void setWinnum(Integer winnum) {
        this.winnum = winnum;
    }

    @Basic
    @Column(name = "gatenum")
    public Integer getGatenum() {
        return gatenum;
    }

    public void setGatenum(Integer gatenum) {
        this.gatenum = gatenum;
    }

    @Basic
    @Column(name = "allinnum")
    public Integer getAllinnum() {
        return allinnum;
    }

    public void setAllinnum(Integer allinnum) {
        this.allinnum = allinnum;
    }

    @Basic
    @Column(name = "rank")
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Basic
    @Column(name = "level")
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Basic
    @Column(name = "registerDate")
    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    @Basic
    @Column(name = "type")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Transient
    public String getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(String currentScore) {
        this.currentScore = currentScore;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (realname != null ? !realname.equals(that.realname) : that.realname != null) return false;
        if (pic != null ? !pic.equals(that.pic) : that.pic != null) return false;
        if (score != null ? !score.equals(that.score) : that.score != null) return false;
        if (allnum != null ? !allnum.equals(that.allnum) : that.allnum != null) return false;
        if (winnum != null ? !winnum.equals(that.winnum) : that.winnum != null) return false;
        if (gatenum != null ? !gatenum.equals(that.gatenum) : that.gatenum != null) return false;
        if (allinnum != null ? !allinnum.equals(that.allinnum) : that.allinnum != null) return false;
        if (rank != null ? !rank.equals(that.rank) : that.rank != null) return false;
        if (level != null ? !level.equals(that.level) : that.level != null) return false;
        if (registerDate != null ? !registerDate.equals(that.registerDate) : that.registerDate != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (realname != null ? realname.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (allnum != null ? allnum.hashCode() : 0);
        result = 31 * result + (winnum != null ? winnum.hashCode() : 0);
        result = 31 * result + (gatenum != null ? gatenum.hashCode() : 0);
        result = 31 * result + (allinnum != null ? allinnum.hashCode() : 0);
        result = 31 * result + (rank != null ? rank.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (registerDate != null ? registerDate.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (pic != null ? pic.hashCode() : 0);
        return result;
    }
}