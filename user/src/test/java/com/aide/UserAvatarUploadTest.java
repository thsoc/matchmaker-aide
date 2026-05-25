package com.aide;

import com.aide.common.auth.context.UserContext;
import com.aide.common.auth.entity.UserInfo;
import com.aide.entity.DO.UserDo;
import com.aide.entity.PO.User;
import com.aide.entity.VO.LoginResponse;
import com.aide.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户头像上传功能测试
 */
@SpringBootTest(classes = UserClientApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("用户头像上传测试")
public class UserAvatarUploadTest {

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private LoginResponse testUserLogin;  // 缓存登录用户

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // ✅ 登录并缓存用户信息
        if (testUserLogin == null) {
            testUserLogin = userService.login("testuser001", "password123", "127.0.0.1");

            // 设置 UserContext
            User user = userService.getById(testUserLogin.getUserId());
            UserInfo userinfo = UserInfo.builder()
                    .id(user.getId())
                    .account(user.getAccount())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .description(user.getDescription())
                    .introduce(user.getIntroduce())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .sex(user.getSex())
                    .avatar(user.getAvatar())
                    .email(user.getEmail())
                    .mobile(user.getMobile())
                    .birthday(user.getBirthday())
                    .income(user.getIncome())
                    .occupation(user.getOccupation())
                    .integral(user.getIntegral())
                    .loginCount(user.getLoginCount())
                    .lastLoginTime(user.getLastLoginTime())
                    .createTime(user.getCreateTime())
                    .updateTime(user.getUpdateTime())
                    .delFlag(user.getDelFlag())
                    .createBy(user.getCreateBy())
                    .updateBy(user.getUpdateBy())
                    .version(user.getVersion()).build();
            UserContext.setUser(userinfo);
        }
    }

    @AfterEach
    public void tearDown() {
        // ✅ 清理 UserContext
        UserContext.clear();
    }

    /**
     * 测试正常头像上传 - JPG格式
     */
    @Test
    @DisplayName("测试上传JPG格式头像")
    public void testUploadAvatarJpg() throws Exception {
        // 创建模拟图片文件（JPG格式）
        byte[] imageContent = new byte[1024 * 100]; // 100KB的模拟图片数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                imageContent
        );

        // 创建模拟HttpServletRequest
        HttpServletRequest request = createMockRequest(testUserLogin.getToken());

        // 执行上传
        String avatarUrl = userService.uploadAvatar(mockFile, request);

        // 验证结果
        assertNotNull(avatarUrl, "头像URL不应为空");
        assertFalse(avatarUrl.isEmpty(), "头像URL不应为空字符串");
        assertTrue(avatarUrl.contains("/avatars/"), "头像URL应包含avatars目录");
        assertTrue(avatarUrl.startsWith("http://"), "头像URL应以http开头");

        System.out.println("✓ JPG头像上传成功！");
        System.out.println("  头像URL: " + avatarUrl);
    }

    /**
     * 测试PNG格式头像上传
     */
    @Test
    @DisplayName("测试上传PNG格式头像")
    public void testUploadAvatarPng() throws Exception {
        byte[] imageContent = new byte[1024 * 200]; // 200KB
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "profile.png",
                "image/png",
                imageContent
        );

        HttpServletRequest request = createMockRequest(testUserLogin.getToken());
        String avatarUrl = userService.uploadAvatar(mockFile, request);

        assertNotNull(avatarUrl, "PNG头像URL不应为空");
        assertTrue(avatarUrl.contains(".png"), "URL应包含.png扩展名");

        System.out.println("✓ PNG头像上传成功！");
        System.out.println("  头像URL: " + avatarUrl);
    }

    /**
     * 测试GIF格式头像上传
     */
    @Test
    @DisplayName("测试上传GIF格式头像")
    public void testUploadAvatarGif() throws Exception {
        byte[] imageContent = new byte[1024 * 50]; // 50KB
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "animated.gif",
                "image/gif",
                imageContent
        );

        HttpServletRequest request = createMockRequest(testUserLogin.getToken());
        String avatarUrl = userService.uploadAvatar(mockFile, request);

        assertNotNull(avatarUrl, "GIF头像URL不应为空");

        System.out.println("✓ GIF头像上传成功！");
    }

    /**
     * 测试上传空文件 - 应该抛出异常
     */
    @Test
    @DisplayName("测试上传空文件应该失败")
    public void testUploadEmptyFile() throws Exception {
        byte[] emptyContent = new byte[0];
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                emptyContent
        );

        HttpServletRequest request = createMockRequest(testUserLogin.getToken());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.uploadAvatar(mockFile, request);
        });

        assertNotNull(exception.getMessage(), "异常消息不应为空");
        System.out.println("✓ 捕获到预期异常: " + exception.getMessage());
    }

    /**
     * 测试上传非图片文件 - 应该抛出异常
     */
    @Test
    @DisplayName("测试上传非图片文件应该失败")
    public void testUploadNonImageFile() throws Exception {
        byte[] textContent = "This is not an image".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                textContent
        );

        HttpServletRequest request = createMockRequest(testUserLogin.getToken());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.uploadAvatar(mockFile, request);
        });

        assertTrue(exception.getMessage().contains("图片") || 
                   exception.getMessage().contains("支持"), 
                   "异常消息应提示只支持图片格式");
        
        System.out.println("✓ 非图片文件被正确拒绝: " + exception.getMessage());
    }

    /**
     * 测试上传超大文件 - 应该抛出异常（超过5MB限制）
     */
    @Test
    @DisplayName("测试上传超大文件应该失败")
    public void testUploadOversizedFile() throws Exception {
        // 创建6MB的文件（超过5MB限制）
        byte[] largeContent = new byte[6 * 1024 * 1024];
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );

        HttpServletRequest request = createMockRequest(testUserLogin.getToken());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.uploadAvatar(mockFile, request);
        });

        assertTrue(exception.getMessage().contains("5MB") || 
                   exception.getMessage().contains("大小"), 
                   "异常消息应提示文件大小限制");

        System.out.println("✓ 超大文件被正确拒绝: " + exception.getMessage());
    }

    /**
     * 测试不同大小的合法文件
     */
    @Test
    @DisplayName("测试不同大小的合法文件")
    public void testUploadVariousSizes() throws Exception {
        // 测试小文件（10KB）
        testFileSize(10 * 1024, testUserLogin, "10KB");

        // 测试中等文件（1MB）
        testFileSize(1024 * 1024, testUserLogin, "1MB");

        // 测试接近限制的文件（4.9MB）
        testFileSize((int)(4.9 * 1024 * 1024), testUserLogin, "4.9MB");

        System.out.println("✓ 各种大小的文件上传测试通过！");
    }

    /**
     * 辅助方法：测试指定大小的文件
     */
    private void testFileSize(int size, LoginResponse loginResponse, String description) throws Exception {
        byte[] content = new byte[size];
        // 填充一些数据使文件更真实
        for (int i = 0; i < Math.min(size, 100); i++) {
            content[i] = (byte)(i % 256);
        }

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        HttpServletRequest request = createMockRequest(loginResponse.getToken());
        String avatarUrl = userService.uploadAvatar(mockFile, request);

        assertNotNull(avatarUrl, description + "文件的头像URL不应为空");
        System.out.println("  ✓ " + description + " 文件上传成功");
    }

    /**
     * 创建模拟HttpServletRequest
     */
    private HttpServletRequest createMockRequest(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8081);
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }
}
