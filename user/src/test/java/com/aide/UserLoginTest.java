package com.aide;

import com.aide.adapter.VO.LoginResponse;
import com.aide.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mazg
 * @description user测试类
 * @date 2026/5/19
 * @date 15:47
 */
@SpringBootTest(classes = UserClientApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserLoginTest {

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    /**
     * @author mazg
     * @description 测试用户登录接口
     * @date 2026/5/19
     * @date 15:29
     **/
    @Test
    public void testLogin() {
        String testuserName = "testuser001";
        LoginResponse loginResponse = userService.login(testuserName, "password123", "1121");

        assertNotNull(loginResponse, "登录响应不应为空");
        assertNotNull(loginResponse.getUserId(), "用户ID不应为空");
        assertEquals(testuserName, loginResponse.getAccount(), "账号应匹配");
        assertNotNull(loginResponse.getUsername(), "用户名不应为空");
        assertNotNull(loginResponse.getToken(), "Token不应为空");
        assertFalse(loginResponse.getToken().isEmpty(), "Token不应为空字符串");
        assertNotNull(loginResponse.getRole(), "角色不应为空");
        assertNotNull(loginResponse.getStatus(), "状态不应为空");

        System.out.println("✓ 登录成功！");
        System.out.println("  用户ID: " + loginResponse.getUserId());
        System.out.println("  账号: " + loginResponse.getAccount());
        System.out.println("  用户名: " + loginResponse.getUsername());
        System.out.println("  Token: " + loginResponse.getToken());
        System.out.println("  角色: " + loginResponse.getRole());
        System.out.println("  状态: " + loginResponse.getStatus());
    }

    /**
     * @author mazg
     * @description 测试用户登录接口 - 密码错误场景
     * @date 2026/5/19
     **/
    @Test
    public void testLoginWithWrongPassword() {
        Exception exception = assertThrows(Exception.class, () -> {
            userService.login("testuser001", "ssss", null);
        });

        assertNotNull(exception.getMessage(), "异常消息不应为空");
        System.out.println("✓ 捕获到预期异常: " + exception.getMessage());
    }

    /**
     * @author mazg
     * @description 测试用户登录接口 - 空账号场景
     * @date 2026/5/19
     **/
    @Test
    public void testLoginWithEmptyAccount() {
        Exception exception = assertThrows(Exception.class, () -> {
            userService.login("", "123456", null);
        });

        assertNotNull(exception.getMessage(), "异常消息不应为空");
        System.out.println("✓ 捕获到预期异常: " + exception.getMessage());
    }

    /**
     * @author mazg
     * @description 测试用户登录接口 - 不存在的账号场景
     * @date 2026/5/19
     **/
    @Test
    public void testLoginWithNonExistentAccount() {
        Exception exception = assertThrows(Exception.class, () -> {
            userService.login("nonexistent_user", "123456", null);
        });

        assertNotNull(exception.getMessage(), "异常消息不应为空");
        System.out.println("✓ 捕获到预期异常: " + exception.getMessage());
    }
}
