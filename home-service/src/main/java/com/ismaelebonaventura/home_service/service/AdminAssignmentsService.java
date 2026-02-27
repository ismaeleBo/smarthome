package com.ismaelebonaventura.home_service.service;

import com.ismaelebonaventura.home_service.dto.UserHomeAssignmentsResponse;
import com.ismaelebonaventura.home_service.repository.HomeMemberRepository;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAssignmentsService {

    private final HomeRepository homeRepository;
    private final HomeMemberRepository homeMemberRepository;

    @Transactional(readOnly = true)
    public UserHomeAssignmentsResponse getUserAssignments() {

        List<UserHomeAssignmentsResponse.UserHomeAssignment> heads = homeRepository
                .findByHeadUserIdIsNotNull()
                .stream()
                .map(h -> new UserHomeAssignmentsResponse.UserHomeAssignment(
                        h.getHeadUserId(),
                        h.getHomeId()
                ))
                .toList();

        List<UserHomeAssignmentsResponse.UserHomeAssignment> members = homeMemberRepository
                .findAll()
                .stream()
                .map(m -> new UserHomeAssignmentsResponse.UserHomeAssignment(
                        m.getMemberUserId(),
                        m.getHomeId()
                ))
                .toList();

        return new UserHomeAssignmentsResponse(heads, members);
    }
}