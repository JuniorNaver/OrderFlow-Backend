package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.config.PoProperties;
import com.youthcase.orderflow.pr.dto.SelectionBagDto;
import com.youthcase.orderflow.pr.dto.SelectionItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;


@Service @RequiredArgsConstructor
public class SelectionForwardService {
    private final WebClient poClient;
    private final PoProperties props;

    public void addItem(String ownerId, SelectionItemDto item) {
        var payload = new SelectionBagDto(ownerId, List.of(item));
        poClient.post().uri(props.importPathItems())
                .header("Idempotency-Key", ownerId + ":" + item.gtin() + ":" + item.orderDate())
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void checkout(String ownerId, List<SelectionItemDto> items) {
        if (items.isEmpty()) return;
        poClient.post().uri(props.importPathCheckout())
                .header("Idempotency-Key", ownerId + ":checkout")
                .bodyValue(new SelectionBagDto(ownerId, items))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}