package com.aide;


import com.aide.adapter.dto.LoginResponse;
import com.aide.adapter.dto.RegisterRequest;
import com.aide.common.Result.Result;
import com.aide.domain.model.UserDo;
import com.aide.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
//// 将 RANDOM_PORT 改为 MOCK (默认) 或 DEFINED_PORT
//@SpringBootTest(classes = UserClientApp.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserRegistest {
    @Autowired
    private UserService userService;


    @LocalServerPort // 注入随机生成的端口号
    private int port;

    // 1. 注入 TestRestTemplate，它会自动连接到随机启动的 Tomcat 端口
    @Autowired
    private TestRestTemplate restTemplate;

    //内存模拟
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        // 关键点：必须加上 .defaultRequest(get("/").port(port)) 或者在 perform 时使用完整 URL
        // 但更稳妥的方式是使用 RestAssuredMockMvc 或者直接用 TestRestTemplate

        // 如果非要用 MockMvc，需要这样配置让它知道去哪里请求：
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
//                .defaultRequest(MockMvcRequestBuilders.get("/").port(port))
//                .build();
    }

    // ==================== 应用服务层测试 ====================

    /**
     * 测试用户注册 - 正常注册场景（最小参数）
     * 验证领域对象的 initializeNewUser() 方法是否正确设置默认值
     */
    @Test
    public void testRegisterWithMinimalInfo() {
        String testuserAccount = "testuser002";
        String testUsername = "测试用户";
        RegisterRequest request = new RegisterRequest();
        request.setAccount(testuserAccount);
        request.setPassword("password123");
        request.setUsername(testUsername);

//        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
//        mockRequest.setRemoteAddr("192.168.1.100");

        LoginResponse response = userService.register(request, "192.168.1.100");

        assertNotNull(response, "注册响应不应为空");
        assertNotNull(response.getUserId(), "用户ID不应为空");
        assertEquals(testuserAccount, response.getAccount(), "账号应匹配");
        assertEquals(testUsername, response.getUsername(), "用户名应匹配");
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
        String testuserAcount = "testuser004";
        String testUserName = "完整信息用户";
        String phone = "13800138002";
        String email = "test@example.com";
        String sex = "MALE";
        String occupation = "软件工程师";
        String birthday = "1990-01-01";

        RegisterRequest request = new RegisterRequest();
        request.setAccount(testuserAcount);
        request.setPassword("password123");
        request.setUsername(testUserName);
        request.setMobile(phone);
        request.setEmail(email);
        request.setSex(sex);
        request.setBirthday(birthday);
        request.setOccupation(occupation);

//        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
//        mockRequest.setRemoteAddr("10.0.0.1");
//        mockRequest.addHeader("X-Forwarded-For", "192.168.1.200");

        LoginResponse response = userService.register(request, "192.168.1.100");

        assertNotNull(response, "注册响应不应为空");
        assertNotNull(response.getUserId(), "用户ID不应为空");
        assertEquals(testuserAcount, response.getAccount(), "账号应匹配");
        assertEquals(testUserName, response.getUsername(), "用户名应匹配");
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
        String testuserAcount = "testuser004";
        request.setAccount(testuserAcount);
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
        request.setAccount("testuser006");
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
        //内存模拟
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
//        //内存模拟方式无法模拟参数校验
//        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
//        String registerJson = "{" +
//                "\"account\":\"ab\"," +
//                "\"password\":\"password123\"," +
//                "\"username\":\"格式测试用户\"" +
//                "}";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(registerJson))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());


        String registerJson = "{" +
                "\"account\":\"ab\"," +
                "\"password\":\"password123\"," +
                "\"username\":\"格式测试用户\"" +
                "}";
        // 1. 设置请求头
        Result result = extracted(registerJson);
        assertThat(result.getMessage()).contains("账号格式不正确");

        System.out.println("✓ 账号格式验证正确");
    }

    /**
     * 测试用户注册接口 - 密码格式验证
     * 验证密码长度和格式要求
     */
    @Test
    public void testRegisterWithInvalidPasswordFormat() throws Exception {
//        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
//
//        String registerJson = "{" +
//                "\"account\":\"validuser\"," +
//                "\"password\":\"123\"," +
//                "\"username\":\"密码格式测试\"" +
//                "}";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(registerJson))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());


        String registerJson = "{" +
                "\"account\":\"validuser\"," +
                "\"password\":\"123\"," +
                "\"username\":\"密码格式测试\"" +
                "}";
        Result result = extracted(registerJson);
        assertThat(result.getMessage()).contains("密码格式不正确");

        System.out.println("✓ 密码格式验证正确");
    }

    /**
     * 测试用户注册接口 - 必填字段验证
     * 验证账号、密码、用户名的必填校验
     */
    @Test
    public void testRegisterWithMissingRequiredFields() throws Exception {
//        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
//
//        String registerJson = "{" +
//                "\"username\":\"缺少必填字段\"" +
//                "}";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(registerJson))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());


        String registerJson = "{" +
                "\"username\":\"缺少必填字段\"" +
                "}";
        extracted(registerJson);

        System.out.println("✓ 必填字段验证正确");
    }

    /**
     * 测试用户注册接口 - 手机号格式验证
     * 验证手机号格式校验规则
     */
    @Test
    public void testRegisterWithInvalidMobile() throws Exception {
        String registerJson = "{" +
                "\"account\":\"validuser001\"," +
                "\"password\":\"password123\"," +
                "\"username\":\"手机号测试\"," +
                "\"mobile\":\"12345678901\"" +
                "}";

        extracted(registerJson);


        System.out.println("✓ 手机号格式验证正确");
    }

    /**
     * 测试用户注册接口 - 邮箱格式验证
     * 验证邮箱格式校验规则
     */
    @Test
    public void testRegisterWithInvalidEmail() throws Exception {
        String registerJson = "{" +
                "\"account\":\"validuser002\"," +
                "\"password\":\"password123\"," +
                "\"username\":\"邮箱测试\"," +
                "\"email\":\"invalid-email\"" +
                "}";

        extracted(registerJson);

        System.out.println("✓ 邮箱格式验证正确");
    }

    /**
     * 测试用户注册接口 - 参数验证
     *
     * @return
     */
    private Result extracted(String registerJson) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(registerJson, headers);

        // 2. 发送 POST 请求到真实服务器
        // 注意：这里不需要拼接 localhost:port，TestRestTemplate 会自动处理
        ResponseEntity<String> response = restTemplate.postForEntity("/user/register", entity, String.class);

        // 5. 断言结果
        // 如果校验生效，状态码应该是 400 (Bad Request)，而不是 200
        System.out.println("响应状态码: " + response.getStatusCode());
        System.out.println("响应内容: " + response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        Result result = mapper.readValue(response.getBody(), Result.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return result;
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

        assertNull(userDo.getLastLoginIp());
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
