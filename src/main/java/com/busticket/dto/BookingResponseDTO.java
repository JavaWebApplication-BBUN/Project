package com.busticket.dto;

import java.util.List;

public class BookingResponseDTO {
    private List<TicketResponseDTO> tickets;
    private String checkoutUrl;

    public BookingResponseDTO() {
    }

    public BookingResponseDTO(List<TicketResponseDTO> tickets, String checkoutUrl) {
        this.tickets = tickets;
        this.checkoutUrl = checkoutUrl;
    }

    public List<TicketResponseDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketResponseDTO> tickets) {
        this.tickets = tickets;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }
}
