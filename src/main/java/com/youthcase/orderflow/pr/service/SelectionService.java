package com.youthcase.orderflow.pr.service;


import com.youthcase.orderflow.pr.dto.SelectionItemDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SelectionService {
    private final Map<String, Map<String, SelectionItemDto>> store = new ConcurrentHashMap<>();

    public void add(String ownerId, SelectionItemDto i) {
        store.computeIfAbsent(ownerId, k -> new ConcurrentHashMap<>())
                .merge(i.gtin(), i, (o,n) ->
                        new SelectionItemDto(o.gtin(), Math.max(1,o.quantity())+Math.max(1,n.quantity()),
                                n.orderDate()!=null ? n.orderDate() : o.orderDate()));
    }
    public List<SelectionItemDto> list(String ownerId){
        return new ArrayList<>(store.getOrDefault(ownerId, Map.of()).values());
    }
    public void changeQty(String ownerId, String gtin, Long qty){
        var bag = store.getOrDefault(ownerId, Map.of());
        var cur = bag.get(gtin);
        if (cur == null) throw new NoSuchElementException("선택 없음: "+gtin);
        if (qty <= 0) bag.remove(gtin);
        else bag.put(gtin, new SelectionItemDto(gtin, qty, cur.orderDate()));
    }
    public void remove(String ownerId, String gtin){ var b=store.get(ownerId); if(b!=null) b.remove(gtin); }
    public void clear(String ownerId){ store.remove(ownerId); }
}