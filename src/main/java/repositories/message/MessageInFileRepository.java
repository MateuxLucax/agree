package repositories.message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import models.group.Group;
import models.message.Message;
import utils.JsonDatabaseUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class MessageInFileRepository implements IMessageRepository {

    private final Gson gson = new Gson();
    private final Type groupMessagesType = new TypeToken<List<Map<Group, Message>>>() {}.getType();
    private final File groupMessageFile;
    private final List<Map<Group, Message>> groupMessages = new ArrayList<>();

    public MessageInFileRepository() {
        groupMessageFile = JsonDatabaseUtil.getFile("groupMessage.json");
        getGroupMessages();
    }

    @Override
    public boolean addMessage(Group group, Message message) {
        try (var fileWriter = new FileWriter(groupMessageFile)) {
            Map<Group, Message> messageMap = new HashMap<>();
            messageMap.put(group, message);
            groupMessages.add(messageMap);
            fileWriter.write(this.gson.toJson(groupMessages));
            this.getGroupMessages();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    @Override
    public boolean getMessagesBefore(Group group, Date date) {
        return false;
    }

    @Override
    public boolean getMessagesAfter(Group group, Date date) {
        return false;
    }

    @Override
    public boolean searchMessages(Group group, String search) {
        return false;
    }

    @Override
    public boolean getMostRecentMessages(Group group) {
        return false;
    }

    @Override
    public boolean removeMessage(Group group, Message message) {
        return false;
    }

    private void getGroupMessages() {
        try (var jsonReader = new JsonReader(new FileReader(groupMessageFile))) {
            List<Map<Group, Message>> groupMessagesInFile = this.gson.fromJson(jsonReader, this.groupMessagesType);
            if (groupMessagesInFile != null) {
                groupMessages.addAll(groupMessagesInFile);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
