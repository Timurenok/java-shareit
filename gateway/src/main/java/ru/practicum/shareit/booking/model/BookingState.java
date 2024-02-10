package ru.practicum.shareit.booking.model;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED, UNSUPPORTED_STATUS;

    public static BookingState mapToBookingState(String stringState) {
        for (BookingState state : values()) {
            if (state.toString().equalsIgnoreCase(stringState)) {
                return state;
            }
        }
        return UNSUPPORTED_STATUS;
    }
}
