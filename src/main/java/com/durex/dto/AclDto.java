package com.durex.dto;

import com.durex.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@ToString
public class AclDto extends SysAcl {

    // 是否默认选中
    private boolean checked = false;
    // 是否有权限操作
    private boolean hasAcl = false;

    public static AclDto adapt(SysAcl sysAcl) {
        AclDto dto = new AclDto();
        BeanUtils.copyProperties(sysAcl, dto);
        return dto;
    }
}

