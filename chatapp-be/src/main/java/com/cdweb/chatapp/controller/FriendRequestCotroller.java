package com.cdweb.chatapp.controller;

import com.cdweb.chatapp.dto.AddFriendReqDto;
import com.cdweb.chatapp.dto.IdDto;
import com.cdweb.chatapp.model.AddFriendRequest;
import com.cdweb.chatapp.model.Room;
import com.cdweb.chatapp.model.User;
import com.cdweb.chatapp.service.AddFriendRequestService;
import com.cdweb.chatapp.service.JwtService;
import com.cdweb.chatapp.service.RoomService;
import com.cdweb.chatapp.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/chatapp.api")
public class FriendRequestCotroller {
    @Autowired
    private UserService userService;
    @Autowired
    private AddFriendRequestService friendRequestService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoomService roomService;

    @PostMapping("/addFriend")
    public void addFriendRequest(@RequestHeader("Authorization") String bearerToken, @RequestBody AddFriendReqDto addFriendReqDto) {

        String username = jwtService.extractUsername(bearerToken.substring(7));

        User sender = userService.findByEmail(username).get();
        User receiver = userService.findByEmail(addFriendReqDto.getReceiver()).get();

        AddFriendRequest addFriendRequest = new AddFriendRequest();
        addFriendRequest.setSender(sender);
        addFriendRequest.setReceiver(receiver);
        addFriendRequest.setSendAt(LocalDateTime.now());

        friendRequestService.sendRequest(addFriendRequest);
    }
    @PostMapping("/acceptFriend")
    public void acceptAddFriendRequest(@RequestBody IdDto idDto){
        System.out.println("haha");
        System.out.println(idDto.getId());
        AddFriendRequest friendRequest = friendRequestService.findById(idDto.getId());

        Room newRoom = new Room();
        newRoom.addMember(friendRequest.getSender());
        newRoom.addMember(friendRequest.getReceiver());
        newRoom.setCreateAt(LocalDateTime.now());
        newRoom.setUpdateAt(LocalDateTime.now());

        roomService.createNewRoom(newRoom);
        friendRequestService.deleteRequest(idDto.getId());
    }

    @DeleteMapping("/addFriendRequests/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        friendRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}
