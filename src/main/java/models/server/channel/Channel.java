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

    private Map<Role, Permissions> permissions;
    //            #main   #videos  #private
    // Admin      DELETE  DELETE   DELETE
    // Moderator  DELETE  DELETE   READ
    // Guy        READ    READ     NONE
    //

}
