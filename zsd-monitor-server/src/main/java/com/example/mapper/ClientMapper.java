package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper; // 引入MyBatis-Plus的基础Mapper接口
import com.example.entity.dto.Client; // 引入Client实体类
import org.apache.ibatis.annotations.Mapper; // 引入@Mapper注解，标记为Mapper接口

/**
 * ClientMapper接口
 * 该接口用于操作Client表的数据。
 * 继承了MyBatis-Plus提供的BaseMapper接口，具备了基本的CRUD操作功能（如查询、插入、更新、删除）。
 */
@Mapper // 声明这是一个MyBatis的Mapper接口，运行时会被MyBatis扫描并注册到容器中
public interface ClientMapper extends BaseMapper<Client> {
    // BaseMapper提供了通用的增删改查方法，如selectById、insert、updateById等。
    // 如果需要定义自定义方法，可以在这里扩展，比如复杂查询或特定的业务逻辑。
}
