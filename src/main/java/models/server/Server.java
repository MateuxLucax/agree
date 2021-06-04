package models.server;

import models.server.channel.Channel;
import models.server.channel.Member;

import java.util.List;

public class Server {

    private Member admin;

    private List<Member> members;

    private List<Channel> channels;

    private List<Role> roles;

}
