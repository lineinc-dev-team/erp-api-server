package com.lineinc.erp.api.server.domain.organization.service.v1;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.organization.entity.Position;
import com.lineinc.erp.api.server.domain.organization.repository.PositionRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.organization.dto.response.PositionResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    @Transactional(readOnly = true)
    public List<PositionResponse> getAllPositions() {
        final List<Position> positions = positionRepository.findAll();
        return positions.stream()
                .sorted(Comparator.comparing(Position::getOrder))
                .map(position -> new PositionResponse(position.getId(), position.getName(), position.getOrder()))
                .collect(Collectors.toList());
    }
}
