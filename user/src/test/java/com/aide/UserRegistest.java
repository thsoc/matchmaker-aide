package com.aide;

import com.aide.entity.DO.UserDo;
import com.aide.entity.VO.LoginResponse;
import com.aide.entity.VO.RegisterRequest;
import com.aide.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author mazg
 * @description 用户注册测试类（符合DDD分层架构）
 * @date 2026/5/19
 * @date 16:35
 */
// 启动Spring Boot应用，并使用随机端口启动，使用项目的配置
@SpringBootTest(classes = UserClientApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRegistest {
    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    // ==================== 应用服务层测试 ====================

    /**
     * 测试用户注册 - 正常注册场景（最小参数）
     * 验证领域对象的 initializeNewUser() 方法是否正确设置默认值
     */
    @Test
    public void testRegisterWithMinimalInfo() {
        RegisterRequest request = new RegisterRequest();
        request.setAccount("testuser001");
        request.setPassword("password123");
        request.setUsername("测试用户");

//        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
//        mockRequest.setRemoteAddr("192.168.1.100");

        LoginResponse response = userService.register(request, "192.168.1.100");

        assertNotNull(response, "注册响应不应为空");
        assertNotNull(response.getUserId(), "用户ID不应为空");
        assertEquals("testuser001", response.getAccount(), "账号应匹配");
        assertEquals("测试用户", response.getUsername(), "用户名应匹配");
        assertNotNull(response.getToken(), "Token不应为空");
        assertFalse(response.getToken().isEmpty(), "Token不应为空字符串");
        assertEquals("USER", response.getRole(), "新用户角色应为USER");
        assertEquals("NORMAL", response.getStatus(), "新用户状态应为NORMAL");

        System.out.println("✓ 用户注册成功（最小参数）");
        System.out.println("  用户ID: " + response.getUserId());
        System.out.println("  账号: " + response.getAccount());
        System.out.println("  用户名: " + response.getUsername());
        System.out.println("  角色: " + response.getRole());
        System.out.println("  状态: " + response.getStatus());
    }

    /**
     * 测试用户注册 - 完整信息注册
     * 验证所有字段都能正确保存
     */
    @Test
    public void testRegisterWithFullInfo() {
        RegisterRequest request = new RegisterRequest();
        request.setAccount("testuser002");
        request.setPassword("password123");
        request.setUsername("完整信息用户");
        request.setMobile("13800138000");
        request.setEmail("test@example.com");
        request.setSex("MALE");
        request.setBirthday("1990-01-01");
        request.setOccupation("工程师");

//        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
//        mockRequest.setRemoteAddr("10.0.0.1");
//        mockRequest.addHeader("X-Forwarded-For", "192.168.1.200");

        LoginResponse response = userService.register(request, "192.168.1.100");

        assertNotNull(response, "注册响应不应为空");
        assertNotNull(response.getUserId(), "用户ID不应为空");
        assertEquals("testuser002", response.getAccount(), "账号应匹配");
        assertEquals("完整信息用户", response.getUsername(), "用户名应匹配");
        assertNotNull(response.getToken(), "Token不应为空");
        assertEquals("USER", response.getRole(), "新用户角色应为USER");
        assertEquals("NORMAL", response.getStatus(), "新用户状态应为NORMAL");

        System.out.println("✓ 用户注册成功（完整信息）");
        System.out.println("  用户ID: " + response.getUserId());
        System.out.println("  账号: " + response.getAccount());
        System.out.println("  Token: " + response.getToken());
    }

    /**
     * 测试用户注册 - 验证领域对象的默认值初始化
     * 符合DDD：领域对象负责自己的默认值设置
     */
    @Test
    public void testDomainObjectDefaultValues() {
        UserDo userDo = new UserDo();
        userDo.initializeNewUser();

        assertEquals("NORMAL", userDo.getStatus(), "默认状态应为NORMAL");
        assertEquals("USER", userDo.getRole(), "默认角色应为USER");
        assertEquals(0, userDo.getIntegral(), "默认积分应为0");
        assertEquals(BigDecimal.ZERO, userDo.getMoney(), "默认余额应为0");
        assertEquals(0, userDo.getLoginCount(), "默认登录次数应为0");
        assertEquals(0, userDo.getDelFlag(), "默认删除标志应为0");
        assertNotNull(userDo.getCreateTime(), "创建时间不应为空");
        assertNotNull(userDo.getUpdateTime(), "更新时间不应为空");

        System.out.println("✓ 领域对象默认值初始化正确");
    }

    /**
     * 测试用户注册 - 重复账号注册（应该失败）
     * 验证领域服务的唯一性检查
     */
    @Test
    public void testRegisterWithDuplicateAccount() {
        RegisterRequest request = new RegisterRequest();
        request.setAccount("admin");
        request.setPassword("password123");
        request.setUsername("重复账号测试");

//        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
//        mockRequest.setRemoteAddr("127.0.0.1");

        Exception exception = assertThrows(Exception.class, () -> {
            userService.register(request, "192.168.1.100");
        });

        assertNotNull(exception.getMessage(), "异常消息不应为空");
        assertTrue(
                exception.getMessage().contains("账号已存在") ||
                        exception.getMessage().contains("注册失败"),
                "异常消息应包含账号已存在或注册失败信息"
        );

        System.out.println("✓ 正确捕获重复账号异常: " + exception.getMessage());
    }

    /**
     * 测试用户注册 - IP地址为空时的处理
     * 验证领域对象的 record() 方法能正确处理 null IP
     */
    @Test
    public void testRegisterWithNullIp() {
        RegisterRequest request = new RegisterRequest();
        request.setAccount("testuser003");
        request.setPassword("password123");
        request.setUsername("空IP测试用户");

//        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
//        mockRequest.setRemoteAddr("0:0:0:0:0:0:0:1");

        LoginResponse response = userService.register(request, null);

        assertNotNull(response, "注册响应不应为空");
        assertNotNull(response.getToken(), "Token不应为空");

        System.out.println("✓ 空IP地址处理正确");
    }

    // ==================== 控制器层测试（集成测试）====================

    /**
     * 测试用户注册接口 - 通过Controller层（模拟HTTP请求）
     * 验证完整的HTTP请求处理流程
     */
    @Test
    public void testRegisterViaController() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        String registerJson = "{" +
                "\"account\":\"apitest001\"," +
                "\"password\":\"password123\"," +
                "\"username\":\"API测试用户\"," +
                "\"mobile\":\"13900139000\"," +
                "\"email\":\"api@test.com\"" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("注册成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.account").value("apitest001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("API测试用户"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value("USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("NORMAL"));

        System.out.println("✓ Controller层注册接口测试通过");
    }

    /**
     * 测试用户注册接口 - 账号格式验证
     * 验证 @Valid 注解的参数校验
     */
    @Test
    public void testRegisterWithInvalidAccountFormat() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        String registerJson = "{" +
                "\"account\":\"ab\"," +
                "\"password\":\"password123\"," +
                "\"username\":\"格式测试用户\"" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        System.out.println("✓ 账号格式验证正确");
    }

    /**
     * 测试用户注册接口 - 密码格式验证
     * 验证密码长度和格式要求
     */
    @Test
    public void testRegisterWithInvalidPasswordFormat() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        String registerJson = "{" +
                "\"account\":\"validuser\"," +
                "\"password\":\"123\"," +
                "\"username\":\"密码格式测试\"" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        System.out.println("✓ 密码格式验证正确");
    }

    /**
     * 测试用户注册接口 - 必填字段验证
     * 验证账号、密码、用户名的必填校验
     */
    @Test
    public void testRegisterWithMissingRequiredFields() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        String registerJson = "{" +
                "\"username\":\"缺少必填字段\"" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        System.out.println("✓ 必填字段验证正确");
    }

    /**
     * 测试用户注册接口 - 手机号格式验证
     * 验证手机号格式校验规则
     */
    @Test
    public void testRegisterWithInvalidMobile() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        String registerJson = "{" +
                "\"account\":\"validuser001\"," +
                "\"password\":\"password123\"," +
                "\"username\":\"手机号测试\"," +
                "\"mobile\":\"12345678901\"" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        System.out.println("✓ 手机号格式验证正确");
    }

    /**
     * 测试用户注册接口 - 邮箱格式验证
     * 验证邮箱格式校验规则
     */
    @Test
    public void testRegisterWithInvalidEmail() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        String registerJson = "{" +
                "\"account\":\"validuser002\"," +
                "\"password\":\"password123\"," +
                "\"username\":\"邮箱测试\"," +
                "\"email\":\"invalid-email\"" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        System.out.println("✓ 邮箱格式验证正确");
    }

    // ==================== 领域对象层测试（纯单元测试）====================

    /**
     * 测试领域对象 - 记录登录信息
     * 验证 record() 方法的业务逻辑
     */
    @Test
    public void testDomainRecordLogin() {
        UserDo userDo = UserDo.builder()
                .account("testuser")
                .loginCount(0)
                .build();

        String loginIp = "192.168.1.100";
        LocalDateTime beforeRecord = LocalDateTime.now();

        userDo.record(loginIp);

        assertNotNull(userDo.getLastLoginTime(), "最后登录时间不应为空");
        assertEquals(loginIp, userDo.getLastLoginIp(), "登录IP应匹配");
        assertEquals(1, userDo.getLoginCount(), "登录次数应为1");
        assertEquals("testuser", userDo.getCreateBy(), "创建人应为账号");
        assertEquals("testuser", userDo.getUpdateBy(), "更新人应为账号");
        assertTrue(userDo.getUpdateTime().isAfter(beforeRecord.minusSeconds(1)), "更新时间应合理");

        System.out.println("✓ 领域对象记录登录信息正确");
    }

    /**
     * 测试领域对象 - 记录登录信息时IP为空的处理
     * 验证领域规则：IP为空时设置为"unknown"
     */
    @Test
    public void testDomainRecordLoginWithNullIp() {
        UserDo userDo = UserDo.builder()
                .account("testuser")
                .loginCount(0)
                .build();

        userDo.record(null);

        assertEquals("unknown", userDo.getLastLoginIp(), "IP为空时应设置为unknown");
        assertEquals(1, userDo.getLoginCount(), "登录次数应为1");

        System.out.println("✓ 领域对象正确处理空IP");
    }

    /**
     * 测试领域对象 - 激活用户
     */
    @Test
    public void testDomainActivateUser() {
        UserDo userDo = UserDo.builder()
                .status("DISABLED")
                .build();

        userDo.activate();

        assertEquals("NORMAL", userDo.getStatus(), "状态应变为NORMAL");
        assertTrue(userDo.isActive(), "用户应该是活跃状态");

        System.out.println("✓ 领域对象激活用户正确");
    }

    /**
     * 测试领域对象 - 禁用用户
     */
    @Test
    public void testDomainDeactivateUser() {
        UserDo userDo = UserDo.builder()
                .status("NORMAL")
                .build();

        userDo.deactivate();

        assertEquals("DISABLED", userDo.getStatus(), "状态应变为DISABLED");
        assertFalse(userDo.isActive(), "用户应该不是活跃状态");

        System.out.println("✓ 领域对象禁用用户正确");
    }

    /**
     * 测试领域对象 - 封禁用户
     */
    @Test
    public void testDomainBanUser() {
        UserDo userDo = UserDo.builder()
                .status("NORMAL")
                .build();

        userDo.ban();

        assertEquals("BANNED", userDo.getStatus(), "状态应变为BANNED");
        assertTrue(userDo.isBanned(), "用户应该是被封禁状态");

        System.out.println("✓ 领域对象封禁用户正确");
    }
}
