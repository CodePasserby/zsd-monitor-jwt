package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService; // 引入MyBatis-Plus的IService接口，提供通用服务功能
import com.example.entity.dto.Account; // 引入Account实体类
import com.example.entity.vo.request.ConfirmResetVO; // 引入请求确认重置密码的VO类
import com.example.entity.vo.request.CreateSubAccountVO; // 引入创建子账户请求的VO类
import com.example.entity.vo.request.EmailResetVO; // 引入通过邮箱重置密码的请求VO类
import com.example.entity.vo.request.ModifyEmailVO; // 引入修改邮箱的请求VO类
import com.example.entity.vo.response.SubAccountVO; // 引入子账户信息响应VO类
import org.springframework.security.core.userdetails.UserDetailsService; // 引入Spring Security的UserDetailsService接口，用于处理用户详情

import java.util.List; // 引入List集合类

/**
 * AccountService接口
 * 该接口定义了与账户相关的服务操作，包括账户注册、密码重置、子账户管理等功能。
 * 它继承了IService<Account>接口，提供了MyBatis-Plus的通用服务功能，同时继承了UserDetailsService接口，用于用户认证。
 */
public interface AccountService extends IService<Account>, UserDetailsService {

    /**
     * 根据用户名或邮箱查找账户
     * @param text 用户名或邮箱
     * @return 匹配的账户信息
     */
    Account findAccountByNameOrEmail(String text);

    /**
     * 发送邮箱验证码用于注册验证
     * @param type 验证类型（如注册、重置密码等）
     * @param email 用户的邮箱
     * @param address 邮件地址
     * @return 发送结果信息
     */
    String registerEmailVerifyCode(String type, String email, String address);

    /**
     * 通过邮箱重置账户密码
     * @param info 包含重置密码信息的VO对象
     * @return 结果信息
     */
    String resetEmailAccountPassword(EmailResetVO info);

    /**
     * 确认重置密码
     * @param info 包含确认重置密码信息的VO对象
     * @return 结果信息
     */
    String resetConfirm(ConfirmResetVO info);

    /**
     * 修改账户密码
     * @param id 用户ID
     * @param oldPass 旧密码
     * @param newPass 新密码
     * @return 修改结果，成功或失败
     */
    boolean changePassword(int id, String oldPass, String newPass);

    /**
     * 创建子账户
     * @param vo 包含创建子账户信息的VO对象
     */
    void createSubAccount(CreateSubAccountVO vo);

    /**
     * 删除子账户
     * @param uid 子账户的用户ID
     */
    void deleteSubAccount(int uid);

    /**
     * 获取子账户列表
     * @return 子账户信息列表
     */
    List<SubAccountVO> listSubAccount();

    /**
     * 修改用户邮箱
     * @param id 用户ID
     * @param vo 包含修改邮箱信息的VO对象
     * @return 修改结果
     */
    String modifyEmail(int id, ModifyEmailVO vo);
}
