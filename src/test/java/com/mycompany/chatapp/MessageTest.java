package com.mycompany.chatapp;

import java.io.File;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Student
 */
public class MessageTest {

    private Message message1 = new Message("0012345678", 0, "+27718693002",
            "Hi Mike, can you join us for dinner tonight?");
    private Message message2 = new Message("0087654321", 1, "08575975889",
            "Hi Keegan, did you receive the payment?");

    private Message msgCake = new Message("0011111111", 0, "+27834557896",
            "Did you get the cake?");
    private Message msgLate = new Message("0022222222", 1, "+27838884567",
            "Where are you? You are late! I have asked you to be on time.");
    private Message msgGate = new Message("0033333333", 2, "+27834484567",
            "Yohoooo, I am at your gate.");
    private Message msgDinner = new Message("0838884567", 3, "+27831234567",
            "It is dinner time!");
    private Message msgLeaving = new Message("0055555555", 4, "+27838884567",
            "Ok, I am leaving without you.");

    private MessageStore store = new MessageStore();

    private void fillStore() {
        store.addMessage(msgCake, "Sent");
        store.addMessage(msgLate, "Stored");
        store.addMessage(msgGate, "Disregard");
        store.addMessage(msgDinner, "Sent");
        store.addMessage(msgLeaving, "Stored");
    }

    @Test
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.", message1.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        String longText = "";
        for (int i = 0; i < 260; i++) {
            longText = longText + "a";
        }
        Message longMessage = new Message("0012345678", 0, "+27718693002", longText);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                longMessage.checkMessageLength());
    }

    @Test
    public void testRecipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.", message1.checkRecipientCell());
    }

    @Test
    public void testRecipientIncorrectlyFormatted() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.",
                message2.checkRecipientCell());
    }

    @Test
    public void testMessageHash() {
        assertEquals("00:0:HITONIGHT", message1.createMessageHash());
    }

    @Test
    public void testMessageHashLoop() {
        String[] messages = {
            "Hi Mike, can you join us for dinner tonight?",
            "Hi Keegan, did you receive the payment?",
            "Hi thanks"
        };
        String[] expectedHashes = {
            "00:0:HITONIGHT",
            "00:1:HIPAYMENT",
            "00:2:HITHANKS"
        };

        for (int i = 0; i < messages.length; i++) {
            Message m = new Message("0012345678", i, "+27718693002", messages[i]);
            assertEquals(expectedHashes[i], m.createMessageHash());
        }
    }

    @Test
    public void testMessageIDCreated() {
        String id = Message.generateMessageID();
        Message m = new Message(id, 0, "+27718693002", "Hello there");
        assertEquals("Message ID generated: " + id, m.displayMessageID());
        assertEquals(10, id.length());
        assertTrue(m.checkMessageID());
    }

    @Test
    public void testSendMessage() {
        assertEquals("Message successfully sent.", message1.sentMessage(1));
    }

    @Test
    public void testDisregardMessage() {
        assertEquals("Press 0 to delete the message.", message2.sentMessage(2));
    }

    @Test
    public void testStoreMessage() {
        Message.setJsonFile("test-messages.json"); 
        assertEquals("Message successfully stored.", message1.sentMessage(3));
        new File("test-messages.json").delete();   
        Message.setJsonFile("messages.json");
    }

    @Test
    public void testTotalMessagesSent() {
        int before = Message.returnTotalMessages();
        message1.sentMessage(1); 
        message2.sentMessage(2);  
        assertEquals(before + 1, Message.returnTotalMessages());
    }

    @Test
    public void testSentMessagesArrayPopulated() {
        fillStore();
        assertEquals("Did you get the cake?", store.getSentMessages().get(0).getMessageText());
        assertEquals("It is dinner time!", store.getSentMessages().get(1).getMessageText());
    }

    @Test
    public void testDisregardedMessagesArrayPopulated() {
        fillStore();
        assertEquals("Yohoooo, I am at your gate.",
                store.getDisregardedMessages().get(0).getMessageText());
    }

    @Test
    public void testHashAndIDArraysPopulated() {
        fillStore();
        assertEquals(5, store.getMessageHashes().size());
        assertEquals("00:0:DIDCAKE", store.getMessageHashes().get(0));
        assertEquals("00:1:WHERETIME", store.getMessageHashes().get(1));
        assertEquals("0838884567", store.getMessageIDs().get(3));
    }

    @Test
    public void testLongestMessage() {
        fillStore();
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                store.longestStoredMessage());
    }

    @Test
    public void testSearchByMessageID() {
        fillStore();
        assertEquals("It is dinner time!", store.findMessageByID("0838884567").getMessageText());
    }

    @Test
    public void testSearchByRecipient() {
        fillStore();
        ArrayList<String> results = store.searchByRecipient("+27838884567");
        assertEquals("Where are you? You are late! I have asked you to be on time.", results.get(0));
        assertEquals("Ok, I am leaving without you.", results.get(1));
    }

    @Test
    public void testDeleteByHash() {
        fillStore();
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" "
                + "successfully deleted.", store.deleteByHash("00:1:WHERETIME"));
        assertEquals(1, store.getStoredMessages().size());
    }

    @Test
    public void testDisplayReport() {
        fillStore();
        String expected = "Message ID: 0011111111\n"
                + "Message Hash: 00:0:DIDCAKE\n"
                + "Recipient: +27834557896\n"
                + "Message: Did you get the cake?\n\n"
                + "Message ID: 0838884567\n"
                + "Message Hash: 08:3:ITTIME\n"
                + "Recipient: +27831234567\n"
                + "Message: It is dinner time!\n\n";
        assertEquals(expected, store.displayReport(store.getSentMessages()));
    }

    @Test
    public void testReadMessageFromJson() {
        Message readBack = MessageStore.messageFromJson(msgLate.toJson());
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                readBack.getMessageText());
        assertEquals("+27838884567", readBack.getRecipient());
        assertEquals("00:1:WHERETIME", readBack.createMessageHash());
    }
}