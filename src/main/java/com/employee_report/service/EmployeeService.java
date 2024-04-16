package com.employee_report.service;

import com.employee_report.entity.Employee;
import com.employee_report.payload.TaxDeductionDTO;
import com.employee_report.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee storeEmployeeDetails(String employeeId, String firstName, String lastName, String email,
                                         List<String> phoneNumbers, LocalDate doj, BigDecimal salary) {

        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPhoneNumbers(phoneNumbers);
        employee.setDoj(doj);
        employee.setSalary(salary);

        return employeeRepository.save(employee);
    }
    public List<TaxDeductionDTO> calculateTaxDeductionForCurrentFinancialYear() {
        List<Employee> employees = employeeRepository.findAll();
        List<TaxDeductionDTO> taxDeductions = new ArrayList<>();

        for (Employee employee : employees) {
            LocalDate doj = employee.getDoj();
            BigDecimal yearlySalary = calculateYearlySalary(employee.getSalary(), doj);
            BigDecimal taxAmount = calculateTaxAmount(yearlySalary);
            BigDecimal cessAmount = calculateCessAmount(yearlySalary);

            taxDeductions.add(new TaxDeductionDTO(
                    employee.getEmployeeId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    yearlySalary,
                    taxAmount,
                    cessAmount
            ));
        }

        return taxDeductions;
    }

    private BigDecimal calculateYearlySalary(BigDecimal salary, LocalDate doj) {
        LocalDate currentDate = LocalDate.now();
        int totalMonths = (currentDate.getYear() - doj.getYear()) * 12 + currentDate.getMonthValue() - doj.getMonthValue();
        BigDecimal totalSalary = salary.multiply(BigDecimal.valueOf(totalMonths));
        int remainingDaysInCurrentMonth = currentDate.lengthOfMonth() - currentDate.getDayOfMonth();
        BigDecimal lossOfPay = salary.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(remainingDaysInCurrentMonth));
        totalSalary = totalSalary.subtract(lossOfPay);
        return totalSalary;
    }


    private BigDecimal calculateTaxAmount(BigDecimal yearlySalary) {
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal remainingSalary = yearlySalary.subtract(BigDecimal.valueOf(250000));
        if (remainingSalary.compareTo(BigDecimal.ZERO) > 0) {
            taxAmount = remainingSalary.min(BigDecimal.valueOf(250000)).multiply(BigDecimal.valueOf(0.05))
                    .add(remainingSalary.min(BigDecimal.valueOf(500000)).max(BigDecimal.ZERO).multiply(BigDecimal.valueOf(0.1)))
                    .add(remainingSalary.min(BigDecimal.valueOf(1000000)).max(BigDecimal.ZERO).multiply(BigDecimal.valueOf(0.2)));
        }
        return taxAmount;
    }

    private BigDecimal calculateCessAmount(BigDecimal yearlySalary) {
        BigDecimal excessAmount = yearlySalary.subtract(BigDecimal.valueOf(2500000)).max(BigDecimal.ZERO);
        return excessAmount.multiply(BigDecimal.valueOf(0.02));
    }



}

