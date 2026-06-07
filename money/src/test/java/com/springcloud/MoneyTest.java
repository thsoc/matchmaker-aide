package com.springcloud;

import com.aide.MoneyClientApp;
import com.aide.common.Result.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Unit test for simple App.
 */
@SpringBootTest(classes = MoneyClientApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoneyTest extends TestCase {

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers;

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ACCOUNT_HEADER = "X-User-Account";
    private static final String USER_NAME_HEADER = "X-User-Username";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_SEX_HEADER = "X-User-Sex";
    private static final String USER_CACHE_PREFIX = "user:cache:";


    @BeforeEach
    public void setUp(TestInfo testInfo) {//非错误，idea校验问题爆红，忽略
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        // 获取当前正在执行的测试方法名称
//        String methodName = testInfo.getTestMethod().get().getName();
//        if ("testRecharge".equals(methodName)) {
//            return;
//        }
        headers.put(USER_ID_HEADER, Arrays.asList("123123"));
        headers.put(USER_ACCOUNT_HEADER, Arrays.asList("123"));
        headers.put(USER_NAME_HEADER, Arrays.asList("123"));
        headers.put(USER_ROLE_HEADER, Arrays.asList("123"));
        headers.put(USER_SEX_HEADER, Arrays.asList("123"));
    }

    /**
     * 正常测试用户充值，参数校验
     */
    @Test
    public void testFailRecharge() throws JsonProcessingException {

        String registerJson = "{\"amount\":100}";
        String url = "/money/recharge";
        Result result = extracted(registerJson, url);
        assertThat(result.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        System.out.println("参数错误");
        System.out.println(result.getMessage());
    }

    @Test
    public void testSuccessRecharge() throws JsonProcessingException {

        String registerJson = "{\"userId\":100,\"amount\":100,\"payType\":1}";
        String url = "/money/recharge";
        Result result = extracted(registerJson, url);
        assertThat(result.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        System.out.println("参数错误");
        System.out.println(result.getMessage());
    }


    private Result extracted(String registerJson, String url) throws JsonProcessingException {
        HttpEntity<String> entity = new HttpEntity<>(registerJson, headers);

        // 2. 发送 POST 请求到真实服务器
        // 注意：这里不需要拼接 localhost:port，TestRestTemplate 会自动处理
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // 5. 断言结果
        // 如果校验生效，状态码应该是 400 (Bad Request)，而不是 200
        System.out.println("响应状态码: " + response.getStatusCode());
        System.out.println("响应内容: " + response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        Result result = mapper.readValue(response.getBody(), Result.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(result.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return result;
    }

}
