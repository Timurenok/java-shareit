package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.InvalidSizeException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationTest {

    @Test
    public void shouldThrowExceptionWhenSizeNotPositive() {
        assertThrows(InvalidSizeException.class, () -> new Pagination(1, -1));
    }

    @Test
    public void shouldThrowExceptionWhenFromNotPositive() {
        assertThrows(InvalidSizeException.class, () -> new Pagination(-1, 1));
    }

    @Test
    public void shouldThrowExceptionWhenSizeEqualsZero() {
        assertThrows(InvalidSizeException.class, () -> new Pagination(0, 0));
    }

    @Test
    public void shouldReturnPageWhenSizeLessFrom() {
        Pagination pagination = new Pagination(2, 1);
        assertThat(pagination.getIndex()).isEqualTo(1);
        assertThat(pagination.getPageSize()).isEqualTo(2);
        assertThat(pagination.getAmountOfPages()).isEqualTo(2);
    }

    @Test
    public void shouldReturnPageWhenSizeMoreFrom() {
        Pagination pagination = new Pagination(1, 2);
        assertThat(pagination.getIndex()).isEqualTo(1);
        assertThat(pagination.getPageSize()).isEqualTo(1);
        assertThat(pagination.getAmountOfPages()).isEqualTo(3);
    }

    @Test
    public void shouldReturnPageWhenSizeEqualsFrom() {
        Pagination pagination = new Pagination(1, 1);
        assertThat(pagination.getIndex()).isEqualTo(1);
        assertThat(pagination.getPageSize()).isEqualTo(1);
        assertThat(pagination.getAmountOfPages()).isEqualTo(2);
    }

    @Test
    public void shouldReturnPageWhenSizeEqualsNotNullAndFromIsZero() {
        Pagination pagination = new Pagination(0, 3);
        assertThat(pagination.getIndex()).isEqualTo(0);
        assertThat(pagination.getPageSize()).isEqualTo(3);
        assertThat(pagination.getAmountOfPages()).isEqualTo(1);
    }

    @Test
    public void shouldReturnPageWhenFromEqualsZeroAndSizeNull() {
        Pagination pagination = new Pagination(0, null);
        assertThat(pagination.getIndex()).isEqualTo(0);
        assertThat(pagination.getPageSize()).isEqualTo(1000);
        assertThat(pagination.getAmountOfPages()).isEqualTo(0);
    }

}
