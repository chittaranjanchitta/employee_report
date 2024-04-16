package com.employee_report.controller;

import com.employee_report.entity.Employee;
import com.employee_report.payload.EmployeeDTO;
import com.employee_report.payload.TaxDeductionDTO;
import com.employee_report.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> storeEmployeeDetails(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeService.storeEmployeeDetails(employeeDTO.getEmployeeId(),
                employeeDTO.getFirstName(), employeeDTO.getLastName(), employeeDTO.getEmail(),
                employeeDTO.getPhoneNumbers(), employeeDTO.getDoj(), employeeDTO.getSalary());
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @GetMapping("/tax-deduction")
    public ResponseEntity<List<TaxDeductionDTO>> getTaxDeductionForCurrentFinancialYear() {
        List<TaxDeductionDTO> taxDeductions = employeeService.calculateTaxDeductionForCurrentFinancialYear();
        return new ResponseEntity<>(taxDeductions, HttpStatus.OK);
    }

}

