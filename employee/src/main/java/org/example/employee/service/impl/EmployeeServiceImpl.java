package org.example.employee.service.impl;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.example.employee.dto.request.EmployeeRequestDTO;
import org.example.employee.dto.response.EmployeeResponseDTO;
import org.example.employee.error.NotFoundRecordException;
import org.example.employee.mapper.EmployeeMapper;
import org.example.employee.model.DepartmentEntity;
import org.example.employee.model.EmployeeEntity;
import org.example.employee.repository.DepartmentRepository;
import org.example.employee.repository.EmployeeRepository;
import org.example.employee.service.EmployeeService;
import org.example.employee.service.UserFeignClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private static final String DEPARTMENT_TABLE = "department";
    private static final String EMPLOYEE_TABLE = "employee";

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper mapper;
    private final DepartmentRepository departmentRepository;
    private final UserFeignClient userFeignClient;
    private final EntityManager entityManager;

    public EmployeeEntity getEmployeeEntityById(Long id, boolean showDeleted) {
        var t = employeeRepository.getEmployeeById(id, showDeleted);
        return Optional.of(id)
                .flatMap((_id) -> employeeRepository.getEmployeeById(_id, showDeleted))
                .orElseThrow(() -> new NotFoundRecordException(new Object[]{EMPLOYEE_TABLE, id.toString()}));
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long id, boolean showDeleted) {
        EmployeeEntity employeeEntity = getEmployeeEntityById(id, showDeleted);
        return mapper.toDTO(employeeEntity);
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {
        return getEmployeeById(id, false);
    }

    @Override
    public List<EmployeeResponseDTO> getEmployees() {
        List<EmployeeEntity> employees = employeeRepository.getEmployees();
        return employees.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {
        EmployeeEntity e = mapper.toEntity(request);

        Optional<DepartmentEntity> department = departmentRepository.findById(request.getDepartmentId());
        if (department.isEmpty()) {
            throw new NotFoundRecordException(new Object[]{DEPARTMENT_TABLE, request.getDepartmentId().toString()});
        }
        e.setDepartment(department.get());

        return Optional.of(employeeRepository.save(e))
                .map(mapper::toDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(EmployeeRequestDTO request) {
        EmployeeEntity updateEmployee = mapper.toEntity(request);

        Optional<DepartmentEntity> department = departmentRepository.findById(request.getDepartmentId());
        if (department.isEmpty()) {
            throw new NotFoundRecordException(new Object[]{DEPARTMENT_TABLE, request.getDepartmentId().toString()});
        }
        updateEmployee.setDepartment(department.get());

        EmployeeEntity employeeEntity = getEmployeeEntityById(request.getId(), false);
        employeeEntity.setDepartment(updateEmployee.getDepartment());
        employeeEntity.setEmail(updateEmployee.getEmail());
        employeeEntity.setName(updateEmployee.getName());
        employeeEntity.setSalary(updateEmployee.getSalary());
        return mapper.toDTO(employeeEntity);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        EmployeeEntity employeeEntity = getEmployeeEntityById(id, false);
        departmentRepository.removeChief(id);
        try {
            userFeignClient.deleteUser(employeeEntity.getUserId());
            employeeEntity.setIsUserDeleted(true);
            employeeRepository.saveAndFlush(employeeEntity);
        } catch (FeignException fe) {
            fe.printStackTrace();
        }
        employeeRepository.delete(employeeEntity);
    }

    @KafkaListener(topics = "userDeleted")
    public void userDeletionListener(Long userId) {
        List<EmployeeEntity> employeeEntities = employeeRepository.getEmployeesByUserId(userId);
        if (employeeEntities != null && employeeEntities.size() > 0) {
            for (var entity : employeeEntities) {
                entity.setIsUserDeleted(true);
                employeeRepository.saveAndFlush(entity);
            }
        }
    }
}
