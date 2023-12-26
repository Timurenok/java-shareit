package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.exception.InvalidSizeException;

@Setter
@Getter
public class Pagination {
    private Integer pageSize;
    private Integer amountOfPages;
    private Integer index;

    public Pagination(Integer from, Integer size) {
        if (size != null) {
            if ((from < 0) || (size < 0)) {
                throw new InvalidSizeException("Size and from must be positive");
            }
            if (size.equals(0)) {
                throw new InvalidSizeException("Size can't equal 0");
            }
        }
        pageSize = from;
        index = 1;
        amountOfPages = 0;
        if (size == null) {
            if (from.equals(0)) {
                pageSize = 1000;
                index = 0;
            }
        } else {
            if (from.equals(0)) {
                pageSize = size;
                index = 0;
            }
            amountOfPages = index + 1;
            if ((from < size) && (!from.equals(0))) {
                amountOfPages = size / from + index;
                if (size % from != 0) {
                    amountOfPages++;
                }
            }
        }
    }
}
