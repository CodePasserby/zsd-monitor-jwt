package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService; // 引入MyBatis-Plus的IService接口，提供通用服务功能
import com.example.entity.dto.Client; // 引入Client实体类
import com.example.entity.vo.request.*; // 引入请求VO类
import com.example.entity.vo.response.*; // 引入响应VO类

import java.util.List; // 引入List集合类

/**
 * ClientService接口
 * 该接口定义了与客户端（Client）相关的服务操作，包括注册、更新、查询、删除客户端等功能。
 * 继承了MyBatis-Plus的IService接口，提供了基础的CRUD服务。
 */
public interface ClientService extends IService<Client> {

    /**
     * 生成注册令牌
     * @return 返回生成的注册令牌
     */
    String registerToken();

    /**
     * 根据客户端ID查找客户端信息
     * @param id 客户端ID
     * @return 匹配的客户端信息
     */
    Client findClientById(int id);

    /**
     * 根据令牌查找客户端信息
     * @param token 客户端令牌
     * @return 匹配的客户端信息
     */
    Client findClientByToken(String token);

    /**
     * 验证令牌并完成注册
     * @param token 客户端令牌
     * @return 注册结果（成功或失败）
     */
    boolean verifyAndRegister(String token);

    /**
     * 更新客户端详细信息
     * @param vo 包含客户端详细信息的VO对象
     * @param client 要更新的客户端对象
     */
    void updateClientDetail(ClientDetailVO vo, Client client);

    /**
     * 更新客户端运行时详细信息
     * @param vo 包含运行时详细信息的VO对象
     * @param client 要更新的客户端对象
     */
    void updateRuntimeDetail(RuntimeDetailVO vo, Client client);

    /**
     * 获取所有客户端的简略信息
     * @return 客户端预览信息列表
     */
    List<ClientPreviewVO> listClients();

    /**
     * 获取所有客户端的简单信息
     * @return 客户端简单信息列表
     */
    List<ClientSimpleVO> listSimpleList();

    /**
     * 修改客户端名称
     * @param vo 包含修改客户端名称信息的VO对象
     */
    void renameClient(RenameClientVO vo);

    /**
     * 修改节点名称
     * @param vo 包含修改节点名称信息的VO对象
     */
    void renameNode(RenameNodeVO vo);

    /**
     * 获取客户端的详细信息
     * @param clientId 客户端ID
     * @return 客户端的详细信息
     */
    ClientDetailsVO clientDetails(int clientId);

    /**
     * 获取客户端的运行时历史记录
     * @param clientId 客户端ID
     * @return 客户端的运行时历史记录
     */
    RuntimeHistoryVO clientRuntimeDetailsHistory(int clientId);

    /**
     * 获取客户端当前的运行时详细信息
     * @param clientId 客户端ID
     * @return 客户端当前的运行时详细信息
     */
    RuntimeDetailVO clientRuntimeDetailsNow(int clientId);

    /**
     * 删除客户端
     * @param clientId 要删除的客户端ID
     */
    void deleteClient(int clientId);

    /**
     * 保存客户端SSH连接信息
     * @param vo 包含SSH连接信息的VO对象
     */
    void saveClientSshConnection(SshConnectionVO vo);

    /**
     * 获取客户端的SSH设置
     * @param clientId 客户端ID
     * @return 客户端的SSH设置
     */
    SshSettingsVO sshSettings(int clientId);
}
