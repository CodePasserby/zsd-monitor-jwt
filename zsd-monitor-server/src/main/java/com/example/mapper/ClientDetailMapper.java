package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper; // 引入MyBatis-Plus的基础Mapper接口
import com.example.entity.dto.ClientDetail; // 引入ClientDetail实体类
import org.apache.ibatis.annotations.Mapper; // 引入@Mapper注解，用于标记Mapper接口

/**
 * ClientDetailMapper接口
 * 该接口用于操作ClientDetail表的数据。
 * 继承了MyBatis-Plus提供的BaseMapper接口，自动拥有基础的CRUD操作（增、删、改、查）。
 */
@Mapper // 标记为MyBatis的Mapper接口，使其在运行时被MyBatis框架识别并注册
public interface ClientDetailMapper extends BaseMapper<ClientDetail> {
    // 这里没有额外方法声明，因为BaseMapper已涵盖了常见数据库操作方法。
    // 如果需要复杂查询或特殊操作，可以在此添加自定义方法。
}
