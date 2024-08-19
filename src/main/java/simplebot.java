import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

public class simplebot extends TelegramLongPollingBot {
    private boolean echoMode = false;
    private boolean calculationMode = false;
    private String selectedOperation = "";

    @Override
    public String getBotUsername() {
        return "a12b2b2_bot";
    }

    @Override
    public String getBotToken() {
        return "7183647995:AAHugg4mwrOo6XE7bbdMtl9gCT3oDS1cHOg";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleIncomingMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery().getData(), update.getCallbackQuery().getMessage().getChatId());
        }
    }

    private void handleIncomingMessage(Message message) {
        if (message.hasText()) {
            String text = message.getText();
            long chatId = message.getChatId();
            SendMessage reply = new SendMessage();
            reply.setChatId(String.valueOf(chatId));

            if (text.equals("/start")) {
                reply.setText("Welcome! Please choose an option:");

                // Setup custom keyboard
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> keyboardRowList = new ArrayList<>();

                KeyboardRow keyboardRow1 = new KeyboardRow();
                KeyboardButton keyboardButton1 = new KeyboardButton("Option 1");
                keyboardRow1.add(keyboardButton1);

                KeyboardRow keyboardRow2 = new KeyboardRow();
                KeyboardButton keyboardButton2 = new KeyboardButton("Option 2");
                keyboardRow2.add(keyboardButton2);

                keyboardRowList.add(keyboardRow1);
                keyboardRowList.add(keyboardRow2);

                replyKeyboardMarkup.setKeyboard(keyboardRowList);
                replyKeyboardMarkup.setResizeKeyboard(true);
                reply.setReplyMarkup(replyKeyboardMarkup);

            } else if (text.equals("Option 1")) {
                echoMode = true;
                calculationMode = false;
                reply.setText("Echo mode activated. I will repeat everything you say.");
                sendStopEchoButton(chatId);

                try {
                    execute(reply);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                return; // Skip sending another message after this

            } else if (text.equals("Option 2")) {
                echoMode = false;
                calculationMode = true;
                reply.setText("Calculation mode activated. Choose an operation:");

                // Send buttons for operations
                sendCalculationOptions(chatId);

                return; // Skip sending another message after this

            } else if (text.equalsIgnoreCase("bye")) {
                reply.setText("Goodbye!");

            } else if (calculationMode) {
                if (!selectedOperation.isEmpty()) {
                    String result = evaluateExpression(text, selectedOperation);
                    reply.setText(result);
                    calculationMode = false; // Exit calculation mode after processing
                    selectedOperation = ""; // Clear selected operation

                    // Send calculation options again after showing the result
                    sendCalculationOptions(chatId);
                } else {
                    reply.setText("Please choose an operation first.");
                }

            } else {
                if (echoMode) {
                    reply.setText(text);  // Echo the user's message

                    try {
                        execute(reply);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    // After echoing the message, show the "Stop Echo" button
                    sendStopEchoButton(chatId);

                    return;
                } else {
                    reply.setText("Please choose an option using the keyboard.");
                }
            }

            try {
                execute(reply);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCallbackQuery(String callbackData, long chatId) {
        SendMessage callbackReply = new SendMessage();
        callbackReply.setChatId(String.valueOf(chatId));

        if (callbackData.equals("Stop")) {
            echoMode = false;
            calculationMode = false;
            selectedOperation = "";
            callbackReply.setText("Echo mode deactivated.");
        } else if (callbackData.equals("Add") || callbackData.equals("Subtract") || callbackData.equals("Multiply") || callbackData.equals("Divide")) {
            selectedOperation = callbackData;
            callbackReply.setText("Please enter two numbers separated by space (e.g., 5 3).");
        }

        try {
            execute(callbackReply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendStopEchoButton(long chatId) {
        SendMessage stopEchoMessage = new SendMessage();
        stopEchoMessage.setChatId(String.valueOf(chatId));
        stopEchoMessage.setText("Click the button below to stop mode:");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton stopEchoButton = new InlineKeyboardButton();
        stopEchoButton.setText("Stop");
        stopEchoButton.setCallbackData("Stop");
        rowInline.add(stopEchoButton);
        rowsInline.add(rowInline);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        stopEchoMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(stopEchoMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendCalculationOptions(long chatId) {
        SendMessage calculationOptionsMessage = new SendMessage();
        calculationOptionsMessage.setChatId(String.valueOf(chatId));
        calculationOptionsMessage.setText("Choose an operation:");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Add buttons for operations
        rowsInline.add(createInlineKeyboardButton("Add", "Add"));
        rowsInline.add(createInlineKeyboardButton("Subtract", "Subtract"));
        rowsInline.add(createInlineKeyboardButton("Multiply", "Multiply"));
        rowsInline.add(createInlineKeyboardButton("Divide", "Divide"));

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        calculationOptionsMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(calculationOptionsMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<InlineKeyboardButton> createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        return row;
    }

    private String evaluateExpression(String expression, String operation) {
        try {
            // Split the expression into two numbers
            String[] parts = expression.split(" ");
            if (parts.length != 2) {
                return "Please enter two numbers separated by space.";
            }
            double num1 = Double.parseDouble(parts[0]);
            double num2 = Double.parseDouble(parts[1]);
            double result;

            // Perform the calculation based on the selected operation
            switch (operation) {
                case "Add":
                    result = num1 + num2;
                    break;
                case "Subtract":
                    result = num1 - num2;
                    break;
                case "Multiply":
                    result = num1 * num2;
                    break;
                case "Divide":
                    if (num2 == 0) {
                        return "Division by zero is not allowed.";
                    }
                    result = num1 / num2;
                    break;
                default:
                    return "Unknown operation.";
            }

            return "Result: " + result;
        } catch (NumberFormatException e) {
            return "Invalid number format.";
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new simplebot());
            System.out.println("Bot started successfully!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
