package org.example.employee.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.example.employee.configuration.WireMockConfig;
import org.example.employee.dto.request.EmployeeRequestDTO;
import org.example.employee.dto.response.EmployeeResponseDTO;
import org.example.employee.error.NotFoundRecordException;
import org.example.employee.service.EmployeeService;
import org.example.employee.util.AppContextTest;
import org.example.employee.util.TestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
@Sql(scripts = "/data/insert_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(encoding = "utf-8"))
@Sql(scripts = "/data/clear_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(encoding = "utf-8"))
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {WireMockConfig.class})
public class EmployeeControllerTest extends AppContextTest {

    private static final Locale LOCALE_RU = new Locale("ru", "RU");

    private static final String BASE_REQUEST = "json/request/";
    private static final String BASE_RESPONSE = "json/response/";

    private static final String REQUEST_CREATE_SUCCESS = BASE_REQUEST + "employee_create_success.json";
    private static final String REQUEST_CREATE_EXCEPTION = BASE_REQUEST + "employee_create_exception.json";
    private static final String REQUEST_UPDATE_SUCCESS = BASE_REQUEST + "employee_update_success.json";
    private static final String REQUEST_UPDATE_EXCEPTION = BASE_REQUEST + "employee_update_exception.json";

    private static final String RESPONSE_GET_ID = BASE_RESPONSE + "employee_get_id.json";
    private static final String RESPONSE_GET_ALL = BASE_RESPONSE + "employees_get_all.json";
    private static final String RESPONSE_CREATE_SUCCESS = BASE_RESPONSE + "employee_create_success.json";
    private static final String RESPONSE_UPDATE_SUCCESS = BASE_RESPONSE + "employee_update_success.json";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WireMockServer mockUserService;

    @Autowired
    private EmployeeService employeeService;

    @Test
    void getEmployeeByIdSuccessTest() throws Exception {
        final EmployeeResponseDTO response =
                TestUtil.readJsonResource(RESPONSE_GET_ID, EmployeeResponseDTO.class);
        final String expected = TestUtil.write(response);
        this.mockMvc
                .perform(get("/employees/{id}", 2L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expected));
    }

    @Test
    void getEmployeeByIdExceptionTest() throws Exception {
        final String expected = "В таблице: employee не найдена запись с идентификатором: 3";
        this.mockMvc
                .perform(get("/employees/{id}", 3L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof NotFoundRecordException))
                .andExpect(content().string(expected));
    }

    /**
     * Обязательно выполняется первым, до тестирования создания и удаления и редактирования.
     * @throws Exception - exception
     */
    @Test
    void getEmployeesTest() throws Exception {
        final List<EmployeeResponseDTO> response =
                TestUtil.readJsonResourceToList(RESPONSE_GET_ALL, EmployeeResponseDTO.class);
        final String expected = TestUtil.write(response);
        this.mockMvc
                .perform(get("/employees"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expected));
    }

    @Test
    void deleteEmployeeSuccessTest() throws Exception {
        this.mockMvc
                .perform(delete("/employees/{id}", 2L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteEmployeeExceptionTest() throws Exception {
        final String expected = "В таблице: employee не найдена запись с идентификатором: 3";
        this.mockMvc
                .perform(delete("/employees/{id}", 3L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof NotFoundRecordException))
                .andExpect(content().string(expected));
    }

    @Test
    void createEmployeeSuccessTest() throws Exception {
        final byte[] requestBytes = TestUtil.readResource(REQUEST_CREATE_SUCCESS).readAllBytes();

        InputStream inputStream = TestUtil.readResource(RESPONSE_CREATE_SUCCESS);
        final String expected = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        this.mockMvc
                .perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBytes)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().encoding(StandardCharsets.UTF_8))
                .andExpect(json().ignoring("##IGNORE##").isEqualTo(expected));
    }

    @Test
    void createEmployeeExceptionTest() throws Exception {
        final byte[] requestBytes =
                TestUtil.readResource(REQUEST_CREATE_EXCEPTION).readAllBytes();
        final String json = "{\"name\":\"должно соответствовать \\\"^[а-яА-ЯёЁ ]+\\\"\"," +
                "\"id\":\"должно равняться null\"," +
                "\"departmentId\":\"не должно равняться null\"," +
                "\"email\":\"должно иметь формат адреса электронной почты\"}";

        this.mockMvc
                .perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBytes)
                        .locale(LOCALE_RU)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(content().json(json));
    }

    @Test
    void updateEmployeeSuccessTest() throws Exception {
        final EmployeeRequestDTO request =
                TestUtil.readJsonResource(REQUEST_UPDATE_SUCCESS, EmployeeRequestDTO.class);
        final EmployeeResponseDTO response =
                TestUtil.readJsonResource(RESPONSE_UPDATE_SUCCESS, EmployeeResponseDTO.class);
        final byte[] requestBytes = TestUtil.readResource(REQUEST_UPDATE_SUCCESS).readAllBytes();
        final String expected = TestUtil.write(response);
        this.mockMvc
                .perform(put("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBytes)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().encoding(StandardCharsets.UTF_8))
                .andExpect(content().string(expected));
    }

    @Test
    void updateEmployeeExceptionTest() throws Exception {
        final byte[] requestBytes =
                TestUtil.readResource(REQUEST_UPDATE_EXCEPTION).readAllBytes();
        final String expected = "{\"id\":\"не должно равняться null\"," +
                "\"email\":\"должно иметь формат адреса электронной почты\"," +
                "\"salary\":\"не должно равняться null\"}";

        this.mockMvc
                .perform(put("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBytes)
                        .locale(LOCALE_RU)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(content().json(expected));
    }
    @Test
    @Sql({"/data/insert_test_data.sql", "/data/insert_user_data.sql"})
    @Sql(scripts = "/data/clear_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteEmployeeWithUserTest() throws Exception {
        mockUserService.stubFor(WireMock.delete(WireMock.urlEqualTo("/users/2"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())));

        this.mockMvc.perform(delete("/employees/{id}", 2L));
        assertThrowsExactly(NotFoundRecordException.class, () -> employeeService.getEmployeeById(2L));
        assertTrue(employeeService.getEmployeeById(2L, true).getIsUserDeleted());
    }
    @Test
    @Sql({"/data/insert_test_data.sql", "/data/insert_user_data.sql"})
    @Sql(scripts = "/data/clear_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteEmployeeWithUserExceptionTest() throws Exception {
        mockUserService.stubFor(WireMock.delete(WireMock.urlEqualTo("/users/1"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())));

        this.mockMvc.perform(delete("/employees/{id}", 1L));
        assertFalse(employeeService.getEmployeeById(1L, true).getIsUserDeleted());
    }
}
