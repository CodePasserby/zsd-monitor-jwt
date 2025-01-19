package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper; // 引入MyBatis-Plus的基础Mapper接口
import com.example.entity.dto.ClientSsh; // 引入ClientSsh实体类
import org.apache.ibatis.annotations.Mapper; // 引入@Mapper注解，标记为Mapper接口

/**
 * ClientSshMapper接口
 * 该接口用于操作ClientSsh表的数据。
 * 继承了MyBatis-Plus提供的BaseMapper接口，因此自动具备基本的CRUD操作（增、删、改、查）。
 */
@Mapper // 标记为MyBatis的Mapper接口，使其在运行时被MyBatis识别并注册
public interface ClientSshMapper extends BaseMapper<ClientSsh> {
    // BaseMapper已提供了常见的增删改查方法，如selectById、insert、updateById等。
    // 如果有特定需求，开发者可以在此扩展自定义SQL方法来处理复杂的查询或操作。
}
