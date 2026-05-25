package com.SpringBoot.HotelService.Hotel_service.controller;

import com.SpringBoot.HotelService.Hotel_service.Service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/hotels")
@RequiredArgsConstructor
public class HotelInternalController {
    private  final HotelService hotelService;

    @GetMapping("/{id}/exists")
    public ResponseEntity<Void> hotelExist(@PathVariable Long id)
    {
        hotelService.getHotelById(id);
        return ResponseEntity.ok().build();   // on Success : 200 & on Fail : 404
    }
}
