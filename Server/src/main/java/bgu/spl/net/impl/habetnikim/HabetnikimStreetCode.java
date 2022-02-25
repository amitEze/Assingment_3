package bgu.spl.net.impl.habetnikim;
import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HabetnikimStreetCode implements MessageEncoderDecoder<List<String>> {
    private byte[] bytes = new byte[1 << 10];
    int len=0;
    String msgData;
    short opcode;
    @Override
    public List<String> decodeNextByte(byte nextByte) {
        System.out.println("received " + (int) nextByte);
        if(nextByte==';') {
            System.out.println("Encoderdecoder: " );
            msgData = new String(bytes, 2, len);
            len=0;
            List<String> sofisofi = new LinkedList<String>();
            sofisofi.add(String.valueOf(opcode)+'\0'+msgData);
            System.out.println("New message: "+msgData+" bytes array :"+Arrays.toString(bytes));
            return sofisofi;
        }
        pushByte(nextByte);
        if(len==2){
            opcode=bytesToShort(bytes);
        }
        return null;
    }

    @Override
    public byte[] encode(List<String> message) { /////*******************
        String opCode=message.remove(0);
        String ackOpCode;
        switch (opCode){
            case "09": {//****************NEED TO ADD THE END MESSAGE SIGN!! ";" IN BYTES!!
                byte[] data = message.remove(0).getBytes(StandardCharsets.UTF_8);
                byte[] aOpCode = shortToBytes(Short.valueOf(opCode));
                byte[] encodedMsg = new byte[2 + data.length];
                for (int k = 0; k < encodedMsg.length; k++) {
                    if (k < 2) {
                        encodedMsg[k] = aOpCode[k];
                    } else {
                        encodedMsg[k] = data[k - 2];
                    }
                }
                break;
            }
            case "10": {
                ackOpCode = message.remove(0);
                if (ackOpCode.compareTo("04") == 0 || ackOpCode.compareTo("07") == 0 || ackOpCode.compareTo("08") == 0) {
                    System.out.println(ackOpCode);
                    byte[] bOpCode = shortToBytes(Short.valueOf(ackOpCode));
                    byte[] aOpCode = shortToBytes(Short.valueOf(opCode));
                    if (ackOpCode.compareTo("04") == 0) {
                        byte[] userName = (message.remove(0)+";").getBytes(StandardCharsets.UTF_8);
                        byte[] encodedMsg = new byte[4 + userName.length];
                        for (int i = 0; i < 4 + userName.length; i++) {
                            if (i < 2)
                                encodedMsg[i] = aOpCode[i];
                            else if (i < 4) {
                                encodedMsg[i] = bOpCode[i - 2];
                            } else {
                                encodedMsg[i] = userName[i - 4];
                            }
                        }
                        System.out.println(Arrays.toString(encodedMsg));
                        return encodedMsg;
                    }
                    if (ackOpCode.compareTo("07") == 0 || ackOpCode.compareTo("08") == 0) {//NEED TO ADD ENDLINE!!
                        String info = message.remove(0);
                        String[] infos = info.split(" ");
                        byte[] infpot = new byte[8];
                        int j = 0;
                        for (String s : infos) {
                            byte[] inBytes = shortToBytes(Short.valueOf(s));
                            infpot[j] = inBytes[0];
                            j++;
                            infpot[j] = inBytes[1];
                            j++;
                        }
                        byte[] encodedMsg = new byte[12];
                        for (int i = 0; i < 4 + infpot.length; i++) {
                            if (i < 2)
                                encodedMsg[i] = aOpCode[i];
                            else if (i < 4) {
                                encodedMsg[i] = bOpCode[i - 2];
                            } else {
                                encodedMsg[i] = infpot[i - 4];
                            }
                            return encodedMsg;
                        }
                    }
                } else {//regular ack case
                    byte[] bOpCode = shortToBytes(Short.valueOf(ackOpCode));
                    byte[] aOpCode = shortToBytes(Short.valueOf(opCode));
                    byte[] endline = ";".getBytes(StandardCharsets.UTF_8);
                    byte[] encodedMsg = new byte[4+endline.length];
                    for (int i = 0; i < 4+endline.length; i++) {
                        if (i < 2)
                            encodedMsg[i] = aOpCode[i];
                        else{
                            if(i<4){
                            encodedMsg[i] = bOpCode[i - 2];
                            } else{
                                encodedMsg[i]=endline[i-4];
                            }
                        }
                    }
                    System.out.println(Arrays.toString(encodedMsg));
                    return encodedMsg;
                }
                break;
            }
            case "11": {
                //System.out.println(Arrays.toString(message.toArray()));
                ackOpCode = message.remove(0);
                byte[] bOpCode = shortToBytes(Short.valueOf(ackOpCode));
                byte[] aOpCode = shortToBytes(Short.valueOf(opCode));
                byte[] endline = ";".getBytes(StandardCharsets.UTF_8);
                byte[] encodedMsg = new byte[4+endline.length];
                for (int i = 0; i < 4+endline.length; i++) {
                    if (i < 2)
                        encodedMsg[i] = aOpCode[i];
                    else{
                        if(i<4){
                            encodedMsg[i] = bOpCode[i - 2];
                        } else{
                            encodedMsg[i]=endline[i-4];
                        }
                    }
                }
                /*for (int i = 0; i < 4; i++) {
                    if (i < 2)
                        encodedMsg[i] = aOpCode[i];
                    else
                        encodedMsg[i] = bOpCode[i - 2];
                }*/
                System.out.println(Arrays.toString(encodedMsg));
                return encodedMsg;
            }
        }
        return null;

    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

}
