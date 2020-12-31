package com.king.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.king.framework.web.domain.BaseEntity;

/**
 * @author 金振林
 * @version v1.0
 * @date 2020/12/25 11:51
 */
@TableName("sys_role")
public class Role extends BaseEntity {

    /**
     * 主键策略必须设置，否则无法调用通用Service的save等方法
     */
    @TableId(type = IdType.AUTO)
    private Integer roleId;

    private String roleName;

    private String roleKey;

    private Integer roleSort;

    private String dataScope;

    private String status;

    /**
     * 定义逻辑删除
     */
    @TableLogic(value = "0", delval = "1")
    private String delFlag;

    /**
     * 指明 是否为数据表的字段
     */
    @TableField(exist = false)
    private String userName;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public Integer getRoleSort() {
        return roleSort;
    }

    public void setRoleSort(Integer roleSort) {
        this.roleSort = roleSort;
    }

    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
