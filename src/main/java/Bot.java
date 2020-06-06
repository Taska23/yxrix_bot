import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

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

    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();


        if(message != null && message.hasText()){
            String text = message.getText();
            System.out.println(message);



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
            }//else if(message.getFrom().getId().equals(407449415) && text.contains("?")){
               // sendMsg(message, "Вася, ну йоханый манус, блять!");
            //}
        }

    }

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
