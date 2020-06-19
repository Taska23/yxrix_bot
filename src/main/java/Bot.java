import org.telegram.telegrambots.ApiContextInitializer;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.stickers.GetStickerSet;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.stickers.StickerSet;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;
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

    public void sendSticker(Message message, String sticker){
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(message.getChatId());
        sendSticker.setSticker(sticker);

        try {
            execute(sendSticker);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public void delMsg (Message message){
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(message.getChatId()));
        deleteMessage.setMessageId(message.getMessageId());
        try {
            execute(deleteMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
            sendMsg(message," ох блять, эксепшон вывалился");
        }
    }
    public void delMsg (Long chatId, Object messageId){
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId((Integer) messageId);
        try {
            execute(deleteMessage);
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

        if (update.hasMessage()) {

            Message message = update.getMessage();


            if (message != null && message.hasText()) {
                String text = message.getText();
                System.out.println(message);

                if (counter != 0 && System.currentTimeMillis() - startTime > 600000) {
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
                if (text.equalsIgnoreCase("/f") || text.equalsIgnoreCase("/f@yxrix_bot") || text.equalsIgnoreCase("F") || text.equalsIgnoreCase("ф")) {
                    pressFSticker(message);
                    spamMesages.add(message.getMessageId());
                }
                if (text.contains("Ставр") || text.contains("ставр") || text.contains("Кентавр") || text.contains("кентавр") || text.contains("Ставровский") || text.contains("ставровский")) {
                    sendMsg(message, randomStavrSay());
                    spamMesages.add(message.getMessageId());
                }
                if (text.contains("Терещь") || text.contains("терещь") || text.contains("Терещ") || text.contains("терещ") || text.contains("Терещенко") || text.contains("терещенко")) {
                    sendMsg(message, randomTerehaSay());
                    spamMesages.add(message.getMessageId());
                }

                if (text.contains("/") && !(text.contains("http"))) {
                    if (lastUserArr(message.getFrom())) {
                        sendMsg(message, (message.getFrom().getFirstName() + " наспамил много комманд, получает мутец на 15 минут"));
                        muteUser(message, message.getFrom().getId());
                    }
                    spamMesages.add(message.getMessageId());
                }
                if (text.contains("/clear") || text.contains("/clear@yxrix_bot") || spamMesages.size() > 200) {
                    sendMsg(message, ("Будет очищенно " + spamMesages.size() * 2 + " сообщений"));
                    spamDelete(message.getChatId(), spamMesages);

                    spamMesages.clear();
                }
                if (text.contains("/del") && message.getFrom().getId().equals(138368804)) {
                    delMsg(message.getReplyToMessage());
                    delMsg(message);
                }
                if (text.contains("/unmute") && message.isReply() && message.getFrom().getId().equals(138368804)) {
                    sendMsg(message, "пробую разбанить");
                    unMuteUser(message, message.getReplyToMessage().getFrom().getId());

                }
                if (text.contains("/ultimatemute") && message.isReply() && message.getFrom().getId().equals(138368804)) {
                    sendMsg(message, "пизда воробушку");
                    muteUser(message, message.getReplyToMessage().getFrom().getId());

                }
                if (text.contains("Вася") || text.contains("вася")) {
                    // sendMsg(message, "Вася, ну йоханый манус");
                } else if (text.contains("Манус") || text.contains("манус")) {
                    sendMsg(message, "Вася, ну йоханый манус");
                } else if ("/manus".equalsIgnoreCase(text) || "/manus@yxrix_bot".equalsIgnoreCase(text)) {
                    sendMsg(message, "Вася, ну йоханый манус");
                } else if ("/manus_generator".equalsIgnoreCase(text) || "/manus_generator@yxrix_bot".equalsIgnoreCase(text)) {
                    sendMsg(message, randomManusGenerator());
                } else if ("/manus_kanon".equalsIgnoreCase(text) || "/manus_kanon@yxrix_bot".equalsIgnoreCase(text)) {
                    sendMsg(message, randomManusKanon());
                }
                if (("/voteban".equalsIgnoreCase(text) || "/voteban@yxrix_bot".equalsIgnoreCase(text)) && message.isReply()) {
                    if (counter == 0) {
                        sendMsg(message, "Инициализация мута на 15 минут для " + message.getReplyToMessage().getFrom().getFirstName() + "\n @" + message.getReplyToMessage().getFrom().getUserName() + " Если молодые наберут 5/5 то ты получишь мут на 15 минут");
                        nameOfMutedUser = (message.getReplyToMessage().getFrom().getFirstName() + " @" + message.getReplyToMessage().getFrom().getUserName());
                        idOfMutedUser = message.getReplyToMessage().getFrom().getId();
                        muteActivators[counter] = message.getFrom().getId();
                        counter++;
                        startTime = System.currentTimeMillis();
                    } else {
                        if (isVoted(message.getFrom().getId())) {
                            sendMsg(message, "Ты уже голосовал");
                        } else {
                            muteActivators[counter] = message.getFrom().getId();
                            counter++;
                            sendMsg(message, "Засчитал. " + counter + "/5");
                        }

                    }
                    if (counter == 5) {
                        sendMsg(message, nameOfMutedUser + " Отправляется в мут на 15 минут.");
                        muteUser(message, idOfMutedUser);
                        counter = 0;
                        muteActivators[0] = 0;
                        muteActivators[1] = 0;
                        muteActivators[2] = 0;
                        muteActivators[3] = 0;
                        muteActivators[4] = 0;
                    }
                } else if ("/voteban".equalsIgnoreCase(text) || "/voteban@yxrix_bot".equalsIgnoreCase(text)) {
                    if (counter == 0) {
                        sendMsg(message, "Ответь этой командой на сообщение того, кого хочешь забанить нахуй");
                    } else {
                        if (isVoted(message.getFrom().getId())) {
                            sendMsg(message, "Ты уже голосовал");
                        } else {
                            muteActivators[counter] = message.getFrom().getId();
                            counter++;
                            sendMsg(message, "Засчитал. " + counter + "/5");
                        }

                    }
                    if (counter == 5) {
                        sendMsg(message, nameOfMutedUser + " Отправляется в мут на 15 минут.");
                        counter = 0;
                        muteActivators[0] = 0;
                        muteActivators[1] = 0;
                        muteActivators[2] = 0;
                        muteActivators[3] = 0;
                        muteActivators[4] = 0;
                        muteUser(message, idOfMutedUser);
                    }
                }
                if ("/yesno".equalsIgnoreCase(text) || "/yesno@yxrix_bot".equalsIgnoreCase(text)) {
                    sendMsg(message, yesNo());
                }
                if ("/russian_roulette".equalsIgnoreCase(text) || "/russian_roulette@yxrix_bot".equalsIgnoreCase(text)) {
                    russianRoulette(message);
                    SendMessage sendMessage = new SendMessage(); // Create a message object object
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setText("Старт Русской рулетки. \n Хочешь поучавствовать в розыгрыше бана на рандомное количество времени?");
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList();
                    List<InlineKeyboardButton> rowInline = new ArrayList();
                    rowInline.add(new InlineKeyboardButton().setText("Принять участие").setCallbackData("take_rr"));
                    // Set the keyboard to the markup
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    sendMessage.setReplyMarkup(markupInline);
                    try {
                        execute(sendMessage); // Sending our message object to user
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }


                //else if(message.getFrom().getId().equals(407449415)){
                // sendMsg(message, "Ладно не будем спорить на счет российского языка");
                // }
            }
        }else if(update.hasCallbackQuery()){
            CallbackQuery callbackQuery = update.getCallbackQuery();

            if (callbackQuery.getData().equals("take_rr")) {
//                russianRoulette(callbackQuery.getMessage());
                String answer = "Updated message text";
                EditMessageText new_message = new EditMessageText()
                        .setChatId(callbackQuery.getMessage().getChatId())
                        .setMessageId(callbackQuery.getMessage().getMessageId())
                        .setText(answer);
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    ArrayList<Integer> spamMesages = new ArrayList<Integer>();
    public void spamDelete(Long chatId, ArrayList spam){

        for (int i=0;i<spam.size();i++){
            Integer tmp = (Integer) spam.get(i);
            delMsg(chatId, tmp);
            delMsg(chatId, tmp + 1);
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

    ArrayList<ArrayList> russianRoulettePlayers = new ArrayList(); //Массив содержащий массивы игроков
    public void russianRoulette(Message message){

        if (russianRoulettePlayers.size() != 0){ //проверка, пустой ли массив массивов игроков
            for (int i = 0; i < russianRoulettePlayers.size(); i++ ){
                if (russianRoulettePlayers.get(i).get(0) == message.getChatId()){ //проверка, есть ли в массиве массив игроков того чата, из которого пришло сообщение
                    if(russianRoulettePlayers.get(i).contains(message.getFrom().getId())){ // проверка на наличие игрока в массиве его чата
                        // игрок уже состоит в игре
                    }else{
                        russianRoulettePlayers.get(i).add(message.getFrom().getId());  // Добавляем игрока в массив его чата
                    }

                }else {
                    russianRoulettePlayers.add(new ArrayList());
                    russianRoulettePlayers.get(russianRoulettePlayers.size()).add(message.getChatId()); // Регистрируем чат в игре. Добавляем массив игроков того чата, откуда поступила команда
                    russianRoulettePlayers.get(russianRoulettePlayers.size()).add(message.getFrom().getId()); //Добавляем игрока в массив его чата
                }
            }
        } else{
            russianRoulettePlayers.add(new ArrayList());
            russianRoulettePlayers.get(0).add(message.getChatId()); // Регистрируем чат в игре. Добавляем массив игроков того чата, откуда поступила команда
            russianRoulettePlayers.get(0).add(message.getFrom().getId());//Добавляем игрока в массив его чата
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
        if (user.getId() == 138368804) return false;
        lastUserArray[0]=lastUserArray[1];
        lastUserArray[1]=lastUserArray[2];
        lastUserArray[2]=lastUserArray[3];
        lastUserArray[3]=lastUserArray[4];
        lastUserArray[4]=user.getId();



        if(lastUserArray[1] == user.getId() && lastUserArray[2] == user.getId() && lastUserArray[3] == user.getId() && lastUserArray[4] == user.getId() && lastUserArray[0] == user.getId()){
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

    public String randomStavrSay(){
        Random random = new Random();
        switch (random.nextInt(5)) {
            case 0:
                return "Айййй, доля воровская";
            case 1:
                return "Your choise";
            case 2:
                return "Дурня";
            case 3:
                return "Ставiiiiнський";
            case 4:
                return "Аааа, Капеля́нович";


            default: return "Случилась неведомая хуйня, выпал эксепшон";
        }
    }

    public String randomTerehaSay(){
        Random random = new Random();
        switch (random.nextInt(5)) {
            case 0:
                return "ЕСД";
            case 1:
                return "Ебанутый старый дед";
            case 2:
                return "Мошнила тот ещё";
            case 3:
                return "Ваш гонор и ваши амбиции это ваши проблемы";
            case 4:
                return "Отправьте бланки новой почтой";


            default: return "Случилась неведомая хуйня, выпал эксепшон";
        }
    }

    public void pressFSticker(Message message){
        Random random = new Random();
        GetStickerSet getStickerSet = new GetStickerSet("FforRespect");
        StickerSet stickerSet = new StickerSet();
        try {
            stickerSet = execute(getStickerSet);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        String sticker = stickerSet.getStickers().get(random.nextInt(stickerSet.getStickers().size())).getFileId();
        sendSticker(message, sticker);
    }



    config config = new config();
    public String getBotUsername() {

        return config.getUsername();
    }

    public String getBotToken() {
        return config.getKey();
    }
}
