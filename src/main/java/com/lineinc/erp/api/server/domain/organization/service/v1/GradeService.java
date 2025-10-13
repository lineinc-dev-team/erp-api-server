package com.lineinc.erp.api.server.domain.organization.service.v1;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.repository.GradeRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.organization.dto.response.GradeResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;

    @Transactional(readOnly = true)
    public List<GradeResponse> getAllGrades() {
        final List<Grade> grades = gradeRepository.findAll();
        return grades.stream()
                .sorted(Comparator.comparing(Grade::getOrder))
                .map(grade -> new GradeResponse(grade.getId(), grade.getName(), grade.getOrder()))
                .collect(Collectors.toList());
    }
}
