package tasks;

import db.UserDbHelper;
import entities.UserEntry;

import java.util.List;

public class WarningsDecreaseTask implements Runnable {
    @Override
    public void run() {
        UserDbHelper helper = UserDbHelper.getInstance();
        List<UserEntry> users = helper.getUsers();

        users.stream()
                .filter(u -> u.getWarnings() > 0)
                .iterator()
                .forEachRemaining(u ->
                        helper.updateWarnings(u.getServerId(), u.getUserId(), u.getWarnings() - 1));
    }
}
