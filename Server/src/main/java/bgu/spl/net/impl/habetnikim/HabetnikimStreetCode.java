package bgu.spl.net.impl.habetnikim;
import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HabetnikimStreetCode implements MessageEncoderDecoder<String> {
    private byte[] bytes = new byte[1 << 10];
    int len=0;
    String msgData;
    short opcode;
    @Override
    public String decodeNextByte(byte nextByte) {
        if(nextByte==';') {
            msgData = new String(bytes, 2, len);
            return opcode+'\0'+msgData;
        }
        if(len==3){
            opcode=bytesToShort(bytes);
        }
        pushByte(nextByte);
        len++;
        return null;
    }

    @Override
    public byte[] encode(String message) { /////*******************
        String opCode="";
        int pos=0;
        for(int i=0;i<message.length();i++){
            if(message.charAt(i)==';'){
                opCode=message.substring(0,i);
                pos=i;
                break;
            }
        }
        byte[] bOpCode=shortToBytes(Short.valueOf(opCode));
        byte[] msg=(message.substring(pos+1).getBytes(StandardCharsets.UTF_8));//assuming message has \0 in right places
        byte[]encodedMsg=new byte[bOpCode.length+msg.length];
        for(int i=0;i<encodedMsg.length;i++){
            if(i<bOpCode.length)
                encodedMsg[i]=bOpCode[i];
            else encodedMsg[i]=msg[i-2]; // -2? maybe we should change that
        }
        return encodedMsg;
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
