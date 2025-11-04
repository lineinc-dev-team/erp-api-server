package com.lineinc.erp.api.server.domain.fuelaggregation.service.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregationChangeHistory;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfoSubEquipment;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationChangeType;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.service.v1.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1.OutsourcingCompanyContractService;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationUpdateRequest.FuelInfoUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuelInfoService {

    private final FuelAggregationChangeHistoryRepository fuelAggregationChangeHistoryRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final OutsourcingCompanyContractService outsourcingCompanyContractService;
    private final Javers javers;
    private final UserService userService;

    @Transactional
    public void updateFuelInfos(final FuelAggregation fuelAggregation, final List<FuelInfoUpdateRequest> requests,
            final Long userId) {
        // 모든 FuelInfo와 서브장비의 transient 필드 동기화
        fuelAggregation.getFuelInfos().forEach(fuelInfo -> {
            fuelInfo.syncTransientFields();
            fuelInfo.getSubEquipments().forEach(FuelInfoSubEquipment::syncTransientFields);
        });

        // 변경 전 상태 저장 (Javers 스냅샷) - syncTransientFields 호출 후
        final List<FuelInfo> beforeFuelInfos = fuelAggregation.getFuelInfos().stream()
                .map(fuelInfo -> {
                    final FuelInfo snapshot = JaversUtils.createSnapshot(javers, fuelInfo, FuelInfo.class);
                    // 서브장비 목록도 깊은 복사로 생성
                    snapshot.setSubEquipments(fuelInfo.getSubEquipments().stream()
                            .map(subEquipment -> JaversUtils.createSnapshot(javers, subEquipment,
                                    FuelInfoSubEquipment.class))
                            .collect(Collectors.toList()));
                    return snapshot;
                })
                .toList();

        // EntitySyncUtils를 사용하여 유류정보 목록 동기화
        EntitySyncUtils.syncList(
                fuelAggregation.getFuelInfos(),
                requests,
                (final FuelInfoUpdateRequest dto) -> {
                    // 업체, 기사, 장비 ID 검증
                    final OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                            .getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId());
                    final OutsourcingCompanyContractDriver driver = dto.driverId() != null
                            ? outsourcingCompanyContractService.getDriverByIdOrThrow(dto.driverId())
                            : null;
                    final OutsourcingCompanyContractEquipment equipment = outsourcingCompanyContractService
                            .getEquipmentByIdOrThrow(dto.equipmentId());

                    return FuelInfo.builder()
                            .fuelAggregation(fuelAggregation)
                            .outsourcingCompany(outsourcingCompany)
                            .driver(driver)
                            .equipment(equipment)
                            .categoryType(dto.categoryType())
                            .fuelType(dto.fuelType())
                            .fuelAmount(dto.fuelAmount())
                            .memo(dto.memo())
                            .fileUrl(dto.fileUrl())
                            .originalFileName(dto.originalFileName())
                            .build();
                });

        // EntitySyncUtils 실행 후, 각 FuelInfo에 실제 엔티티 설정
        final Map<Long, FuelInfoUpdateRequest> requestMap = requests.stream()
                .filter(r -> r.id() != null)
                .collect(Collectors.toMap(FuelInfoUpdateRequest::id, Function.identity()));

        for (final FuelInfo fuelInfo : fuelAggregation.getFuelInfos()) {
            if (fuelInfo.getId() != null && requestMap.containsKey(fuelInfo.getId())) {
                final FuelInfoUpdateRequest request = requestMap.get(fuelInfo.getId());
                final OutsourcingCompany company = outsourcingCompanyService
                        .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId());
                final OutsourcingCompanyContractDriver driver = request.driverId() != null
                        ? outsourcingCompanyContractService.getDriverByIdOrThrow(request.driverId())
                        : null;
                final OutsourcingCompanyContractEquipment equipment = outsourcingCompanyContractService
                        .getEquipmentByIdOrThrow(request.equipmentId());

                fuelInfo.updateFrom(request, company, driver, equipment);

                // 서브장비 정보 동기화
                if (request.subEquipments() != null) {
                    EntitySyncUtils.syncList(
                            fuelInfo.getSubEquipments(),
                            request.subEquipments(),
                            (final FuelInfoUpdateRequest.FuelInfoSubEquipmentUpdateRequest dto) -> {
                                final OutsourcingCompanyContractSubEquipment subEquipment = dto
                                        .outsourcingCompanyContractSubEquipmentId() != null
                                                ? outsourcingCompanyContractService
                                                        .getSubEquipmentByIdOrThrow(
                                                                dto.outsourcingCompanyContractSubEquipmentId())
                                                : null;

                                return FuelInfoSubEquipment.builder()
                                        .fuelInfo(fuelInfo)
                                        .outsourcingCompanyContractSubEquipment(subEquipment)
                                        .fuelType(dto.fuelType())
                                        .fuelAmount(dto.fuelAmount())
                                        .memo(dto.memo())
                                        .build();
                            });

                    // EntitySyncUtils 실행 후, 각 서브장비에 실제 서브장비 엔티티 설정
                    final Map<Long, FuelInfoUpdateRequest.FuelInfoSubEquipmentUpdateRequest> subEquipmentRequestMap = request
                            .subEquipments().stream()
                            .filter(r -> r.id() != null)
                            .collect(Collectors.toMap(
                                    FuelInfoUpdateRequest.FuelInfoSubEquipmentUpdateRequest::id,
                                    Function.identity()));

                    for (final FuelInfoSubEquipment fuelInfoSubEquipment : fuelInfo.getSubEquipments()) {
                        if (fuelInfoSubEquipment.getId() != null
                                && subEquipmentRequestMap.containsKey(fuelInfoSubEquipment.getId())) {
                            final FuelInfoUpdateRequest.FuelInfoSubEquipmentUpdateRequest subEquipmentRequest = subEquipmentRequestMap
                                    .get(fuelInfoSubEquipment.getId());
                            final OutsourcingCompanyContractSubEquipment subEquipment = subEquipmentRequest
                                    .outsourcingCompanyContractSubEquipmentId() != null
                                            ? outsourcingCompanyContractService
                                                    .getSubEquipmentByIdOrThrow(
                                                            subEquipmentRequest
                                                                    .outsourcingCompanyContractSubEquipmentId())
                                            : null;

                            // 실제 서브장비 엔티티로 업데이트
                            fuelInfoSubEquipment.updateFrom(subEquipmentRequest, subEquipment);
                        }
                    }

                    fuelInfo.getSubEquipments().forEach(FuelInfoSubEquipment::syncTransientFields);
                }
            }
        }

        // 새로 생성된 엔티티들도 transient 필드 동기화
        fuelAggregation.getFuelInfos().forEach(fuelInfo -> {
            fuelInfo.syncTransientFields();
            fuelInfo.getSubEquipments().forEach(FuelInfoSubEquipment::syncTransientFields);
        });

        // 변경사항 감지 및 이력 저장
        final List<FuelInfo> afterFuelInfos = new ArrayList<>(fuelAggregation.getFuelInfos());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 유류정보
        final Set<Long> beforeIds = beforeFuelInfos.stream()
                .map(FuelInfo::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        for (final FuelInfo after : afterFuelInfos) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 유류정보
        final Map<Long, FuelInfo> afterMap = afterFuelInfos.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(FuelInfo::getId, f -> f));

        for (final FuelInfo before : beforeFuelInfos) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final FuelInfo after = afterMap.get(before.getId());

            // 유류정보 단위 변경 감지 (서브장비는 제외)
            // 서브장비를 임시로 제거하여 비교
            final List<FuelInfoSubEquipment> beforeSubEquipmentsTemp = before.getSubEquipments();
            final List<FuelInfoSubEquipment> afterSubEquipmentsTemp = after.getSubEquipments();
            before.setSubEquipments(new ArrayList<>());
            after.setSubEquipments(new ArrayList<>());

            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);

            // 서브장비 목록 복원
            before.setSubEquipments(beforeSubEquipmentsTemp);
            after.setSubEquipments(afterSubEquipmentsTemp);

            // 유류정보가 그대로 존재하는 경우에만 서브장비 변경사항 감지
            final List<FuelInfoSubEquipment> beforeSubEquipments = before.getSubEquipments();
            final List<FuelInfoSubEquipment> afterSubEquipments = after.getSubEquipments();
            final Set<Long> beforeSubEquipmentIds = beforeSubEquipments.stream()
                    .map(FuelInfoSubEquipment::getId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());

            // 서브장비 추가 감지
            for (final FuelInfoSubEquipment afterSubEquipment : afterSubEquipments) {
                if (afterSubEquipment.getId() == null || !beforeSubEquipmentIds.contains(afterSubEquipment.getId())) {
                    allChanges.add(JaversUtils.extractAddedEntityChange(javers, afterSubEquipment));
                }
            }

            // 서브장비 수정 감지
            final Map<Long, FuelInfoSubEquipment> afterSubEquipmentMap = afterSubEquipments.stream()
                    .filter(s -> s.getId() != null)
                    .collect(Collectors.toMap(FuelInfoSubEquipment::getId, s -> s));

            for (final FuelInfoSubEquipment beforeSubEquipment : beforeSubEquipments) {
                if (beforeSubEquipment.getId() == null || !afterSubEquipmentMap.containsKey(beforeSubEquipment.getId()))
                    continue;

                final FuelInfoSubEquipment afterSubEquipment = afterSubEquipmentMap.get(beforeSubEquipment.getId());

                final Diff subEquipmentDiff = javers.compare(beforeSubEquipment, afterSubEquipment);
                final List<Map<String, String>> subEquipmentModified = JaversUtils.extractModifiedChanges(javers,
                        subEquipmentDiff);
                allChanges.addAll(subEquipmentModified);
            }
        }

        // 변경사항이 있다면 이력 저장
        if (!allChanges.isEmpty()) {
            final String changesJson = javers.getJsonConverter().toJson(allChanges);
            final FuelAggregationChangeHistory changeHistory = FuelAggregationChangeHistory.builder()
                    .fuelAggregation(fuelAggregation)
                    .type(FuelAggregationChangeType.FUEL_INFO)
                    .changes(changesJson)
                    .user(userService.getUserByIdOrThrow(userId))
                    .build();
            fuelAggregationChangeHistoryRepository.save(changeHistory);
        }
    }
}
