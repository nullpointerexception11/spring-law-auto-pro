package com.lawauto.backend.client;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @GetMapping
    public List<ClientDto> listClients() {
        return List.of(
            new ClientDto(UUID.randomUUID(), "Ahmet Yılmaz", "ahmet@example.com", "0532 111 22 33", "İstanbul", "Davacı", 3, "Aktif"),
            new ClientDto(UUID.randomUUID(), "Ayşe Kaya", "ayse@example.com", "0533 444 55 66", "Ankara", "Davalı", 1, "Aktif"),
            new ClientDto(UUID.randomUUID(), "Mehmet Demir", "mehmet@example.com", "0535 777 88 99", "İzmir", "Davacı", 5, "Aktif"),
            new ClientDto(UUID.randomUUID(), "Zeynep Çelik", "zeynep@example.com", "0536 222 33 44", "Bursa", "Davacı", 0, "Pasif"),
            new ClientDto(UUID.randomUUID(), "Fatma Öztürk", "fatma@example.com", "0537 555 66 77", "Antalya", "Davalı", 2, "Aktif")
        );
    }

    public record ClientDto(UUID id, String name, String email, String phone, String city, String type, int activeCases, String status) {}
}
