package com.fooddelivery.users.mapper;

import com.fooddelivery.users.domain.User;
import com.fooddelivery.users.domain.UserProfileHistory;
import com.fooddelivery.users.dto.UserCreateRequest;
import com.fooddelivery.users.dto.UserHistoryResponse;
import com.fooddelivery.users.dto.UserResponse;
import com.fooddelivery.users.dto.UserUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    User toEntity(UserCreateRequest request);

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(UserUpdateRequest request, @MappingTarget User user);

    @Mapping(target = "id", source = "userId")
    UserHistoryResponse toHistoryResponse(UserProfileHistory history);

    @Mapping(target = "recordedAt", ignore = true)
    UserHistoryResponse toHistoryResponse(User user);
}
