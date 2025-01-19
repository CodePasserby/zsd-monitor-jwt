package com.example.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 节点重命名表单实体
 * 用于接收用户重命名节点的请求
 */
@Data
public class RenameNodeVO {
    // 节点ID
    int id;

    // 节点名称，长度要求在1到10个字符之间
    @Length(min = 1, max = 10)
    String node;

    // 节点位置，必须是以下国家/地区中的一个：cn、hk、jp、us、sg、kr、de
    @Pattern(regexp = "(cn|hk|jp|us|sg|kr|de)")
    String location;
}
