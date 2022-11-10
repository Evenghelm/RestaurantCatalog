package org.example.employee.service;

import org.example.employee.dto.response.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "userFeignClient",url = "localhost:8081/users")
public interface UserFeignClient {
    @GetMapping("/{id}")
    UserResponseDTO getUserById(@PathVariable("id") Long id);
    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable("id") Long id);
}
