import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;

public class discordBot extends ListenerAdapter {
    public static final int MAX_WARNS = 3;
    public static final String helpMessage = "Вот список моих команд:\n!help - выводит список команд\nadd banned word example - добавляет слово example в список запрещенных. За употребление запрещенных слов я выдаю пользователям предупреждения.Три предупреждения и бан на 1 день(Требуется разрешение на бан участников).\ndelete banned word example - удаляет слово example из списка запрещенных.\nget banned words - выводит все запрещенные слова.\n!request url - позволяет сделать запрос к сайту по адресу url.\n!mywarns - выводит количество ваших предупреждений.\nadd ignored chat example - добавляет чат с id example в список игнорируемых.\ndelete ignored chat example - удаляет чат с id example из списка игнорируемых.\nget ignored chats - выводит id всех игнорируемых чатов";
    private static final String DB_URL = "jdbc:h2:~/test";
    public static void main(String[] args){
        try{
            Class.forName("org.h2.Driver").getDeclaredConstructor().newInstance();
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        String token = "NjIxMDQzMzU1NTg5MDE3NjMx.XXf8zw.zYsWN0iOxJLQoqQQmLmTHYAuLpc";
        builder.addEventListener(new discordBot());
        builder.setToken(token);
        try{
            builder.buildAsync();
        }catch (Exception ex){
            System.out.print(ex.getMessage());
        }

        /*
         * Обнуление предупреждений юзера каждые 24 часа
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true) {
                        Thread.sleep(86400 * 1000);
                        ResultSet set1;
                        set1 = executeQuery("SELECT SERV_ID, WARNS, NAME FROM USERS");
                        while (set1.next()) {
                            if(set1.getInt("WARNS") > 0){
                                executeUpdate("UPDATE USERS SET WARNS=" + (set1.getInt("WARNS") - 1) + " WHERE NAME='" + set1.getString("NAME") + "' AND SERV_ID='" + set1.getString("SERV_ID") + "'");
                            }
                        }
                    }
                }catch (Exception ex){
                    System.err.println(ex.getMessage());
                }
            }
        }).start();
    }
    //h2 database jdbc url: jdbc:h2:~/test
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        ResultSet set = executeQuery("SELECT WORD FROM BANNED_WORDS WHERE SERV_ID='" + event.getGuild().getId() +"'");
        if(event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE) & event.getChannel().canTalk() & !event.getMessage().getContentRaw().toLowerCase().equals(helpMessage.toLowerCase())){
            Treater.treat(event);
            /*
             * Обработка сообщений
             */
            if(event.getMessage().getContentRaw().toLowerCase().contains("add banned word ") & event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                String[] arr = event.getMessage().getContentRaw().toLowerCase().split(" ");
                ArrayList<String> list = new ArrayList<>();
                try{
                    while(set.next()){
                        list.add(set.getString("WORD"));
                    }
                }catch (Exception ex){
                    System.err.println(ex.getMessage());
                }
                if(!list.contains(arr[3])){
                    //добавление слова в список запрещенных
                    executeUpdate("INSERT INTO BANNED_WORDS(SERV_ID, WORD) VALUES('" + event.getGuild().getId() +"', '" + arr[3].toLowerCase() + "')");
                    event.getChannel().sendMessage("Слово добавлено: " + arr[3].toLowerCase() + "!").queue();
                }else{
                    event.getChannel().sendMessage("Такое слово уже есть в списке запрещенных").queue();
                }
            }else if(event.getMessage().getContentRaw().toLowerCase().equals("!help")){
                event.getChannel().sendMessage(helpMessage).queue();
            }else if(event.getMessage().getContentRaw().toLowerCase().contains("delete banned word ") & !event.getMessage().getContentRaw().toLowerCase().equals(helpMessage.toLowerCase()) & event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                String[] arr = event.getMessage().getContentRaw().toLowerCase().split(" ");
                ArrayList<String> list = new ArrayList<>();
                try{
                    while(set.next()){
                        list.add(set.getString("WORD"));
                    }
                }catch (Exception ex){
                    System.err.println(ex.getMessage());
                }
                if(list.contains(arr[3])){
                    //удаление слова из списка запрещенных
                    executeUpdate("DELETE FROM BANNED_WORDS WHERE SERV_ID='" + event.getGuild().getId() + "'AND WORD='" + arr[3] + "'");
                    event.getChannel().sendMessage("Слово " + arr[3] + " удалено из списка запрещенных").queue();
                }else {
                    event.getChannel().sendMessage("Слова " + arr[3] + " еще нет в списке запрещенных").queue();
                }
            }else if(event.getMessage().getContentRaw().toLowerCase().equals("get banned words")){
                //вывод всех запрещенных слов
                event.getChannel().sendMessage("Вот весь список запрещенных слов:").queue();
                String text = "";
                try{
                    while(set.next()){
                        text += set.getString("WORD") + "\n";
                    }
                    event.getChannel().sendMessage(text).queue();
                }catch (Exception ex){
                    System.err.println(ex.getMessage());
                }
                if(text.isEmpty()){
                    event.getChannel().sendMessage("Список запрещенных слов пуст").queue();
                }else{
                    event.getChannel().sendMessage(text);
                }
            }else if(event.getMessage().getContentRaw().toLowerCase().contains("!request ") & !event.getMessage().getContentRaw().toLowerCase().equals(helpMessage.toLowerCase())){
                String[] arr = event.getMessage().getContentRaw().split(" ");
                try{
                    URL url = new URL(arr[1]);
                    //запрашиваем url
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream is;
                    connection.connect();
                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        is = connection.getInputStream();
                    }else{
                        is = connection.getErrorStream();
                    }
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder builder = new StringBuilder(2000);
                    builder.append(connection.getResponseMessage() + "\n");
                    //формируем вывод
                    while(reader.readLine() != null){
                        builder.append(reader.readLine() + "\n");
                    }
                    if(builder.toString().length() < 2000){
                        event.getChannel().sendMessage(builder.toString()).queue();
                    }else{
                        event.getChannel().sendMessage(builder.toString().substring(0,1999)).queue();
                    }
                }catch (Exception ex){
                    event.getChannel().sendMessage("Был запрошен некорректный url").queue();
                    System.out.println(ex.getMessage());
                }
            }else if(event.getMessage().getContentRaw().toLowerCase().equals("!mywarns")){
                //вывод количества предупреждений
                ResultSet set1 = executeQuery("SELECT WARNS FROM USERS WHERE SERV_ID='" + event.getGuild().getId() + "' AND NAME='" + event.getMember().getUser().getId() + "'");
                String text = "Количество предупреждений " + event.getMember().getEffectiveName() + ": ";
                try{
                    if (set1.next()){
                        int i =  set1.getInt("WARNS");
                        text += i;
                    }
                }catch (Exception ex){
                    System.err.println(ex.getMessage());
                }
                event.getChannel().sendMessage(text).queue();
            }else if(event.getMessage().getContentRaw().toLowerCase().contains("add ignored chat ") & event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                String[] arr = event.getMessage().getContentRaw().toLowerCase().split(" ");
                ResultSet set1 = executeQuery("SELECT CHAT_ID FROM IGNORED_CHATS WHERE SERV_ID='" + event.getGuild().getId() + "'");
                ArrayList<String> list = new ArrayList<>();
                try{
                    while(set1.next()){
                        list.add(set1.getString("CHAT_ID"));
                    }
                }catch (Exception ex){
                    System.err.println(ex.getMessage());
                }
                try{
                    if(!list.contains(arr[3])){
                        if(!event.getGuild().getTextChannelById(arr[3]).equals(null)){
                            //добавление чата в список игнорируемых(P.S. игнорируемые чату не модерируются)
                            executeUpdate("INSERT INTO IGNORED_CHATS(SERV_ID, CHAT_ID) VALUES('" + event.getGuild().getId() + "','" + arr[3] + "')");
                            event.getChannel().sendMessage("Чат с id: " + arr[3] + " теперь не модерируется").queue();
                        }
                    }else {
                        event.getChannel().sendMessage("Чат с id: " + arr[3] + " уже есть в списке игнорируемых").queue();
                    }
                }catch (NumberFormatException | NullPointerException ex){
                    event.getChannel().sendMessage("Чата с id: " + arr[3] + " не существует").queue();
                }

            }else if(event.getMessage().getContentRaw().toLowerCase().contains("delete ignored chat ") & event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                String[] arr = event.getMessage().getContentRaw().toLowerCase().split(" ");
                ResultSet set1 = executeQuery("SELECT CHAT_ID FROM IGNORED_CHATS WHERE SERV_ID='" + event.getGuild().getId() + "'");
                ArrayList<String> list = new ArrayList<>();
                try{
                    while(set1.next()){
                        list.add(set1.getString("CHAT_ID"));
                    }
                }catch (Exception ex){
                    System.err.println(ex.getMessage());
                }
                try{
                    if(list.contains(arr[3])){
                        if(!event.getGuild().getTextChannelById(arr[3]).equals(null)){
                            //удаление чата из списка игнорируемых
                            executeUpdate("DELETE FROM IGNORED_CHATS WHERE CHAT_ID='" + arr[3] + "'");
                            event.getChannel().sendMessage("Чат с id: " + arr[3] + " удален из списка игнорируемых").queue();
                        }
                    }else{
                        event.getChannel().sendMessage("Чата с id: " + arr[3] + " не было в списке игнорируемых").queue();
                    }
                }catch (NumberFormatException | NullPointerException ex) {
                    event.getChannel().sendMessage("Чата с id: " + arr[3] + " не существует").queue();
                }
            }else if(event.getMessage().getContentRaw().toLowerCase().contains("get ignored chats")){
                //получаем список игнорируемых чатов
                ResultSet set1 = executeQuery("SELECT CHAT_ID FROM IGNORED_CHATS WHERE SERV_ID='" + event.getGuild().getId() + "'");
                String text = "Вот список всех игнорируемых чатов:";
                String text1 = "";
                try{
                    while(set1.next()){
                        event.getGuild().getTextChannelById(set1.getString("CHAT_ID"));
                        text1 += "\n" + set1.getString("CHAT_ID");
                    }
                }catch (Exception ex){
                    System.err.println(ex.getMessage());
                }
                if(text1.isEmpty()){
                    event.getChannel().sendMessage("\nСписок игнорируемых чатов пуст").queue();
                }else{
                    event.getChannel().sendMessage(text + text1).queue();
                }
            }
        }

        if (event.getAuthor().isBot()){
            return;
        }

        try{
            while(set.next()){
                ResultSet set1 = executeQuery("SELECT WARNS FROM USERS WHERE SERV_ID='" + event.getGuild().getId() + "' AND NAME='" + event.getMember().getUser().getId() + "'");
                int warns = 0;
                if(set1.next()){
                    warns = set1.getInt("WARNS");
                }
                ResultSet set2 = executeQuery("SELECT CHAT_ID FROM IGNORED_CHATS WHERE SERV_ID='" + event.getGuild().getId() + "'");
                ArrayList<String> list = new ArrayList<>();
                while(set2.next()){
                    list.add(set2.getString("CHAT_ID"));
                }
                if(event.getMessage().getContentRaw().toLowerCase().contains(set.getString("WORD").toLowerCase()) & !event.getMessage().getContentRaw().toLowerCase().contains("add banned word ") & !event.getMessage().getContentRaw().toLowerCase().contains("delete banned word ") & !event.getMessage().getContentRaw().toLowerCase().equals(helpMessage.toLowerCase()) & !event.getMessage().getContentRaw().contains("!request ") & !list.contains(event.getChannel().getId()) & !event.getMessage().getContentRaw().toLowerCase().contains("add ignored chat ") & !event.getMessage().getContentRaw().toLowerCase().contains("delete ignored chat ") & !event.getMessage().getContentRaw().toLowerCase().contains("get ignored chats") & !event.getMessage().getContentRaw().toLowerCase().contains("set srach chat ") & !event.getMessage().getContentRaw().toLowerCase().contains("delete srach chat ") & !event.getMessage().getContentRaw().toLowerCase().contains("get srach chat") & event.getChannel().canTalk() & event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE) & !event.getMessage().getContentRaw().toLowerCase().equals(Treater.greet_message())){
                    //предупреждаем юзера сообщением
                    event.getChannel().sendMessage(event.getMember().getEffectiveName() + ", следите за своим поведением!").queue();
                    if(event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS) & !event.getMember().isOwner() & event.getGuild().getSelfMember().canInteract(event.getMember())){
                        if(warns < 2){
                            //увеличиваем количество предупреждений юзера
                            warns++;
                            //обновляем запись в базе данных
                            executeUpdate("UPDATE USERS SET WARNS=" + warns + " WHERE SERV_ID='" + event.getGuild().getId() + "' AND NAME='" + event.getMember().getUser().getId() + "'");
                            if(event.getChannel().canTalk() & event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE)) {
                                event.getChannel().sendMessage(event.getMember().getEffectiveName() + ", вас предупредили " + warns + " раз(а). Еще " + (MAX_WARNS - warns) + " раз(а) и вам бан!").queue();
                            }
                            break;
                        }else{
                            //баним юзера
                            event.getGuild().getController().ban(event.getMember(), 1).queue();
                            if(event.getChannel().canTalk() & event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE)){
                                event.getChannel().sendMessage("У участника " + event.getMember().getEffectiveName() + " накопилось максимум предупреждений. Он вернется к нам завтра").queue();
                            }
                            executeUpdate("UPDATE USERS SET WARNS=0 WHERE SERV_ID='" + event.getGuild().getId() + "' AND NAME='" + event.getMember().getUser().getId() + "'");
                            break;
                        }
                    }
                }
            }
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) { // auth link: https://discordapp.com/oauth2/authorize?&client_id=602940808261271572&scope=bot&permissions=0
        super.onGuildJoin(event);
        if(event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE) & event.getGuild().getDefaultChannel().canTalk()){
            event.getGuild().getDefaultChannel().sendMessage("Всем привет, я бот Аркадий. Я бот-модератор. Узнать список моих команд можно с помощью команды: !help.").queue();
        }
        ArrayList<String> list = new ArrayList<>();
        ResultSet set = executeQuery("SELECT NAME FROM USERS WHERE SERV_ID='" + event.getGuild().getId() + "'");
        try{
            while(set.next()){
                list.add(set.getString("NAME"));
            }
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
        for(Member member : event.getGuild().getMembers()){
            if(!list.contains(member.getUser().getId()) & !member.getEffectiveName().equals("Аркадий") & !member.getUser().isBot()){
                //вносим данные всех юзеров в базу данных
                executeUpdate("INSERT INTO USERS(SERV_ID, WARNS, POINTS, NAME) VALUES('" + event.getGuild().getId() + "', 0, 10, '" + member.getUser().getId() + "')");
                if(event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE) & event.getGuild().getDefaultChannel().canTalk()){
                    event.getGuild().getDefaultChannel().sendMessage(member.getEffectiveName() + " был добавлен в базу данных.").queue();
                }
            }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        super.onGuildMemberJoin(event);
        ArrayList<String> list = new ArrayList<>();
        ResultSet set = executeQuery("SELECT NAME FROM USERS WHERE SERV_ID='" + event.getGuild().getId() + "'");
        try{
            while(set.next()){
                list.add(set.getString("NAME"));
            }
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
        for(Member member : event.getGuild().getMembers()){
            if(!list.contains(member.getUser().getId()) & !member.getEffectiveName().equals("Аркадий") & !member.getUser().isBot()){
                //вносим данные всех юзеров в базу данных
                executeUpdate("INSERT INTO USERS(SERV_ID, WARNS, POINTS, NAME) VALUES('" + event.getGuild().getId() + "', 0, 10, '" + member.getUser().getId() + "')");
                if(event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE) & event.getGuild().getDefaultChannel().canTalk()){
                    event.getGuild().getDefaultChannel().sendMessage(member.getEffectiveName() + " был добавлен в базу данных.").queue();
                }
            }
        }
    }

    //соединение с бд
    public static Connection getConnection(String url, String user, String password){
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(url,user,password);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return conn;
    }

    //создаем statement
    public static Statement createStatement(){
        Statement st = null;
        try{
            st = getConnection(DB_URL, "root", "").createStatement();
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
        return st;
    }

    //метод для выполнений запросов которые возвращают массивы данных
    public static ResultSet executeQuery( String query){
        ResultSet set = null;
        Statement st = createStatement();
        try{
            set = st.executeQuery(query);
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
        return set;
    }

    //метод для выполнения запросов которые возвращают количество затронутых строк
    public static int executeUpdate(String query) {
        int i = 0;
        Statement st = createStatement();
        try{
            i = st.executeUpdate(query);
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
        return i;
    }
}