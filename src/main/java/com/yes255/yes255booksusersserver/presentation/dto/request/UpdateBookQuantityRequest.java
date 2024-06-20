package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.enumtype.OperationType;

import java.util.List;

public record UpdateBookQuantityRequest (
    List<Long> bookIdList,
    List<Integer> quantityList,
    OperationType operationType
)
{

}
