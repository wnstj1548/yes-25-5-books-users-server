package com.yes255.yes255booksusersserver.presentation.dto.request;

import java.util.List;

public record UpdateBookQuantityRequest (
    List<Long> bookIdList,
    List<Integer> quantityList
)
{

}
