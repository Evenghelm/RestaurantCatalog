package org.example.employee.kafka.listener;

import org.example.employee.model.EmployeeEntity;
import org.example.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaUserDeletionListener {
    @Autowired
    EmployeeRepository employeeRepository;

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
