import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Bot extends TelegramLongPollingBot {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        }catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }



    public void sendMsg(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public void unMuteUser(Message message, int id){
        RestrictChatMember restrictChatMember = new RestrictChatMember();
        restrictChatMember.setChatId(message.getChatId());
        restrictChatMember.setUserId(id);
        restrictChatMember.setCanSendMessages(true);
        restrictChatMember.setCanSendMediaMessages(true);
        restrictChatMember.setCanSendOtherMessages(true);
        sendMsg(message,"Вроде получилось");
        try {
            execute(restrictChatMember);
        }catch (TelegramApiException e){
            e.printStackTrace();
            sendMsg(message," ох блять, эксепшон вывалился");

        }
    }

    public void muteUser(Message message, int id){
        RestrictChatMember restrictChatMember = new RestrictChatMember();
        restrictChatMember.setChatId(message.getChatId());
        restrictChatMember.setUserId(id);
        restrictChatMember.setCanSendMessages(false);
        restrictChatMember.setCanSendMediaMessages(false);
        restrictChatMember.setCanSendOtherMessages(false);
        restrictChatMember.setUntilDate((int) ((System.currentTimeMillis() / 1000L) + 900));
        try {
            execute(restrictChatMember);
        }catch (TelegramApiException e){
            e.printStackTrace();
            sendMsg(message," ох блять, эксепшон вывалился");

        }
    }



    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();


        if(message != null && message.hasText()){
            String text = message.getText();
            System.out.println(message);

            if (counter !=0 && System.currentTimeMillis() - startTime > 600000){
                counter = 0;
                muteActivators[0] = 0;
                muteActivators[1] = 0;
                muteActivators[2] = 0;
                muteActivators[3] = 0;
                muteActivators[4] = 0;
                idOfMutedUser = 0;
                nameOfMutedUser = "";
                sendMsg(message, "Сбросил счётчик");
            }

            if (text.contains("/")){
                if(lastUserArr(message.getFrom())){
                    sendMsg(message,"Кто-то наспамил много команд, пока что дебаг, но в будущем бан нахуй");
                    muteUser(message, message.getFrom().getId());
                }
            }
            if (text.contains("/unmute") && message.isReply() && message.getFrom().getId().equals(138368804)){
                sendMsg(message, "пробую разбанить");
                unMuteUser(message, message.getReplyToMessage().getFrom().getId());

            }
            if (text.contains("/ultimatemute") && message.isReply() && message.getFrom().getId().equals(138368804)){
                sendMsg(message, "пизда воробушку");
                muteUser(message, message.getReplyToMessage().getFrom().getId());

            }
            if (text.contains("Вася") || text.contains("вася")) {
               // sendMsg(message, "Вася, ну йоханый манус");
            } else if (text.contains("Манус") || text.contains("манус")) {
                sendMsg(message, "Вася, ну йоханый манус");
            }else if ("/manus".equalsIgnoreCase(text) || "/manus@yxrix_bot".equalsIgnoreCase(text)){
                sendMsg(message, "Вася, ну йоханый манус");
            }else if ("/manus_generator".equalsIgnoreCase(text) || "/manus_generator@yxrix_bot".equalsIgnoreCase(text)){
                sendMsg(message, randomManusGenerator());
            }else if ("/manus_kanon".equalsIgnoreCase(text) || "/manus_kanon@yxrix_bot".equalsIgnoreCase(text)){
                sendMsg(message, randomManusKanon());
            }
            if(("/voteban".equalsIgnoreCase(text) || "/voteban@yxrix_bot".equalsIgnoreCase(text)) && message.isReply()){
                if (counter == 0) {
                    sendMsg(message, "Инициализация мута на 15 минут для " + message.getReplyToMessage().getFrom().getFirstName() + "\n @" + message.getReplyToMessage().getFrom().getUserName() + " Если молодые наберут 5/5 то ты получишь мут на 15 минут");
                    nameOfMutedUser = (message.getReplyToMessage().getFrom().getFirstName() + " @" + message.getReplyToMessage().getFrom().getUserName());
                    idOfMutedUser = message.getReplyToMessage().getFrom().getId();
                    muteActivators[counter] = message.getFrom().getId();
                    counter++;
                    startTime = System.currentTimeMillis();
                }else {
                    if (isVoted(message.getFrom().getId())){
                        sendMsg(message,"Ты уже голосовал");
                    }else {
                        muteActivators[counter] = message.getFrom().getId();
                        counter++;
                        sendMsg(message,"Засчитал. " + counter + "/5");
                    }

                }
                if (counter == 5){
                    sendMsg(message,nameOfMutedUser + " Отправляется в мут на 15 минут.");
                    muteUser(message, idOfMutedUser);
                    counter = 0;
                    muteActivators[0] = 0;
                    muteActivators[1] = 0;
                    muteActivators[2] = 0;
                    muteActivators[3] = 0;
                    muteActivators[4] = 0;
                }
            }else if ("/voteban".equalsIgnoreCase(text) || "/voteban@yxrix_bot".equalsIgnoreCase(text)){
                if (counter == 0) {
                    sendMsg(message, "Ответь этой командой на сообщение того, кого хочешь забанить нахуй");
                }else {
                    if (isVoted(message.getFrom().getId())){
                        sendMsg(message,"Ты уже голосовал");
                    }else {
                        muteActivators[counter] = message.getFrom().getId();
                        counter++;
                        sendMsg(message,"Засчитал. " + counter + "/5");
                    }

                }
                if (counter == 5){
                    sendMsg(message,nameOfMutedUser + " Отправляется в мут на 15 минут.");
                    counter = 0;
                    muteActivators[0] = 0;
                    muteActivators[1] = 0;
                    muteActivators[2] = 0;
                    muteActivators[3] = 0;
                    muteActivators[4] = 0;
                    muteUser(message, idOfMutedUser);
                }
            }
            if("/yesno".equalsIgnoreCase(text) || "/yesno@yxrix_bot".equalsIgnoreCase(text)){
                sendMsg(message,yesNo());
            }

            //else if(message.getFrom().getId().equals(407449415)){
              // sendMsg(message, "Ладно не будем спорить на счет российского языка");
            // }
        }

    }

    public String yesNo (){
        Random random = new Random();
        if (random.nextInt(1) == 0){
            return "Да.";
        } else {
            return "Нет.";
        }
    }

    public boolean isVoted (int id){
        boolean result = false;
        for (int i = 0; i < muteActivators.length;i++){
            if (muteActivators[i] == id){
                result = true;
            }
        }
        return result;
    }
    int lastUserArray[] = new int[5];
    public boolean lastUserArr(User user){
        lastUserArray[0]=lastUserArray[1];
        lastUserArray[1]=lastUserArray[2];
        lastUserArray[2]=lastUserArray[3];
        lastUserArray[3]=lastUserArray[4];
        lastUserArray[4]=user.getId();


        if(lastUserArray[1] == user.getId() && lastUserArray[2] == user.getId() && lastUserArray[3] == user.getId() && lastUserArray[4] == user.getId()){
            return true;
        } else return false;
    }
    int counter = 0;
    int muteActivators[] = new int[5];
    String nameOfMutedUser;
    int idOfMutedUser;
    long startTime;

    public String randomManusGenerator(){

        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 4; i++){
            switch (random.nextInt(4)){
                case 0: sb.append("Вася");
                    break;
                case 1: sb.append("ну");
                    break;
                case 2: sb.append("йоханый");
                    break;
                case 3: sb.append("Манус");
                    break;
                default: System.err.println("Error at rMG");
                    break;
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    public String randomManusKanon(){
        Random random = new Random();
        switch (random.nextInt(24)){
            case 0:
                return "Вася манус йоханий ну";
            case 1:
                return "Вася манус ну йоханий";
            case 2:
                return "Вася йоханий ну манус";
            case 3:
                return "Вася йоханий манус ну";
            case 4:
                return "Вася ну манус йоханий";
            case 5:
                return "Вася ну йоханий манус";
            case 6:
                return "Ну Вася йоханий манус";
            case 7:
                return "Ну Вася манус йоханий";
            case 8:
                return "Ну йоханий Вася манус";
            case 9:
                return "Ну йоханий манус Вася";
            case 10:
                return "Ну манус йоханий Вася";
            case 11:
                return "Ну манус Вася йоханий";
            case 12:
                return "Йоханий ну Вася манус";
            case 13:
                return "Йоханий ну манус Вася";
            case 14:
                return "Йоханий Вася ну манус";
            case 15:
                return "Йоханий Вася манус ну";
            case 16:
                return "Йоханий манус ну Вася";
            case 17:
                return "Йоханий манус Вася ну";
            case 18:
                return "Манус Вася ну йоханий";
            case 19:
                return "Манус Вася йоханий ну";
            case 20:
                return "Манус ну йоханий Вася";
            case 21:
                return "Манус ну Вася йоханий";
            case 22:
                return "Манус йоханий ну Вася";
            case 23:
                return "Манус йоханий Вася ну";
            default:
                return "Вася ну йоханый Манус";
        }

    }
    config config = new config();
    public String getBotUsername() {

        return config.getUsername();
    }

    public String getBotToken() {
        return config.getKey();
    }
}
