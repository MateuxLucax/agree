package models.server.channel;

import models.message.Message;
import models.server.Role;

import java.util.List;
import java.util.Map;

public class Channel {

    private String name;

    private String description;

    private List<Message> messages;
    // private Deque<Message> messages;

    private Map<Role, Permission> permissions;
    //            #main   #videos  #announcements  #private
    // Admin      DELETE  DELETE   DELETE          DELETE
    // Moderator  DELETE  DELETE   WRITE           READ
    // Guy        WRITE   WRITE    READ            NONE

    // so, inside the Channel main,
    // Map permissions = { (admin, DELETE), (moderator, DELETE), (guy, WRITE) }

}
