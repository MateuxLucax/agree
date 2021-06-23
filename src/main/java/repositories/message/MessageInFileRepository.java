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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MessageInFileRepository implements IMessageRepository {

    private final Gson gson = new Gson();
    private final File groupMessageFile;
    private final List<GroupMessage> groupMessages = new ArrayList<>();

    public MessageInFileRepository() {
        groupMessageFile = JsonDatabaseUtil.getFile("groupMessages.json");
        getGroupMessages();
    }

    @Override
    public boolean addMessage(Group group, Message message) {
        try (var fileWriter = new FileWriter(groupMessageFile)) {
            groupMessages.add(new GroupMessage(group, message));
            fileWriter.write(this.gson.toJson(groupMessages));
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
        // For now not really the "most recent", just all of them (for testing)
        for (var gm : groupMessages) {
            if (gm.getGroup().equals(group))
                group.loadMessageBelow(gm.getMessage());
        }
        return true;
    }

    @Override
    public boolean removeMessage(Group group, Message message) {
        return false;
    }

    private void getGroupMessages() {
        try (var jsonReader = new JsonReader(new FileReader(groupMessageFile))) {
            List<GroupMessage> groupMessagesInFile = this.gson.fromJson(jsonReader, new TypeToken<List<GroupMessage>>() {}.getType());
            if (groupMessagesInFile != null) {
                groupMessages.addAll(groupMessagesInFile);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static class GroupMessage {
        private Group group;
        private Message message;

        public GroupMessage(Group group, Message message) {
            this.group = group;
            this.message = message;
        }

        public Group getGroup() {
            return group;
        }

        public void setGroup(Group group) {
            this.group = group;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupMessage that = (GroupMessage) o;
            return Objects.equals(group, that.group) && Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(group, message);
        }
    }
}
