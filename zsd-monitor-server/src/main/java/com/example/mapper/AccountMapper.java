package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper; // 引入MyBatis-Plus提供的基础Mapper接口
import com.example.entity.dto.Account; // 引入Account实体类
import org.apache.ibatis.annotations.Mapper; // 引入@Mapper注解

/**
 * AccountMapper接口
 * 这个接口用于操作Account表的数据。
 * 继承了MyBatis-Plus提供的BaseMapper接口，直接具备了基本的CRUD操作。
 */
@Mapper // 告诉MyBatis这是一个Mapper接口，用于数据库映射
public interface AccountMapper extends BaseMapper<Account> {
    // 此处无需额外的方法声明，BaseMapper已提供通用的增删改查方法。
    // 如果需要定义自定义SQL方法，可以在这里扩展。
}
