package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.CreateSubAccountVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.entity.vo.request.ModifyEmailVO;
import com.example.entity.vo.response.SubAccountVO;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 账户信息处理相关服务
 * 实现了AccountService接口，提供账户管理相关操作的具体实现，包括注册、密码重置、子账户管理等功能。
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    // 验证邮件发送冷却时间限制，秒为单位
    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    @Resource
    AmqpTemplate rabbitTemplate; // RabbitMQ模板，用于发送邮件请求

    @Resource
    StringRedisTemplate stringRedisTemplate; // Redis操作模板，用于存储验证码

    @Resource
    PasswordEncoder passwordEncoder; // 密码加密器，用于加密密码

    @Resource
    FlowUtils flow; // 限流工具，用于防止频繁请求

    /**
     * 从数据库中通过用户名或邮箱查找用户详细信息
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 如果用户未找到则抛出此异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if(account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        // 返回用户的详细信息，包括用户名、密码和角色
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    /**
     * 生成注册验证码并存入Redis中，将邮件发送请求提交到消息队列等待发送
     * @param type 类型
     * @param email 邮件地址
     * @param address 请求IP地址
     * @return 操作结果，null表示正常，否则为错误原因
     */
    public String registerEmailVerifyCode(String type, String email, String address){
        synchronized (address.intern()) { // 同步操作，防止同一IP地址频繁请求
            if(!this.verifyLimit(address)) // 判断IP地址是否请求频繁
                return "请求频繁，请稍后再试";
            Random random = new Random();
            int code = random.nextInt(899999) + 100000; // 生成六位随机验证码
            Map<String, Object> data = Map.of("type",type,"email", email, "code", code); // 封装验证码信息
            rabbitTemplate.convertAndSend(Const.MQ_MAIL, data); // 发送邮件请求到消息队列
            // 将验证码存入Redis，设置3分钟过期
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 邮件验证码重置密码操作，需要检查验证码是否正确
     * @param info 重置基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetEmailAccountPassword(EmailResetVO info) {
        String verify = resetConfirm(new ConfirmResetVO(info.getEmail(), info.getCode()));
        if(verify != null) return verify; // 验证码验证失败
        String email = info.getEmail();
        String password = passwordEncoder.encode(info.getPassword()); // 加密新密码
        boolean update = this.update().eq("email", email).set("password", password).update(); // 更新密码
        if(update) {
            this.deleteEmailVerifyCode(email); // 更新密码后删除验证码
        }
        return update ? null : "更新失败，请联系管理员"; // 返回更新结果
    }

    /**
     * 重置密码确认操作，验证验证码是否正确
     * @param info 验证基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetConfirm(ConfirmResetVO info) {
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email); // 从Redis获取验证码
        if(code == null) return "请先获取验证码"; // 验证码不存在
        if(!code.equals(info.getCode())) return "验证码错误，请重新输入"; // 验证码错误
        return null;
    }

    /**
     * 修改账户密码
     * @param id 账户ID
     * @param oldPass 旧密码
     * @param newPass 新密码
     * @return 修改是否成功
     */
    @Override
    public boolean changePassword(int id, String oldPass, String newPass) {
        Account account = this.getById(id); // 获取账户信息
        String password = account.getPassword();
        if(!passwordEncoder.matches(oldPass, password)) // 验证旧密码是否正确
            return false;
        // 更新密码
        this.update(Wrappers.<Account>update().eq("id", id)
                .set("password", passwordEncoder.encode(newPass)));
        return true;
    }

    /**
     * 创建子账户
     * @param vo 子账户信息
     */
    @Override
    public void createSubAccount(CreateSubAccountVO vo) {
        // 检查邮箱或用户名是否已被注册
        Account account = this.findAccountByNameOrEmail(vo.getEmail());
        if(account != null)
            throw new IllegalArgumentException("该电子邮件已被注册");
        account = this.findAccountByNameOrEmail(vo.getUsername());
        if(account != null)
            throw new IllegalArgumentException("该用户名已被注册");
        // 创建新的子账户并保存
        account = new Account(null, vo.getUsername(), passwordEncoder.encode(vo.getPassword()),
                vo.getEmail(), Const.ROLE_NORMAL, new Date(), JSONArray.copyOf(vo.getClients()).toJSONString());
        this.save(account);
    }

    /**
     * 删除子账户
     * @param uid 子账户ID
     */
    @Override
    public void deleteSubAccount(int uid) {
        this.removeById(uid); // 删除子账户
    }

    /**
     * 获取所有子账户列表
     * @return 子账户列表
     */
    @Override
    public List<SubAccountVO> listSubAccount() {
        return this.list(Wrappers.<Account>query().eq("role", Const.ROLE_NORMAL)) // 查找所有普通角色的账户
                .stream().map(account -> {
                    SubAccountVO vo = account.asViewObject(SubAccountVO.class); // 转换为SubAccountVO对象
                    vo.setClientList(JSONArray.parse(account.getClients())); // 设置客户端列表
                    return vo;
                }).toList();
    }

    /**
     * 修改账户邮箱
     * @param id 账户ID
     * @param vo 包含新邮箱和验证码的修改信息
     * @return 操作结果，null表示成功
     */
    @Override
    public String modifyEmail(int id, ModifyEmailVO vo) {
        String code = getEmailVerifyCode(vo.getEmail()); // 获取当前存储的验证码
        if (code == null) return "请先获取验证码"; // 验证码不存在
        if(!code.equals(vo.getCode())) return "验证码错误，请重新输入"; // 验证码错误
        this.deleteEmailVerifyCode(vo.getEmail()); // 删除验证码
        Account account = this.findAccountByNameOrEmail(vo.getEmail()); // 查找是否已存在该邮箱
        if(account != null && account.getId() != id) return "该邮箱账号已经被其他账号绑定，无法完成操作"; // 邮箱已被绑定到其他账号
        // 更新邮箱
        this.update()
                .set("email", vo.getEmail())
                .eq("id", id)
                .update();
        return null;
    }

    /**
     * 移除Redis中存储的邮件验证码
     * @param email 电邮
     */
    private void deleteEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key); // 删除Redis中的验证码
    }

    /**
     * 获取Redis中存储的邮件验证码
     * @param email 电邮
     * @return 验证码
     */
    private String getEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key); // 从Redis获取验证码
    }


/**
     * 针对IP地址进行邮件验证码获取限流
     * @param address 地址
     * @return 是否通过验证
     */
    private boolean verifyLimit(String address) {
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flow.limitOnceCheck(key, verifyLimit);
    }

    /**
     * 通过用户名或邮件地址查找用户
     * @param text 用户名或邮件
     * @return 账户实体
     */
    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }
}
